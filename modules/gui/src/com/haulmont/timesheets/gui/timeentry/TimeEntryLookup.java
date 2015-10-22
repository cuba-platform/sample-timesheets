/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui.timeentry
 */
package com.haulmont.timesheets.gui.timeentry;

import com.haulmont.cuba.core.entity.Entity;
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
    protected Table timeEntriesTable;

    @Override
    public void init(final Map<String, Object> params) {
        getDialogParams().setWidth(800);
        getDialogParams().setHeight(500);
        getDialogParams().setResizable(true);

        timeEntriesTable.setStyleProvider(new Table.StyleProvider() {
            @Nullable
            @Override
            public String getStyleName(Entity entity, String property) {
                if ("status".equals(property)) {
                    TimeEntry timeEntry = (TimeEntry) entity;
                    return ComponentsHelper.getTimeEntryStatusStyle(timeEntry);
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