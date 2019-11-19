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

package com.haulmont.timesheets.gui.task;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.gui.data.TasksCollectionLoadDelegate;
import com.haulmont.timesheets.gui.timeentry.TimeEntryEdit;
import com.haulmont.timesheets.gui.util.ScreensHelper;

import javax.inject.Inject;

/**
 * @author gorelov
 */
@UiController("ts$Task.browse")
@UiDescriptor("task-browse.xml")
@LookupComponent("tasksTable")
@LoadDataBeforeShow
public class TaskBrowse extends StandardLookup<Task> {
    @Inject
    protected UserSession userSession;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected Table<Task> tasksTable;
    @Inject
    private Metadata metadata;
    @Inject
    protected CollectionLoader<Task> tasksDl;

    @Subscribe
    public void onInit(InitEvent event) {
        tasksDl.setLoadDelegate(new TasksCollectionLoadDelegate());
    }


    @Install(to = "tasksTable", subject = "styleProvider")
    protected String tasksTableStyleProvider(Task task, String property) {
        if ("status".equals(property)) {
            return ScreensHelper.getTaskStatusStyle(task);
        }
        return null;
    }

    @Subscribe("tasksTable.createTimeEntry")
    protected void onTasksTableCreateTimeEntryActionPerformed(Action.ActionPerformedEvent event) {
        Task selected = tasksTable.getSingleSelected();
        TimeEntry newTimeEntry = metadata.create(TimeEntry.class);
        newTimeEntry.setTask(selected);
        screenBuilders.editor(TimeEntry.class, this)
                .newEntity(newTimeEntry)
                .withScreenClass(TimeEntryEdit.class)
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();

    }
}