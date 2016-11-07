
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

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.timesheets.SystemDataManager;
import com.haulmont.timesheets.entity.ActivityType;
import com.haulmont.timesheets.entity.Tag;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.global.CommandLineProcessor;
import com.haulmont.timesheets.global.HoursAndMinutes;
import com.haulmont.timesheets.global.TimeParser;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author degtyarjov
 */
@Service(CommandLineService.NAME)
public class CommandLineServiceBean implements CommandLineService {
    @Inject
    protected SystemDataManager systemDataManager;
    @Inject
    protected TimeParser timeParser;
    @Inject
    protected Metadata metadata;
    @Inject
    protected TimeSource timeSource;

    @Nullable
    @Override
    public List<TimeEntry> createTimeEntriesForTheCommandLine(String commandLine) {
        CommandLineProcessor commandLineProcessor = new CommandLineProcessor(commandLine);
        String taskCode = commandLineProcessor.getTaskCode();
        String activityTypeCode = commandLineProcessor.getActivityType();
        List<String> tagCodes = commandLineProcessor.getTagCodes();
        if (taskCode != null) {
            Task task = systemDataManager.getEntityByCode(Task.class, taskCode, "task-full");
            if (task != null) {
                List<Tag> tags = systemDataManager.getEntitiesByCodes(Tag.class, tagCodes, View.MINIMAL);
                ActivityType activityType = systemDataManager.getEntityByCode(ActivityType.class, activityTypeCode, View.LOCAL);

                TimeEntry timeEntry = metadata.create(TimeEntry.class);
                timeEntry.setTask(task);
                timeEntry.setActivityType(activityType);
                timeEntry.setTags(new HashSet<>(tags));
                timeEntry.getTags().addAll(task.getDefaultTags());
                String spentTime = commandLineProcessor.getSpentTime();
                if (spentTime != null) {
                    HoursAndMinutes hoursAndMinutes = timeParser.parseToHoursAndMinutes(spentTime);
                    timeEntry.setTimeInMinutes(hoursAndMinutes.toMinutes());
                } else {
                    timeEntry.setTimeInMinutes(0);
                }
                timeEntry.setDate(timeSource.currentTimestamp());

                return Arrays.asList(timeEntry);
            }
        }

        return null;
    }
}
