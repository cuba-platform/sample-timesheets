package com.haulmont.timesheets.web;

import com.haulmont.charts.web.gui.ChartComponentPalette;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.web.AppUI;
import com.haulmont.cuba.web.DefaultApp;
import com.haulmont.cuba.web.LoginWindow;
import com.haulmont.cuba.web.UIView;
import com.haulmont.cuba.web.gui.WebComponentsFactory;
import com.haulmont.cuba.web.gui.WebUIPaletteManager;
import com.haulmont.timesheets.config.LdapConfig;
import com.haulmont.timesheets.gui.commandline.CommandLine;
import com.haulmont.timesheets.web.commandline.WebCommandLine;

public class App extends DefaultApp {
    static {
        WebComponentsFactory.registerComponent(CommandLine.NAME, WebCommandLine.class);
        WebUIPaletteManager.registerPalettes(new ChartComponentPalette());
    }

    boolean useLdap = AppBeans.get(Configuration.class).getConfig(LdapConfig.class).getLdapAuth();

    @Override
    protected void initExceptionHandlers(boolean isConnected) {
        super.initExceptionHandlers(isConnected);
        exceptionHandlers.addHandler(new ClosedPeriodExceptionHandler());
    }

    @Override
    protected UIView createLoginWindow(AppUI ui) {
        if (useLdap)
            return new TimesheetsLoginWindow(ui);
        else
            return new LoginWindow(ui);
    }
}