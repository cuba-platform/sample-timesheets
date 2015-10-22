/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web;

import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.web.AppUI;
import com.haulmont.cuba.web.LoginWindow;
import com.haulmont.timesheets.EncryptDecrypt;

import java.util.Locale;

/**
 * @author shakhov
 * @version $Id$
 */
public class TimesheetsLoginWindow extends LoginWindow {

    public TimesheetsLoginWindow(AppUI ui) {
        super(ui);
    }

    @Override
    protected void login(String login, String password, Locale locale) throws LoginException {
        connection.login(login, new EncryptDecrypt(login).encrypt(passwordField.getValue()), locale);
    }

    @Override
    protected void loginByRememberMe(String login, String rememberMeToken, Locale locale) throws LoginException {
        //FIXME: security issue - if you change user's password or disable user in ldap, you will still be able to login
        super.loginByRememberMe(login, rememberMeToken, locale);
    }

}
