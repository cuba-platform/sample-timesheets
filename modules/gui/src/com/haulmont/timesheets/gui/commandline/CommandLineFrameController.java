
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
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.service.CommandLineService;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * @author degtyarjov
 */
@UiController("command-line-frame")
@UiDescriptor("command-line-frame.xml")
public class CommandLineFrameController extends ScreenFragment {

    protected CommandLine commandLine;

    @Inject
    protected UiComponents uiComponents;
    @Inject
    protected BoxLayout commandLineHBox;
    @Inject
    protected CommandLineService commandLineService;
    @Inject
    protected Notifications notifications;
    @Inject
    protected MessageBundle messageBundle;

    protected ResultTimeEntriesHandler timeEntriesHandler;

    @Subscribe("apply")
    protected void onApplyClick(Button.ClickEvent event) {
        apply();
    }

    @Subscribe
    protected void onInit(InitEvent event) {
        commandLine = uiComponents.create(CommandLine.class);
        commandLine.setWidth("800px");
        commandLine.setHeight("30px");

        commandLineHBox.add(commandLine, 0);

        commandLine.setShowGutter(false);
        commandLine.setShowPrintMargin(false);
        commandLine.setHighlightActiveLine(false);
        commandLine.setSuggester(new CommandLineSuggester(commandLine));
        commandLine.setApplyHandler(this::apply);

        commandLine.focus();
    }

    public void apply() {
        if (timeEntriesHandler != null) {
            try {
                List<TimeEntry> timeEntries =
                        commandLineService.createTimeEntriesForTheCommandLine(String.valueOf(commandLine.getValue()));
                if (CollectionUtils.isEmpty(timeEntries)) {
                    notifications.create(Notifications.NotificationType.HUMANIZED)
                            .withCaption(messageBundle.getMessage("notification.emptyCommandResult"))
                            .show();
                }
                timeEntriesHandler.handle(timeEntries != null ? timeEntries : Collections.<TimeEntry>emptyList());
            } catch (Exception e) {
                notifications.create(Notifications.NotificationType.WARNING)
                        .withCaption(messageBundle.getMessage("error.commandLine"))
                        .show();
            }
        } else {
            throw new GuiDevelopmentException("ResultTimeEntriesHandler is not set for CommandLineFrameController", getId());
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
