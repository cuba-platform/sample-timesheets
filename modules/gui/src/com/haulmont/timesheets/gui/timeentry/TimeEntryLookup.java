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

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.gui.util.ComponentsHelper;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gorelov
 */
public class TimeEntryLookup extends AbstractLookup {

    @Inject
    protected Table<TimeEntry> timeEntriesTable;

    @Override
    public void init(final Map<String, Object> params) {
        timeEntriesTable.setStyleProvider(new Table.StyleProvider<TimeEntry>() {
            @Nullable
            @Override
            public String getStyleName(TimeEntry entity, String property) {
                if ("status".equals(property)) {
                    return ComponentsHelper.getTimeEntryStatusStyle(entity);
                }
                return null;
            }
        });

        timeEntriesTable.addAction(new CreateAction(timeEntriesTable) {
            @Override
            public Map<String, Object> getInitialValues() {
                Map<String, Object> initialValues = new HashMap<>();
                initialValues.put("task", params.get("task"));
                initialValues.put("user", params.get("user"));
                initialValues.put("date", params.get("date"));
                return initialValues;
            }
        });
    }
}