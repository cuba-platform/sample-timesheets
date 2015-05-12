/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import com.haulmont.timesheets.core.WorkConfig;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import static com.haulmont.timesheets.entity.WeeklyReportEntry.DayOfWeek;

/**
 * @author gorelov
 * @version $Id$
 */
@ManagedBean(WorkConfigBean.NAME)
public class WorkConfigBean {

    public static final String NAME = "timesheets_WorkConfigBean";

    @Inject
    protected WorkConfig workConfig;

    public double getWorkHourForDay() {
        return getWorkHourForWeek() / getWorkDaysCount();
    }

    public double getWorkHourForWeek() {
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
