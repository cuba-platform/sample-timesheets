/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui.xml.layout.loaders;

import com.haulmont.cuba.gui.xml.layout.loaders.AbstractComponentLoader;
import com.haulmont.timesheets.gui.commandline.CommandLine;

public class CommandLineLoader extends AbstractComponentLoader<CommandLine> {

    @Override
    public void createComponent() {
        resultComponent = factory.create(CommandLine.class);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {}
}
