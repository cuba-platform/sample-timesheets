/*
 * Copyright (c) 2015 Haulmont
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.cuba.security.app;

import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.app.DataManagerBean;
import com.haulmont.cuba.core.app.ServerConfig;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.UserRole;
import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.EncryptDecrypt;
import com.haulmont.timesheets.config.LdapConfig;
import com.haulmont.timesheets.config.TimeSheetsSettings;
import com.haulmont.timesheets.config.WorkTimeConfig;
import com.haulmont.timesheets.entity.ExtUser;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LookupAttemptingCallback;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;

import javax.inject.Inject;
import javax.naming.directory.Attributes;
import java.util.*;

public class LdapLoginWorker extends LoginWorkerBean {
    @Inject
    protected Configuration configuration;

    @Inject
    protected Authentication authentication;

    @Inject
    protected LdapTemplate ldapTemplate;

    @Inject
    protected DataManagerBean dataManager;

    @Inject
    protected Metadata metadata;

    @Override
    public UserSession login(String login, String password, Locale locale, Map<String, Object> params) throws LoginException {
        LdapConfig ldapConfig = configuration.getConfig(LdapConfig.class);

        // use standard implementation, if ldap is disabled in config
        if (!ldapConfig.getLdapAuth()) {
            return super.login(login, password, locale, params);
        }

        // attempt to login and get fresh user info from ldap directory
        Map<String, String> userAttributes = ldapAuthenticate(
                login, new EncryptDecrypt(login).decrypt(password), locale
        );
        ExtUser user = null;
        Transaction tx = persistence.createTransaction();
        try {
            user = (ExtUser) loadUser(login);
            tx.commit();
        } finally {
            tx.end();
        }
        if (user == null && ldapConfig.getLdapCreateUser()) {
            // create new user using ldap info
            authentication.begin("admin");
            try {
                createUserFromLdap(login, userAttributes);
            } finally {
                authentication.end();
            }
        } else if (user != null && ldapConfig.getLdapUpdateUser()) {
            // update existing users using ldap info
            authentication.begin("admin");
            try {
                user.setEmail(userAttributes.get("email"));
                user.setFirstName(userAttributes.get("firstName"));
                user.setLastName(userAttributes.get("lastName"));
                CommitContext context = new CommitContext();
                context.getCommitInstances().add(user);
                dataManager.commit(context);
            } finally {
                authentication.end();
            }
        }

        // perform trusted login
        return super.loginTrusted(login, configuration.getConfig(ServerConfig.class).getTrustedClientPassword(), locale);
    }

    protected void createUserFromLdap(String login, Map<String, String> userAttributes) {
        ExtUser user = metadata.create(ExtUser.class);
        CommitContext context = new CommitContext();
        context.getCommitInstances().add(user);
        user.setLogin(login);
        user.setWorkHoursForWeek(configuration.getConfig(WorkTimeConfig.class).getWorkHourForWeek());
        user.setEmail(userAttributes.get("email"));
        user.setFirstName(userAttributes.get("firstName"));
        user.setLastName(userAttributes.get("lastName"));
        Transaction tx = persistence.createTransaction();
        try {
            UUID defaultGroupId = configuration.getConfig(TimeSheetsSettings.class).getDefaultGroupId();
            Group defaultGroup = persistence.getEntityManager().getReference(Group.class, defaultGroupId);
            List<Role> defaultRoles = persistence.getEntityManager()
                    .createQuery("select r from sec$Role r where r.defaultRole = true", Role.class).getResultList();
            List<UserRole> userRoles = new ArrayList<>();
            for (Role defaultRole : defaultRoles) {
                UserRole userRole = metadata.create(UserRole.class);
                userRole.setUser(user);
                userRole.setRole(defaultRole);
                userRoles.add(userRole);
            }
            user.setGroup(defaultGroup);
            user.setUserRoles(userRoles);
            tx.commit();
        } finally {
            tx.end();
        }

        context.getCommitInstances().addAll(user.getUserRoles());
        dataManager.commit(context);
    }


    protected Map<String, String> ldapAuthenticate(String login, String password, Locale locale) throws LoginException {
        if (!ldapTemplate.authenticate(
                DistinguishedName.EMPTY_PATH,
                buildPersonFilter(login),
                password,
                new LookupAttemptingCallback()
        )) {
            throw new LoginException(getInvalidCredentialsMessage(login, locale));
        }

        List result = ldapTemplate.search(DistinguishedName.EMPTY_PATH, buildPersonFilter(login), (Attributes attributes) -> {
            Map<String, String> map = new HashMap<>();
            if (attributes.get("givenname") != null) {
                map.put("firstName", (String) attributes.get("givenname").get());
            }
            if (attributes.get("sn") != null) {
                map.put("lastName", (String) attributes.get("sn").get());
            }
            if (attributes.get("mail") != null) {
                map.put("email", (String) attributes.get("mail").get());
            }
            return map;
        });

        if (result.size() != 1) {
            throw new LoginException(getInvalidCredentialsMessage(login, locale));
        }

        //noinspection unchecked
        return (Map<String, String>) result.get(0);
    }

    protected static String buildPersonFilter(String login) {
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", "person")).and(new EqualsFilter("sAMAccountName", login));
        return filter.encode();
    }

}
