
/*
 * Copyright (c) 2016 Haulmont
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

package com.haulmont.timesheets.gui.util;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.security.entity.RoleType;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import com.haulmont.timesheets.config.TimeSheetsSettings;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author degtyarjov
 */
@Component(SecurityAssistant.NAME)
public class SecurityAssistant {
    @Inject
    protected UserSessionSource userSessionSource;

    public static final String NAME = "ts_SecurityAssistant";

    public boolean isSuperUser() {
        User user = userSessionSource.getUserSession().getCurrentOrSubstitutedUser();
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

    public boolean isUserCloser() {
        User user = userSessionSource.getUserSession().getCurrentOrSubstitutedUser();
        if (CollectionUtils.isEmpty(user.getUserRoles())) {
            return false;
        }
        Configuration configuration = AppBeans.get(Configuration.NAME);
        for (UserRole userRole : user.getUserRoles()) {
            if (userRole.getRole().getId().equals(configuration.getConfig(TimeSheetsSettings.class).getCloserId())) {
                return true;
            }
        }

        return false;
    }
}
