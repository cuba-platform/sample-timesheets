/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.timesheets.gui.timeentry;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.ExcelAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.gui.util.ComponentsHelper;
import com.haulmont.timesheets.gui.util.SecurityAssistant;
import com.haulmont.timesheets.service.ProjectsService;

import javax.annotation.Nullable;
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

        timeEntriesTable.setStyleProvider(new Table.StyleProvider() {
            @Nullable
            @Override
            public String getStyleName(Entity entity, String property) {
                if ("status".equals(property)) {
                    TimeEntry timeEntry = (TimeEntry) entity;
                    if (timeEntry != null) {
                        return ComponentsHelper.getTimeEntryStatusStyle(timeEntry);
                    }
                }
                return null;
            }
        });
        timeEntriesTable.addAction(new ExcelAction(timeEntriesTable));
    }
}