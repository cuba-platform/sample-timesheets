/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui;

import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.security.entity.RoleType;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

/**
 * @author degtyarjov
 * @version $Id$
 */
@ManagedBean(SecurityAssistant.NAME)
public class SecurityAssistant {
    @Inject
    protected UserSessionSource userSessionSource;

    public static final String NAME = "ts_SecurityAssistant";

    public boolean isSuperUser(){
        User user = userSessionSource.getUserSession().getUser();
        if (CollectionUtils.isEmpty(user.getUserRoles())) {
            return true;
        }

        for (UserRole userRole : user.getUserRoles()) {
            if (userRole.getRole().getType() == RoleType.SUPER) {
                return true;
            }
        }

        return false;
    }
}
