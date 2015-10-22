/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.commandline;

import com.haulmont.timesheets.gui.commandline.CommandLine;
import com.haulmont.timesheets.gui.commandline.CommandLineFrameController;

/**
 * @author degtyarjov
 * @version $Id$
 */
public class WebCommandLineCompanion implements CommandLineFrameController.Companion {
    @Override
    public void setApplyHandler(CommandLine commandLine, Runnable handler) {
        WebCommandLine webCommandLine = (WebCommandLine) commandLine;
        webCommandLine.getSuggestionExtension().setApplyHandler(handler);
    }
}
