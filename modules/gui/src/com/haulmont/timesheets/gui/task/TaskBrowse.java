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
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractAction;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.gui.util.ComponentsHelper;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Map;

/**
 * @author gorelov
 */
public class TaskBrowse extends AbstractLookup {

    @Inject
    protected Table<Task> tasksTable;
    @Inject
    protected Metadata metadata;

    @Override
    public void init(Map<String, Object> params) {
        tasksTable.setStyleProvider(new Table.StyleProvider<Task>() {
            @Nullable
            @Override
            public String getStyleName(Task entity, @Nullable String property) {
                if ("status".equals(property)) {
                    return ComponentsHelper.getTaskStatusStyle(entity);
                }
                return null;
            }
        });

        tasksTable.addAction(new AbstractAction("createTimeEntry") {
            @Override
            public String getCaption() {
                return getMessage("caption.createTimeEntry");
            }

            @Override
            public void actionPerform(Component component) {
                Task selected = tasksTable.getSingleSelected();
                if (selected != null) {
                    TimeEntry timeEntry = metadata.create(TimeEntry.class);
                    timeEntry.setTask(selected);
                    openEditor("ts$TimeEntry.edit", timeEntry, WindowManager.OpenType.DIALOG);
                }
            }
        });
    }
}