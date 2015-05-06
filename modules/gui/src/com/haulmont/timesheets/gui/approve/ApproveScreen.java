/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui
 */
package com.haulmont.timesheets.gui.approve;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.entity.TimeEntryStatus;

import javax.inject.Inject;
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