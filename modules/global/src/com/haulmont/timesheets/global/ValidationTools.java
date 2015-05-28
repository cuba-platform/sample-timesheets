/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author gorelov
 * @version $Id$
 */
@ManagedBean(ValidationTools.NAME)
public class ValidationTools {

    public static final String NAME = "timesheets_ValidationTools";
    public static final int SCALE = 2;

    @Inject
    protected WorkTimeConfigBean workTimeConfigBean;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected DateTools dateTools;

    public BigDecimal workHoursForPeriod(Date start, Date end) {
        // TODO: gg, check dates?
        BigDecimal dayHourPlan = workTimeConfigBean.getWorkHourForDay();
        BigDecimal totalWorkHours = BigDecimal.ZERO;

        for (; start.getTime() <= end.getTime(); start = DateUtils.addDays(start, 1)) {
            if (dateTools.isWorkday(start)) {
                totalWorkHours = totalWorkHours.add(dayHourPlan);
            }
        }
        return totalWorkHours;
    }

    public BigDecimal workHoursForWeek(Date date) {
        return workHoursForPeriod(DateTimeUtils.getFirstDayOfWeek(date), DateTimeUtils.getLastDayOfWeek(date));
    }

    public BigDecimal workHoursForMonth(Date date) {
        return workHoursForPeriod(DateTimeUtils.getFirstDayOfMonth(date), DateTimeUtils.getLastDayOfMonth(date));
    }

    public BigDecimal userWorkHoursForPeriod(Date start, Date end, User user) {
        List<TimeEntry> timeEntries = projectsService.getTimeEntriesForPeriod(start, end, user, null, View.LOCAL);
        if (timeEntries.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalWorkHours = BigDecimal.ZERO;
        DateFormat formatter = new SimpleDateFormat(DateTimeUtils.TIME_FORMAT);
        for (TimeEntry timeEntry : timeEntries) {
            String time = formatter.format(timeEntry.getTime());
            totalWorkHours = totalWorkHours.add(DateTimeUtils.timeStringToBigDecimal(time));
        }
        return totalWorkHours;
    }

    public BigDecimal userWorkHoursForWeek(Date date, User user) {
        return userWorkHoursForPeriod(
                DateTimeUtils.getFirstDayOfWeek(date),
                DateTimeUtils.getLastDayOfWeek(date),
                user
        );
    }

    public BigDecimal userWorkHoursForMonth(Date date, User user) {
        return userWorkHoursForPeriod(
                DateTimeUtils.getFirstDayOfMonth(date),
                DateTimeUtils.getLastDayOfMonth(date),
                user
        );
    }

    public boolean isWorkTimeMatchToPlanForPeriod(Date start, Date end, User user) {
        BigDecimal plan = workHoursForPeriod(start, end).setScale(SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal fact = userWorkHoursForPeriod(start, end, user).setScale(SCALE, BigDecimal.ROUND_HALF_UP);
        return plan.equals(fact);
    }

    public boolean isWorkTimeMatchToPlanForDay(Date date, User user) {
        return isWorkTimeMatchToPlanForPeriod(date, date, user);
    }

    public boolean isWorkTimeMatchToPlanForWeek(Date date, User user) {
        return isWorkTimeMatchToPlanForPeriod(
                DateTimeUtils.getFirstDayOfWeek(date),
                DateTimeUtils.getLastDayOfWeek(date),
                user
        );
    }

    public boolean isWorkTimeMatchToPlanForMonth(Date date, User user) {
        return isWorkTimeMatchToPlanForPeriod(
                DateTimeUtils.getFirstDayOfMonth(date),
                DateTimeUtils.getLastDayOfMonth(date),
                user
        );
    }
}
