/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui.commandline;

import com.haulmont.cuba.gui.GuiDevelopmentException;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.SourceCodeEditor;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.service.CommandLineService;
import org.apache.commons.collections.CollectionUtils;

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

    protected ResultTimeEntriesHandler timeEntriesHandler;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        commandLine = componentsFactory.createComponent(CommandLine.NAME);
        commandLine.setWidth("800px");
        commandLine.setHeight("30px");
        commandLineHBox.add(commandLine, 0);

        commandLine.setShowGutter(false);
        commandLine.setShowPrintMargin(false);
        commandLine.setHighlightActiveLine(false);
        commandLine.setSuggester(new CommandLineSuggester(commandLine));
    }

    public void apply() {
        if (timeEntriesHandler != null) {
            try {
                List<TimeEntry> timeEntries =
                        commandLineService.createTimeEntriesForTheCommandLine(String.valueOf(commandLine.getValue()));
                if (CollectionUtils.isEmpty(timeEntries)) {
                    showNotification(getMessage("notification.emptyCommandResult"), NotificationType.HUMANIZED);
                }
                timeEntriesHandler.handle(timeEntries != null ? timeEntries : Collections.<TimeEntry>emptyList());
            } catch (Exception e) {
                showNotification(getMessage("error.commandLine"), NotificationType.WARNING);
            }
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
