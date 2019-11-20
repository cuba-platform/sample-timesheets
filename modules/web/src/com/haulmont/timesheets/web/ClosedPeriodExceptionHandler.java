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

package com.haulmont.timesheets.web;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.exception.AbstractUiExceptionHandler;
import com.haulmont.cuba.web.App;
import com.haulmont.cuba.web.Connection;
import com.haulmont.timesheets.exception.ClosedPeriodException;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * @author degtyarjov
 * @version $Id$
 */
public class ClosedPeriodExceptionHandler extends AbstractUiExceptionHandler {
    private Locale locale;

    public ClosedPeriodExceptionHandler() {
        super(ClosedPeriodException.class.getName());

        Connection connection = App.getInstance().getConnection();
        if (connection.getSession() != null) {
            locale = connection.getSession().getLocale();
        }
    }

    @Override
    protected void doHandle(String className, String message, @Nullable Throwable throwable, UiContext context) {
        Notifications notifications = context.getNotifications();
        Messages messages = AppBeans.get(Messages.NAME);
        notifications.create(Notifications.NotificationType.WARNING)
                .withCaption(messages.getMessage(App.class, "exception.closedPeriod", locale))
                .show();
    }
}
