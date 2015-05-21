package com.haulmont.timesheets.web;

import com.haulmont.cuba.web.DefaultApp;
import com.haulmont.cuba.web.gui.WebComponentsFactory;
import com.haulmont.timesheets.gui.commandline.CommandLine;
import com.haulmont.timesheets.web.commandline.WebCommandLine;

public class App extends DefaultApp {
    static {
        WebComponentsFactory.registerComponent(CommandLine.NAME, WebCommandLine.class);
    }
}