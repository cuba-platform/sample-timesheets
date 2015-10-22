/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.web.App;
import com.haulmont.cuba.web.Connection;
import com.haulmont.cuba.web.WebWindowManager;
import com.haulmont.cuba.web.exception.AbstractExceptionHandler;
import com.haulmont.timesheets.exception.ClosedPeriodException;

import java.util.Locale;

/**
 * @author degtyarjov
 * @version $Id$
 */
public class ClosedPeriodExceptionHandler extends AbstractExceptionHandler {
    private Locale locale;

    public ClosedPeriodExceptionHandler() {
        super(ClosedPeriodException.class.getName());

        Connection connection = App.getInstance().getConnection();
        if (connection.getSession() != null) {
            locale = connection.getSession().getLocale();
        }
    }

    @Override
    protected void doHandle(App app, String className, String message, Throwable throwable) {
        WebWindowManager wm = app.getWindowManager();
        Messages messages = AppBeans.get(Messages.NAME);
        wm.showNotification(messages.getMessage(App.class, "exception.closedPeriod", locale), Frame.NotificationType.WARNING);
    }
}
