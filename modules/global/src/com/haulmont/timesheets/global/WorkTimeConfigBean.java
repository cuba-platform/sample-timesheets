/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import com.haulmont.timesheets.core.WorkTimeConfig;
import com.haulmont.timesheets.entity.DayOfWeek;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.*;

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
        return getWorkDays().size();
    }

    public List<DayOfWeek> getWorkDays() {
        String[] days = workTimeConfig.getWorkDays().split("[|]");
        if (days.length == 0) {
            return Collections.emptyList();
        }
        List<DayOfWeek> workDays = new ArrayList<>(days.length);
        for (String day : days) {
            workDays.add(DayOfWeek.fromAbbreviation(day));
        }
        return workDays;
    }

    public void setWorkDays(Collection<DayOfWeek> workDays) {
        if (workDays.isEmpty()) {
            workTimeConfig.setWorkDays("");
            return;
        }
        int i = 0;
        int count = 3;
        int iMax = workDays.size() - 1;
        StringBuilder sb = new StringBuilder(workDays.size() * count);
        for (DayOfWeek day : workDays) {
            sb.append(day.getId().substring(0, count));
            if (i++ < iMax) {
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
