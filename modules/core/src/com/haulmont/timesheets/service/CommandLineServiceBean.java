/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.service;

import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.timesheets.SystemDataManager;
import com.haulmont.timesheets.entity.Tag;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.entity.TimeEntryStatus;
import com.haulmont.timesheets.global.CommandLineUtils;
import com.haulmont.timesheets.global.DateTimeUtils;
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
        CommandLineUtils commandLineUtils = new CommandLineUtils(commandLine);
        String taskCode = commandLineUtils.getTaskCode();
        List<String> tagCodes = commandLineUtils.getTagCodes();
        if (taskCode != null) {
            Task task = systemDataManager.getEntityByCode(Task.class, taskCode, "task-full");
            List<Tag> tags = systemDataManager.getEntitiesByCodes(Tag.class, tagCodes, View.MINIMAL);
            TimeEntry timeEntry = new TimeEntry();
            timeEntry.setTask(task);
            timeEntry.setTags(new HashSet<>(tags));
            String spentTime = commandLineUtils.getSpentTime();
            if (spentTime != null) {
                Date parsedTime = timeParser.parse(spentTime);
                timeEntry.setTime(parsedTime);
            } else {
                timeEntry.setTime(DateTimeUtils.getDateWithoutTime(timeSource.currentTimestamp()));
            }

            timeEntry.setDate(timeSource.currentTimestamp());
            timeEntry.setStatus(TimeEntryStatus.NEW);

            return Arrays.asList(timeEntry);
        }

        return null;
    }
}
