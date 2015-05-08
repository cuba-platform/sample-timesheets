/*
 * Copyright (c) 2015 com.haulmont.timesheets.service
 */
package com.haulmont.timesheets.service;

import com.haulmont.timesheets.core.WorkSettingsBean;
import com.haulmont.timesheets.entity.WeeklyReportEntry;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author gorelov
 */
@Service(WorkService.NAME)
public class WorkServiceBean implements WorkService {

    @Inject
    protected WorkSettingsBean settingsBean;

    @Override
    public float getWorkHourForDay() {
        return settingsBean.getWorkHourForDay();
    }

    @Override
    public float getWorkHourForWeek() {
        return settingsBean.getWorkHourForWeek();
    }

    @Override
    public int getWorkDaysCount() {
        return settingsBean.getWorkDaysCount();
    }

    @Override
    public WeeklyReportEntry.DayOfWeek[] getWorkDays() {
        return settingsBean.getWorkDays();
    }
}