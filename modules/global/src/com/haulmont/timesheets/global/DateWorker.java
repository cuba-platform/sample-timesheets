/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import com.haulmont.timesheets.entity.DayOfWeek;
import com.haulmont.timesheets.entity.Holiday;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * @author gorelov
 * @version $Id$
 */
@ManagedBean(DateWorker.NAME)
public class DateWorker {

    public static final String NAME = "timesheets_DateWorker";

    @Inject
    protected WorkTimeConfigBean workTimeConfigBean;
    @Inject
    protected ProjectsService projectsService;

    public boolean isHoliday(Date date) {
        List<Holiday> holidays = projectsService.getHolidays();
        long mills = date.getTime();
        for (Holiday holiday : holidays) {
            if (mills >= holiday.getStartDate().getTime() && mills <= holiday.getEndDate().getTime()) {
                return true;
            }
        }
        return false;
    }

    public boolean isWeekend(Date date) {
        for (DayOfWeek day : workTimeConfigBean.getWeekends()) {
            if (day.equals(DayOfWeek.fromCalendarDay(DateTimeUtils.getCalendarDayOfWeek(date)))) {
                return true;
            }
        }
        return false;
    }
}
