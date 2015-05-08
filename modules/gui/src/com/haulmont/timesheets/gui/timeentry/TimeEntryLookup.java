/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui.timeentry
 */
package com.haulmont.timesheets.gui.timeentry;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.timesheets.entity.TimeEntry;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Map;

/**
 * @author gorelov
 */
public class TimeEntryLookup extends AbstractLookup {

    @Inject
    protected Table timeEntriesTable;

    @Override
    public void init(Map<String, Object> params) {
        timeEntriesTable.setStyleProvider(new Table.StyleProvider() {
            @Nullable
            @Override
            public String getStyleName(Entity entity, String property) {
                if ("status".equals(property)) {
                    TimeEntry entry = (TimeEntry) entity;

                    switch (entry.getStatus()) {
                        case NEW:
                            return "time-entry-new";
                        case APPROVED:
                            return "time-entry-approved";
                        case REJECTED:
                            return "time-entry-rejected";
                        default:
                            return null;
                    }
                }
                return null;
            }
        });
    }
}