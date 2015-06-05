/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.service;

import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.timesheets.SystemDataManager;
import com.haulmont.timesheets.entity.Tag;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.entity.TimeEntryStatus;
import com.haulmont.timesheets.global.CommandLineProcessor;
import com.haulmont.timesheets.global.DateTimeUtils;
import com.haulmont.timesheets.global.HoursAndMinutes;
import com.haulmont.timesheets.global.TimeParser;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @author degtyarjov
 * @version $Id$
 */
@Service(CommandLineService.NAME)
public class CommandLineServiceBean implements CommandLineService {
    @Inject
    protected SystemDataManager systemDataManager;
    @Inject
    protected TimeParser timeParser;

    @Inject
    protected TimeSource timeSource;

    @Nullable
    @Override
    public List<TimeEntry> createTimeEntriesForTheCommandLine(String commandLine) {
        CommandLineProcessor commandLineProcessor = new CommandLineProcessor(commandLine);
        String taskCode = commandLineProcessor.getTaskCode();
        List<String> tagCodes = commandLineProcessor.getTagCodes();
        if (taskCode != null) {
            Task task = systemDataManager.getEntityByCode(Task.class, taskCode, "task-full");
            if (task != null) {
                List<Tag> tags = systemDataManager.getEntitiesByCodes(Tag.class, tagCodes, View.MINIMAL);
                TimeEntry timeEntry = new TimeEntry();
                timeEntry.setTask(task);
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
