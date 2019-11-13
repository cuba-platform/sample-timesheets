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

import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.gui.util.ScreensHelper;
import com.haulmont.timesheets.gui.util.SecurityAssistant;

import javax.inject.Inject;

/**
 * @author gorelov
 */
@UiController("ts$TimeEntry.all")
@UiDescriptor("timeentry-all.xml")
public class AllTimeEntries extends StandardLookup<TimeEntry> {

    @Inject
    protected SecurityAssistant securityAssistant;
    @Inject
    protected UserSession userSession;

    @Inject
    protected CollectionLoader<TimeEntry> timeEntriesDl;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected GroupTable<TimeEntry> timeEntriesTable;

    @Subscribe
    protected void onInit(Screen.InitEvent e) {
        if (securityAssistant.isSuperUser()) {
            timeEntriesDl.setQuery("select e from ts$TimeEntry e");
        }
    }

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
    protected String timeEntriesTableStyleProvider(TimeEntry entity, String property) {
        if ("status".equals(property)) {
            if (entity != null) {
                return ScreensHelper.getTimeEntryStatusStyle(entity);
            }
        }
        return null;
    }

    @Subscribe
    protected void onBeforeShow(Screen.BeforeShowEvent e) {
        timeEntriesDl.setParameter("user", userSession.getUser());
        timeEntriesDl.load();
    }
}