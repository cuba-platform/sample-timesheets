
package com.haulmont.timesheets.service;

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

                TimeEntry timeEntry = new TimeEntry();
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
