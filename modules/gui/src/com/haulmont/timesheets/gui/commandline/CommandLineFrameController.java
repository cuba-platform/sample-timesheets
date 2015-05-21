/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui.commandline;

import com.haulmont.cuba.gui.GuiDevelopmentException;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.service.CommandLineService;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author degtyarjov
 * @version $Id$
 */
public class CommandLineFrameController extends AbstractFrame {
    @Inject
    protected SourceCodeEditor commandLine;

    @Inject
    protected Button apply;

    @Inject
    private CommandLineService commandLineService;

    protected  ResultTimeEntriesHandler timeEntriesHandler;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        commandLine.setShowGutter(false);
        commandLine.setShowPrintMargin(false);
        commandLine.setHighlightActiveLine(false);
        commandLine.setSuggester(new CommandLineSuggester(commandLine));
        apply.setAction(new AbstractAction("apply") {
            @Override
            public void actionPerform(Component component) {
                if (timeEntriesHandler != null) {
                    List<TimeEntry> timeEntries =
                            commandLineService.createTimeEntriesForTheCommandLine(String.valueOf(commandLine.getValue()));
                    timeEntriesHandler.handle(timeEntries != null ? timeEntries : Collections.<TimeEntry>emptyList());
                } else {
                    throw new GuiDevelopmentException("ResultTimeEntriesHandler is not set for CommandLineFrameController", getFrame().getId());
                }
            }
        });
    }

    public void setTimeEntriesHandler(ResultTimeEntriesHandler timeEntriesHandler) {
        this.timeEntriesHandler = timeEntriesHandler;
    }

    public static interface ResultTimeEntriesHandler {
        void handle(List<TimeEntry> resultTimeEntries);
    }
}
