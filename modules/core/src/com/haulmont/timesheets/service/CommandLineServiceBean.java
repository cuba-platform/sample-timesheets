/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.service;

import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.timesheets.SystemDataManager;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.entity.TimeEntryStatus;
import com.haulmont.timesheets.global.CommandLineUtils;
import com.haulmont.timesheets.global.TimeWorker;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Date;
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
    protected TimeWorker timeWorker;

    @Inject
    protected TimeSource timeSource;

    @Nullable
    @Override
    public List<TimeEntry> createTimeEntriesForTheCommandLine(String commandLine) {
        CommandLineUtils commandLineUtils = new CommandLineUtils(commandLine);
        String taskCode = commandLineUtils.getTaskCode();
        if (taskCode != null) {
            Task task = systemDataManager.getEntityByCode(Task.class, taskCode, View.MINIMAL);
            TimeEntry timeEntry = new TimeEntry();
            timeEntry.setTask(task);
            String spentTime = commandLineUtils.getSpentTime();
            if (spentTime != null) {
                Date parsedTime = timeWorker.parse(spentTime);
                timeEntry.setTime(parsedTime);
            }

            timeEntry.setDate(timeSource.currentTimestamp());
            timeEntry.setStatus(TimeEntryStatus.NEW);

            return Arrays.asList(timeEntry);
        }

        return null;
    }
}
