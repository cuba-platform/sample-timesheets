/*
 * Copyright (c) 2015 com.haulmont.timesheets.listener
 */
package com.haulmont.timesheets.listener;

import com.haulmont.cuba.core.listener.BeforeDeleteEntityListener;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.entity.TimeEntryStatus;
import com.haulmont.timesheets.global.EntityDeletionException;

import javax.annotation.ManagedBean;

/**
 * @author degtyarjov
 */
@ManagedBean("ts_TimeEntryListener")
public class TimeEntryListener implements BeforeInsertEntityListener<TimeEntry>,
        BeforeUpdateEntityListener<TimeEntry>,
        BeforeDeleteEntityListener<TimeEntry> {
    @Override
    public void onBeforeInsert(TimeEntry entity) {
        if (entity.getTask() != null) {
            entity.setTaskName(entity.getTask().getName());
        }
    }

    @Override
    public void onBeforeUpdate(TimeEntry entity) {
        if (entity.getTask() != null) {
            entity.setTaskName(entity.getTask().getName());
        }
    }

    @Override
    public void onBeforeDelete(TimeEntry entity) {
        if (entity.getStatus() != null && TimeEntryStatus.CLOSED.equals(entity.getStatus())) {
            throw new EntityDeletionException("Deletion of closed TimeEntry");
        }
    }
}