/*
 * Copyright (c) 2015 com.haulmont.timesheets.service
 */
package com.haulmont.timesheets.service;

import com.haulmont.timesheets.entity.WeeklyReportEntry;

/**
 * @author gorelov
 */
public interface WorkService {
    String NAME = "ts_WorkService";

    float getWorkHourForDay();

    float getWorkHourForWeek();

    int getWorkDaysCount();

    WeeklyReportEntry.DayOfWeek[] getWorkDays();
}