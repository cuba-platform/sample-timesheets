/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.Tag;
import com.haulmont.timesheets.entity.TagType;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @author gorelov
 * @version $Id$
 */
@ManagedBean(ValidationTools.NAME)
public class ValidationTools {
    public static final String NAME = "ts_ValidationTools";

    @Inject
    protected WorkTimeConfigBean workTimeConfigBean;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected DateTools dateTools;
    @Inject
    protected Messages messages;

    public HoursAndMinutes workHoursForPeriod(Date start, Date end, User user) {
        HoursAndMinutes dayHourPlan = HoursAndMinutes.fromBigDecimal(workTimeConfigBean.getUserWorkHourForDay(user));
        HoursAndMinutes totalWorkHours = new HoursAndMinutes();

        for (; start.getTime() <= end.getTime(); start = DateUtils.addDays(start, 1)) {
            if (dateTools.isWorkday(start)) {
                totalWorkHours.add(dayHourPlan);
            }
        }
        return totalWorkHours;
    }

    public HoursAndMinutes plannedWorkHoursForWeek(Date date, User user) {
        return workHoursForPeriod(DateTimeUtils.getFirstDayOfWeek(date), DateTimeUtils.getLastDayOfWeek(date), user);
    }

    public HoursAndMinutes plannedWorkHoursForMonth(Date date, User user) {
        return workHoursForPeriod(DateTimeUtils.getFirstDayOfMonth(date), DateTimeUtils.getLastDayOfMonth(date), user);
    }

    public HoursAndMinutes actualWorkHoursForPeriod(Date start, Date end, User user) {
        List<TimeEntry> timeEntries = projectsService.getTimeEntriesForPeriod(start, end, user, null, View.LOCAL);
        if (timeEntries.isEmpty()) {
            return new HoursAndMinutes();
        }

        HoursAndMinutes totalWorkHours = new HoursAndMinutes();
        for (TimeEntry timeEntry : timeEntries) {
            totalWorkHours.addMinutes(timeEntry.getTimeInMinutes());
        }
        return totalWorkHours;
    }

    public HoursAndMinutes actualWorkHoursForWeek(Date date, User user) {
        return actualWorkHoursForPeriod(
                DateTimeUtils.getFirstDayOfWeek(date),
                DateTimeUtils.getLastDayOfWeek(date),
                user
        );
    }

    public HoursAndMinutes actualWorkHoursForMonth(Date date, User user) {
        return actualWorkHoursForPeriod(
                DateTimeUtils.getFirstDayOfMonth(date),
                DateTimeUtils.getLastDayOfMonth(date),
                user
        );
    }

    public boolean isWorkTimeMatchToPlanForPeriod(Date start, Date end, User user) {
        HoursAndMinutes plan = workHoursForPeriod(start, end, user);
        HoursAndMinutes fact = actualWorkHoursForPeriod(start, end, user);
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

    public ResultAndCause validateTags(TimeEntry timeEntry) {
        Preconditions.checkNotNullArgument(timeEntry);
        Preconditions.checkNotNullArgument(timeEntry.getTask());
        Preconditions.checkNotNullArgument(timeEntry.getTask().getRequiredTagTypes());

        HashSet<TagType> remainingRequiredTagTypes = new HashSet<>(timeEntry.getTask().getRequiredTagTypes());
        if (CollectionUtils.isNotEmpty(timeEntry.getTags())) {
            for (Tag tag : timeEntry.getTags()) {
                remainingRequiredTagTypes.remove(tag.getTagType());
            }
        }

        if (remainingRequiredTagTypes.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (TagType remainingRequiredTagType : remainingRequiredTagTypes) {
                stringBuilder.append(remainingRequiredTagType.getName()).append(",");
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);//remove last comma
            }



            return ResultAndCause.negative(
                    messages.formatMessage(getClass(), "notification.requiredTagTypesNotPresent", stringBuilder));
        }

        return ResultAndCause.positive();
    }
}
