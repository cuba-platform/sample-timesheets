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

package com.haulmont.timesheets.core;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.timesheets.entity.Holiday;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;

import javax.annotation.concurrent.GuardedBy;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author degtyarjov
 * @version $Id$
 */
@Component("ts_HolidaysCache")
public class HolidaysCache implements HolidaysCacheMBean, HolidaysCacheAPI {
    @Inject
    protected TimeSource timeSource;

    @Inject
    protected DataManager dataManager;

    protected ReadWriteLock lock = new ReentrantReadWriteLock();

    @GuardedBy("lock")
    protected TreeMap<Date, Holiday> cache;

    public String updateCache() {
        return doLoadCache(false);
    }

    private String doLoadCache(boolean lockReadBeforeFinish) {
        try {
            lock.writeLock().lock();
            cache = new TreeMap<>();
            LoadContext<Holiday> loadContext = new LoadContext<>(Holiday.class);
            loadContext.setQueryString("select e from ts$Holiday e " +
                    "where (e.startDate between :start and :end) or (e.endDate between :start and :end)")
                    .setParameter("start", DateUtils.addYears(timeSource.currentTimestamp(), -1))
                    .setParameter("end", DateUtils.addYears(timeSource.currentTimestamp(), 1));
            List<Holiday> holidays = dataManager.loadList(loadContext);
            for (Holiday holiday : holidays) {
                Date startDate = holiday.getStartDate();
                Date endDate = holiday.getEndDate();
                Date currentDate = startDate;
                while (currentDate.before(endDate)) {
                    cache.put(currentDate, holiday);
                    currentDate = DateUtils.addDays(currentDate, 1);
                }
            }

            if (lockReadBeforeFinish) {
                lock.readLock().lock();
            }
            return "Successfully loaded";
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Set<Holiday> getHolidays(Date start, Date end) {
        try {
            lock.readLock().lock();
            if (cache == null) {
                lock.readLock().unlock();
                doLoadCache(true);
            }

            NavigableMap<Date, Holiday> navigableMap = cache.subMap(start, true, end, true);
            return new HashSet<>(navigableMap.values());
        } finally {
            lock.readLock().unlock();
        }
    }
}
