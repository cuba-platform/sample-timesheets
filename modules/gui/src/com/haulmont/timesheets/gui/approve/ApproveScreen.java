/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui
 */
package com.haulmont.timesheets.gui.approve;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.entity.TimeEntryStatus;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author gorelov
 */
public class ApproveScreen extends AbstractWindow {

    @Inject
    protected Table timeEntriesTable;
    @Inject
    protected CollectionDatasource<TimeEntry, UUID> timeEntriesDs;

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

    public void approve() {
        updateTimeEntriesStatus(TimeEntryStatus.APPROVED);
    }

    public void reject() {
        updateTimeEntriesStatus(TimeEntryStatus.REJECTED);
    }

    protected void updateTimeEntriesStatus(TimeEntryStatus status) {
        Set<TimeEntry> timeEntries = timeEntriesTable.getSelected();
        for (TimeEntry entry : timeEntries) {
            entry.setStatus(status);
        }
        timeEntriesDs.commit();
    }
}