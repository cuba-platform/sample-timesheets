package com.haulmont.timesheets.web;

import com.haulmont.cuba.web.DefaultApp;
import com.haulmont.cuba.web.gui.WebComponentsFactory;
import com.haulmont.timesheets.web.commandline.CommandLine;

public class App extends DefaultApp {
    static {
        WebComponentsFactory.registerComponent(CommandLine.NAME, CommandLine.class);
    }
}