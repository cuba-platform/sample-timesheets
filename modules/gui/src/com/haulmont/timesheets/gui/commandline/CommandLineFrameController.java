/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui.commandline;

import com.haulmont.cuba.gui.GuiDevelopmentException;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.timesheets.entity.TimeEntry;

import javax.inject.Inject;
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

    protected  ResultTimeEntriesHandler timeEntriesHandler;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        commandLine.setSuggester(new CommandLineSuggester(commandLine));
        apply.setAction(new AbstractAction("apply") {
            @Override
            public void actionPerform(Component component) {
                if (timeEntriesHandler != null) {
                    timeEntriesHandler.handle(null);
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
