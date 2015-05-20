/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import com.haulmont.timesheets.core.WorkTimeConfig;
import com.haulmont.timesheets.entity.DayOfWeek;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author gorelov
 * @version $Id$
 */
@ManagedBean(WorkTimeConfigBean.NAME)
public class WorkTimeConfigBean {

    public static final String NAME = "timesheets_WorkTimeConfigBean";

    @Inject
    protected WorkTimeConfig workTimeConfig;

    public double getWorkHourForDay() {
        return getWorkHourForWeek() / getWorkDaysCount();
    }

    public double getWorkHourForWeek() {
        return workTimeConfig.getWorkHourForWeek();
    }

    public void setWorkHourForWeek(double hours) {
        workTimeConfig.setWorkHourForWeek(hours);
    }

    public int getWorkDaysCount() {
        return getWorkDays().length;
    }

    public DayOfWeek[] getWorkDays() {
        String[] days = workTimeConfig.getWorkDays().split("[|]");
        DayOfWeek[] workDays = new DayOfWeek[days.length];
        for (int i = 0; i < days.length; i++) {
            workDays[i] = DayOfWeek.fromAbbreviation(days[i]);
        }
        return workDays;
    }

    public void setWorkDays(DayOfWeek[] workDays) {
        int count = 3;
        StringBuilder sb = new StringBuilder(workDays.length * count);
        int length = workDays.length;
        for (int i = 0; i < length; i++) {
            sb.append(workDays[i].getId().substring(0, count));
            if (i < length - 1) {
                sb.append("|");
            }
        }
        workTimeConfig.setWorkDays(sb.toString());
    }

    public DayOfWeek[] getWeekends() {
        List<DayOfWeek> days = new ArrayList<>(Arrays.asList(DayOfWeek.values()));
        for (DayOfWeek day : getWorkDays()) {
            days.remove(day);
        }
        return days.toArray(new DayOfWeek[days.size()]);
    }
}
