/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.core;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import static com.haulmont.timesheets.entity.WeeklyReportEntry.DayOfWeek;

/**
 * @author gorelov
 * @version $Id$
 */
@ManagedBean(WorkSettingsBean.NAME)
public class WorkSettingsBean {

    public static final String NAME = "ts_WorkSettings";

    @Inject
    protected WorkConfig workConfig;

    public float getWorkHourForDay() {
        return getWorkHourForWeek() / getWorkDaysCount();
    }

    public float getWorkHourForWeek() {
        return workConfig.getWorkHourForWeek();
    }

    public int getWorkDaysCount() {
        return getWorkDays().length;
    }

    public DayOfWeek[] getWorkDays() {
        String[] days = workConfig.getWorkDays().split("[|]");
        DayOfWeek[] workDays = new DayOfWeek[days.length];
        for (int i = 0; i < days.length; i++) {
            workDays[i] = DayOfWeek.fromAbbreviation(days[i]);
        }
        return workDays;
    }
}
