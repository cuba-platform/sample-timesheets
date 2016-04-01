
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

import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.config.WorkTimeConfig;
import com.haulmont.timesheets.entity.DayOfWeek;
import com.haulmont.timesheets.entity.ExtUser;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author gorelov
 */
@Component(WorkTimeConfigBean.NAME)
public class WorkTimeConfigBean {

    public static final String NAME = "ts_WorkTimeConfigBean";

    @Inject
    protected WorkTimeConfig workTimeConfig;

    public BigDecimal getWorkHourForDay() {
        return getWorkHourForWeek().divide(BigDecimal.valueOf(getWorkDaysCount()), BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getWorkHourForWeek() {
        return workTimeConfig.getWorkHourForWeek();
    }

    public BigDecimal getUserWorkHourForDay(User user) {
        return getUserWorkHourForWeek(user).divide(BigDecimal.valueOf(getWorkDaysCount()), BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getUserWorkHourForWeek(User user) {
        if (user instanceof ExtUser) {
            ExtUser extUser = (ExtUser) user;
            return extUser.getWorkHoursForWeek();
        }
        return BigDecimal.ZERO;
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
            workTimeConfig.setWorkDays(Collections.emptyList());
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

    public Date getOpenPeriodStart() {
        return workTimeConfig.getOpenPeriodStart();
    }

    public void setOpenPeriodStart(Date date) {
        workTimeConfig.setOpenPeriodStart(date);
    }
}
