/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui.commandline;

import com.haulmont.cuba.gui.GuiDevelopmentException;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.service.CommandLineService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author degtyarjov
 * @version $Id$
 */
public class CommandLineFrameController extends AbstractFrame {
    protected SourceCodeEditor commandLine;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private BoxLayout commandLineHBox;

    @Inject
    private CommandLineService commandLineService;

    protected  ResultTimeEntriesHandler timeEntriesHandler;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        commandLine = componentsFactory.createComponent(CommandLine.NAME);
        commandLine.setWidth("500px");
        commandLine.setHeight("30px");
        commandLineHBox.add(commandLine, 0);

        commandLine.setShowGutter(false);
        commandLine.setShowPrintMargin(false);
        commandLine.setHighlightActiveLine(false);
        commandLine.setSuggester(new CommandLineSuggester(commandLine));
    }

    public void apply(){
        if (timeEntriesHandler != null) {
            List<TimeEntry> timeEntries =
                    commandLineService.createTimeEntriesForTheCommandLine(String.valueOf(commandLine.getValue()));
            timeEntriesHandler.handle(timeEntries != null ? timeEntries : Collections.<TimeEntry>emptyList());
        } else {
            throw new GuiDevelopmentException("ResultTimeEntriesHandler is not set for CommandLineFrameController", getFrame().getId());
        }
    }

    public void setTimeEntriesHandler(ResultTimeEntriesHandler timeEntriesHandler) {
        this.timeEntriesHandler = timeEntriesHandler;
    }

    public static interface ResultTimeEntriesHandler {
        void handle(List<TimeEntry> resultTimeEntries);
    }
}
