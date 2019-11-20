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

package com.haulmont.timesheets.gui.timeentry;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.gui.commandline.CommandLineFrameController;
import com.haulmont.timesheets.gui.util.ScreensHelper;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;

/**
 * @author gorelov
 */
@UiController("ts$TimeEntry.browse")
@UiDescriptor("timeentry-my.xml")
@LookupComponent("timeEntriesTable")
public class MyTimeEntries extends StandardLookup<TimeEntry> {

    @Inject
    protected UserSession userSession;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected ScreenBuilders screenBuilders;

    @Inject
    protected CollectionContainer<TimeEntry> timeEntriesDc;
    @Inject
    protected CollectionLoader<TimeEntry> timeEntriesDl;
    @Inject
    protected GroupTable<TimeEntry> timeEntriesTable;
    @Inject
    protected CommandLineFrameController commandLine;

    @Subscribe("timeEntriesTable.create")
    protected void onTimeEntriesTableCreateActionPerformed(Action.ActionPerformedEvent e) {
        screenBuilders.editor(timeEntriesTable)
                .newEntity()
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();
    }

    @Subscribe("timeEntriesTable.edit")
    protected void onTimeEntriesTableEditActionPerformed(Action.ActionPerformedEvent e) {
        screenBuilders.editor(timeEntriesTable)
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();
    }

    @Install(to = "timeEntriesTable", subject = "styleProvider")
    protected String timeEntriesTableStyleProvider(Entity entity, String property) {
        if ("status".equals(property)) {
            TimeEntry timeEntry = (TimeEntry) entity;
            if (timeEntry == null) {
                return null;
            } else {
                return ScreensHelper.getTimeEntryStatusStyle(timeEntry);
            }
        }
        return null;
    }

    @Subscribe
    protected void onInit(InitEvent e) {
        commandLine.setTimeEntriesHandler(resultTimeEntries -> {
            if (CollectionUtils.isNotEmpty(resultTimeEntries)) {
                screenBuilders.editor(timeEntriesTable)
                        .editEntity(resultTimeEntries.get(0))
                        .withScreenClass(TimeEntryEdit.class)
                        .withLaunchMode(OpenMode.DIALOG)
                        .withAfterCloseListener(ace -> {
                            if ("commit".equalsIgnoreCase(((StandardCloseAction) ace.getCloseAction()).getActionId())) {
                                timeEntriesTable.setSelected(ace.getScreen().getEditedEntity());
                            }
                        })
                        .build()
                        .show();
            }
        });
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        timeEntriesDl.setParameter("user", userSession.getUser());
        timeEntriesDl.load();
    }
}