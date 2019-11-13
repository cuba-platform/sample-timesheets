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

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.gui.util.ScreensHelper;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * @author gorelov
 */
@UiController("ts$TimeEntry.lookup")
@UiDescriptor("timeentry-lookup.xml")
@LookupComponent("timeEntriesTable")
public class TimeEntryLookup extends StandardLookup<TimeEntry> {

    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;

    @Inject
    protected Table<TimeEntry> timeEntriesTable;
    @Inject
    protected CollectionLoader<TimeEntry> timeEntriesDl;

    protected Task task;
    protected User user;
    protected Date date;

    public void setTask(Task task) {
        this.task = task;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Install(to = "timeEntriesTable", subject = "styleProvider")
    protected String timeEntriesTableStyleProvider(TimeEntry entity, String property) {
        if ("status".equals(property)) {
            return ScreensHelper.getTimeEntryStatusStyle(entity);
        }
        return null;
    }

    @Install(to = "timeEntriesDl", target = Target.DATA_LOADER)
    protected List<TimeEntry> timeEntriesDlLoadDelegate(LoadContext<TimeEntry> context) {
        context.getQuery().setParameter("task", task);
        context.getQuery().setParameter("user", user);
        context.getQuery().setParameter("date", date);
        return dataManager.loadList(context);
    }


    @Subscribe("timeEntriesTable.create")
    protected void onTimeEntriesTableCreateActionPerformed(Action.ActionPerformedEvent e) {
        TimeEntry newEntity = metadata.create(TimeEntry.class);
        newEntity.setTask(task);
        newEntity.setUser(user);
        newEntity.setDate(date);
        screenBuilders.editor(timeEntriesTable)
                .newEntity(newEntity)
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent e) {
        timeEntriesDl.load();
    }
}
