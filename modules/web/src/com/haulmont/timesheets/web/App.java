package com.haulmont.timesheets.web;

import com.haulmont.charts.web.gui.ChartComponentPalette;
import com.haulmont.cuba.web.DefaultApp;
import com.haulmont.cuba.web.gui.WebComponentsFactory;
import com.haulmont.cuba.web.gui.WebUIPaletteManager;
import com.haulmont.timesheets.gui.commandline.CommandLine;
import com.haulmont.timesheets.web.commandline.WebCommandLine;

public class App extends DefaultApp {
    static {
        WebComponentsFactory.registerComponent(CommandLine.NAME, WebCommandLine.class);
        WebUIPaletteManager.registerPalettes(new ChartComponentPalette());
    }
}