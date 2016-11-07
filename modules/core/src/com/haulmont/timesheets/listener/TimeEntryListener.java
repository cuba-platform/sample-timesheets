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

package com.haulmont.timesheets.listener;

import com.haulmont.cuba.core.listener.BeforeDeleteEntityListener;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import com.haulmont.timesheets.config.WorkTimeConfig;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.entity.TimeEntryStatus;
import com.haulmont.timesheets.exception.ClosedPeriodException;
import com.haulmont.timesheets.global.EntityDeletionException;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;
import com.haulmont.cuba.core.EntityManager;
import org.springframework.stereotype.Component;

/**
 * @author degtyarjov
 */
@Component("ts_TimeEntryListener")
public class TimeEntryListener implements BeforeInsertEntityListener<TimeEntry>,
        BeforeUpdateEntityListener<TimeEntry>,
        BeforeDeleteEntityListener<TimeEntry> {

    protected static final BigDecimal MINUTES_IN_HOUR = BigDecimal.valueOf(60);

    @Inject
    protected WorkTimeConfig workTimeConfig;

    @Override
    public void onBeforeInsert(TimeEntry entity, EntityManager entityManager) {
        if (entity.getTask() != null) {
            entity.setTaskName(entity.getTask().getName());
        }

        if (entity.getTimeInMinutes() != null) {
            setTimeInHours(entity);
        }

        checkClosedPeriods(entity.getDate());
    }

    @Override
    public void onBeforeUpdate(TimeEntry entity, EntityManager entityManager) {
        if (entity.getTask() != null) {
            entity.setTaskName(entity.getTask().getName());
        }

        if (entity.getTimeInMinutes() != null) {
            setTimeInHours(entity);
        }

        checkClosedPeriods(entity.getDate());
    }

    @Override
    public void onBeforeDelete(TimeEntry entity, EntityManager entityManager) {
        if (entity.getStatus() != null && TimeEntryStatus.CLOSED.equals(entity.getStatus())) {
            throw new EntityDeletionException("Deletion of closed TimeEntry");
        }

        checkClosedPeriods(entity.getDate());
    }

    protected void setTimeInHours(TimeEntry entity) {
        BigDecimal minutes = BigDecimal.valueOf(entity.getTimeInMinutes()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        entity.setTimeInHours(minutes.divide(MINUTES_IN_HOUR, BigDecimal.ROUND_HALF_DOWN));
    }

    protected void checkClosedPeriods(Date date) {
        Date openPeriodStart = workTimeConfig.getOpenPeriodStart();

        if (openPeriodStart != null && date.before(openPeriodStart)) {
            throw new ClosedPeriodException("You can not modify time entries in closed periods");
        }
    }
}