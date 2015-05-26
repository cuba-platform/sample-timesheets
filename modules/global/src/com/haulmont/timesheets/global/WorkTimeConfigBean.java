/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import com.haulmont.timesheets.core.WorkTimeConfig;
import com.haulmont.timesheets.entity.DayOfWeek;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.math.BigDecimal;
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

    public BigDecimal getWorkHourForDay() {
        return getWorkHourForWeek().divide(BigDecimal.valueOf(getWorkDaysCount()), BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getWorkHourForWeek() {
        return workTimeConfig.getWorkHourForWeek();
    }

    public void setWorkHourForWeek(BigDecimal hours) {
        workTimeConfig.setWorkHourForWeek(hours);
    }

    public int getWorkDaysCount() {
        return getWorkDays().size();
    }

    public List<DayOfWeek> getWorkDays() {
        List<String> days = workTimeConfig.getWorkDays();
        if (days.isEmpty()) {
            return Collections.emptyList();
        }
        List<DayOfWeek> workDays = new ArrayList<>(days.size());
        for (String day : days) {
            workDays.add(DayOfWeek.fromAbbreviation(day));
        }
        return Collections.unmodifiableList(workDays);
    }

    public void setWorkDays(Collection<DayOfWeek> workDays) {
        if (workDays.isEmpty()) {
            workTimeConfig.setWorkDays(Collections.<String>emptyList());
            return;
        }
        int count = 3;
        List<String> days = new ArrayList<>(workDays.size());
        for (DayOfWeek day : workDays) {
            days.add(day.getId().substring(0, count));
        }
        workTimeConfig.setWorkDays(days);
    }

    public List<DayOfWeek> getWeekends() {
        List<DayOfWeek> days = new ArrayList<>(Arrays.asList(DayOfWeek.values()));
        for (DayOfWeek day : getWorkDays()) {
            days.remove(day);
        }
        return Collections.unmodifiableList(days);
    }
}
