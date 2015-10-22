/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
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
