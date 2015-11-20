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
