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

package com.haulmont.timesheets.service;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.global.ViewRepository;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.global.DateTimeUtils;
import com.haulmont.timesheets.global.HoursAndMinutes;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author degtyarjov
 */
@Service(StatisticService.NAME)
public class StatisticServiceBean implements StatisticService {
    @Inject
    protected DataManager dataManager;

    @Inject
    protected ViewRepository viewRepository;

    public Map<Task, BigDecimal> getStatisticsByTasks(Date start, Date end, @Nullable Project project) {
        LoadContext.Query query = LoadContext.createQuery(
                "select t from ts$TimeEntry t where t.date >= :start and t.date <= :end")
                .setParameter("start", start)
                .setParameter("end", end);
        if (project != null) {
            query.setQueryString(query.getQueryString() + " and t.task.project.id = :project");
            query.setParameter("project", project);
        }
        LoadContext<TimeEntry> loadContext = LoadContext.create(TimeEntry.class)
                .setQuery(query)
                .setView(new View(TimeEntry.class)
                                .addProperty("task",
                                        new View(Task.class)
                                                .addProperty("name")
                                                .addProperty("project", viewRepository.getView(Project.class, View.MINIMAL)))
                                .addProperty("timeInMinutes")
                );
        List<TimeEntry> timeEntries = dataManager.loadList(loadContext);
        Map<Task, BigDecimal> result = new HashMap<>();
        for (TimeEntry timeEntry : timeEntries) {
            BigDecimal sum = result.get(timeEntry.getTask());
            if (sum == null) {
                sum = BigDecimal.ZERO;
            }

            sum = sum.add(HoursAndMinutes.fromTimeEntry(timeEntry).toBigDecimal());
            result.put(timeEntry.getTask(), sum);
        }

        return result;
    }

    @Override
    public Map<Integer, Map<String, Object>> getStatisticsByProjects(Date start, Date end) {
        LoadContext<TimeEntry> loadContext = new LoadContext<>(TimeEntry.class)
                .setQuery(
                        new LoadContext.Query("select t from ts$TimeEntry t where t.date >= :start and t.date <= :end order by t.date")
                                .setParameter("start", start)
                                .setParameter("end", end)
                )
                .setView(new View(TimeEntry.class)
                                .addProperty("task",
                                        new View(Task.class)
                                                .addProperty("name")
                                                .addProperty("project", viewRepository.getView(Project.class, View.MINIMAL)))
                                .addProperty("timeInMinutes")
                                .addProperty("date")
                );
        List<TimeEntry> timeEntries = dataManager.loadList(loadContext);

        Map<WeekAndProject, BigDecimal> statistic = new LinkedHashMap<>();
        Calendar calendar = Calendar.getInstance();
        for (TimeEntry timeEntry : timeEntries) {
            calendar.setTime(timeEntry.getDate());
            int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
            WeekAndProject key = new WeekAndProject(weekOfYear, timeEntry.getTask().getProject());

            BigDecimal sum = statistic.get(key);
            if (sum == null) {
                sum = BigDecimal.ZERO;
            }

            sum = sum.add(HoursAndMinutes.fromTimeEntry(timeEntry).toBigDecimal());
            statistic.put(key, sum);
        }

        Map<Integer, Map<String, Object>> result = new LinkedHashMap<>();
        for (Map.Entry<WeekAndProject, BigDecimal> entry : statistic.entrySet()) {
            Integer week = entry.getKey().week;
            Map<String, Object> projectsByWeek = result.get(week);
            if (projectsByWeek == null) {
                projectsByWeek = new HashMap<>();
                calendar.set(Calendar.WEEK_OF_YEAR, week);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                projectsByWeek.put("week", DateTimeUtils.getDateFormat().format(calendar.getTime()));

            }
            projectsByWeek.put(entry.getKey().project.getName(), entry.getValue());
            result.put(week, projectsByWeek);
        }

        return result;
    }

    protected static class WeekAndProject {
        final Integer week;
        final Project project;

        public WeekAndProject(Integer week, Project project) {
            this.week = week;
            this.project = project;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WeekAndProject)) return false;

            WeekAndProject that = (WeekAndProject) o;

            return project != null ? project.equals(that.project)
                    : that.project == null && (week != null ? week.equals(that.week) : that.week == null);

        }

        @Override
        public int hashCode() {
            int result = week != null ? week.hashCode() : 0;
            result = 31 * result + (project != null ? project.hashCode() : 0);
            return result;
        }
    }
}