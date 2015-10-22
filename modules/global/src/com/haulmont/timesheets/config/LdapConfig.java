/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;

@Source(type = SourceType.APP)
public interface LdapConfig extends Config {
    @Property("ldap.auth")
    @DefaultBoolean(false)
    boolean getLdapAuth();

    @Property("ldap.createuser")
    @DefaultBoolean(false)
    boolean getLdapCreateUser();

    @Property("ldap.updateuser")
    @DefaultBoolean(false)
    boolean getLdapUpdateUser();

}
