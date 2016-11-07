
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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.haulmont.timesheets.entity.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author gorelov
 */
@Component(WeeklyReportConverter.NAME)
public class WeeklyReportConverter {
    public static final String NAME = "ts_WeeklyReportConverter";

    public static class TimeEntryGroupKey {
        final Project project;
        final Task task;
        final ActivityType activityType;

        public TimeEntryGroupKey(TimeEntry timeEntry) {
            this.project = timeEntry.getTask().getProject();
            this.task = timeEntry.getTask();
            this.activityType = timeEntry.getActivityType();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TimeEntryGroupKey that = (TimeEntryGroupKey) o;

            if (activityType != null ? !activityType.equals(that.activityType) : that.activityType != null)
                return false;
            return project.equals(that.project) && task.equals(that.task);

        }

        @Override
        public int hashCode() {
            int result = project.hashCode();
            result = 31 * result + task.hashCode();
            result = 31 * result + (activityType != null ? activityType.hashCode() : 0);
            return result;
        }
    }

    public List<WeeklyReportEntry> convertFromTimeEntries(List<TimeEntry> timeEntries) {

        if (timeEntries.isEmpty()) {
            return Collections.emptyList();
        }

        final Multimap<TimeEntryGroupKey, TimeEntry> groupedTimeEntries = ArrayListMultimap.create();

        for (TimeEntry timeEntry : timeEntries) {
            groupedTimeEntries.put(new TimeEntryGroupKey(timeEntry), timeEntry);
        }

        if (groupedTimeEntries.isEmpty()) {
            return Collections.emptyList();
        }

        List<WeeklyReportEntry> reportEntries = new ArrayList<>();
        for (Map.Entry<TimeEntryGroupKey, Collection<TimeEntry>> entry : groupedTimeEntries.asMap().entrySet()) {
            WeeklyReportEntry reportEntry = new WeeklyReportEntry();
            TimeEntryGroupKey entryKey = entry.getKey();
            reportEntry.setProject(entryKey.project);
            reportEntry.setTask(entryKey.task);
            reportEntry.setActivityType(entryKey.activityType);
            for (TimeEntry timeEntry : entry.getValue()) {
                reportEntry.addTimeEntry(timeEntry);
            }
            reportEntries.add(reportEntry);
        }

        return reportEntries;
    }
}
