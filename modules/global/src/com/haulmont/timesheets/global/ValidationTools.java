
/*
 * Copyright (c) 2016 Haulmont
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.timesheets.global;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @author gorelov
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@Component(ValidationTools.NAME)
public class ValidationTools {
    public static final String NAME = "ts_ValidationTools";

    @Inject
    protected WorkTimeConfigBean workTimeConfigBean;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected WorkdaysTools workdaysTools;
    @Inject
    protected Messages messages;

    public HoursAndMinutes workHoursForPeriod(Date start, Date end, User user) {
        HoursAndMinutes dayHourPlan = HoursAndMinutes.fromBigDecimal(workTimeConfigBean.getUserWorkHourForDay(user));
        HoursAndMinutes totalWorkHours = new HoursAndMinutes();

        for (; start.getTime() <= end.getTime(); start = DateUtils.addDays(start, 1)) {
            if (workdaysTools.isWorkday(start)) {
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

    public ResultAndCause validateTimeEntry(TimeEntry timeEntry) {
        ResultAndCause baseValidation = validateTimeEntryBase(timeEntry);
        if (baseValidation.isPositive) {
            if (timeEntry.getTimeInMinutes() == null || timeEntry.getTimeInMinutes() == 0) {
                return ResultAndCause.negative(messages.getMessage(getClass(), "notification.emptySpentTime"));
            }
        } else {
            return baseValidation;
        }

        return ResultAndCause.POSITIVE;
    }

    public ResultAndCause validateWeeklyReport(WeeklyReportEntry weeklyReportEntry) {
        return validateTimeEntryBase(weeklyReportEntry);
    }

    protected ResultAndCause validateTimeEntryBase(TimeEntryBase timeEntry) {
        if (timeEntry.getTask() == null) {
            return ResultAndCause.negative(messages.getMessage(getClass(), "notification.emptyTask"));
        }

        List<ActivityType> activityTypes = projectsService.getActivityTypesForProject(timeEntry.getTask().getProject(), View.MINIMAL);
        if (CollectionUtils.isNotEmpty(activityTypes) && timeEntry.getActivityType() == null) {
            return ResultAndCause.negative(messages.getMessage(getClass(), "notification.emptyActivityType"));
        }

        return ResultAndCause.POSITIVE;
    }

    public ResultAndCause validateTags(TimeEntryBase timeEntry) {
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
