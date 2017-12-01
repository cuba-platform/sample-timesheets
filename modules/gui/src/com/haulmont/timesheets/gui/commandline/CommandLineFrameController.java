
/*
 * Copyright (c) 2016 Haulmont
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.timesheets.gui.commandline;

import com.haulmont.cuba.gui.GuiDevelopmentException;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.service.CommandLineService;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author degtyarjov
 */
public class CommandLineFrameController extends AbstractFrame {
    protected CommandLine commandLine;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected BoxLayout commandLineHBox;

    @Inject
    protected CommandLineService commandLineService;

    @Inject
    protected Companion companion;

    protected ResultTimeEntriesHandler timeEntriesHandler;

    public interface Companion {
        void setApplyHandler(CommandLine commandLine, Runnable handler);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        commandLine = componentsFactory.createComponent(CommandLine.class);
        commandLine.setWidth("800px");
        commandLine.setHeight("30px");
        commandLineHBox.add(commandLine, 0);

        commandLine.setShowGutter(false);
        commandLine.setShowPrintMargin(false);
        commandLine.setHighlightActiveLine(false);
        commandLine.setSuggester(new CommandLineSuggester(commandLine));
        companion.setApplyHandler(commandLine, this::apply);
    }

    public void apply() {
        if (timeEntriesHandler != null) {
            try {
                List<TimeEntry> timeEntries =
                        commandLineService.createTimeEntriesForTheCommandLine(commandLine.getValue());
                if (CollectionUtils.isEmpty(timeEntries)) {
                    showNotification(getMessage("notification.emptyCommandResult"), NotificationType.HUMANIZED);
                }
                timeEntriesHandler.handle(timeEntries != null ? timeEntries : Collections.emptyList());
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

    public ResultTimeEntriesHandler getTimeEntriesHandler() {
        return timeEntriesHandler;
    }

    public interface ResultTimeEntriesHandler {
        void handle(List<TimeEntry> resultTimeEntries);
    }
}
