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

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.ExcelAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.gui.util.ComponentsHelper;
import com.haulmont.timesheets.gui.util.SecurityAssistant;
import com.haulmont.timesheets.service.ProjectsService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

/**
 * @author gorelov
 */
public class AllTimeEntries extends AbstractLookup {
    @Inject
    protected GroupTable<TimeEntry> timeEntriesTable;
    @Named("timeEntriesTable.edit")
    protected EditAction timeEntriesTableEdit;
    @Named("timeEntriesTable.create")
    protected CreateAction timeEntriesTableCreate;
    @Inject
    protected SecurityAssistant securityAssistant;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected GroupDatasource<TimeEntry, UUID> timeEntriesDs;

    @Override
    public void init(Map<String, Object> params) {
        if (securityAssistant.isSuperUser()) {
            timeEntriesDs.setQuery("select e from ts$TimeEntry e");
        }

        timeEntriesTableCreate.setOpenType(WindowManager.OpenType.DIALOG);
        timeEntriesTableEdit.setOpenType(WindowManager.OpenType.DIALOG);

        timeEntriesTable.setStyleProvider((entity, property) -> {
            if ("status".equals(property)) {
                if (entity != null) {
                    return ComponentsHelper.getTimeEntryStatusStyle(entity);
                }
            }
            return null;
        });
        timeEntriesTable.addAction(new ExcelAction(timeEntriesTable));
    }
}