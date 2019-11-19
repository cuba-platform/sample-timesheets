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

package com.haulmont.timesheets.gui.approve;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.entity.TimeEntryStatus;
import com.haulmont.timesheets.global.WorkTimeConfigBean;
import com.haulmont.timesheets.global.WorkdaysTools;
import com.haulmont.timesheets.gui.timeentry.AllTimeEntries;
import com.haulmont.timesheets.gui.util.ScreensHelper;
import com.haulmont.timesheets.gui.util.TimeEntryOvertimeAggregation;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.*;

/**
 * @author gorelov
 */
@UiController("ts$TimeEntry.all-approve")
@UiDescriptor("timeentry-all-approve.xml")
public class BulkTimeEntriesApprove extends AllTimeEntries {
    @Inject
    protected UserSession userSession;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected Dialogs dialogs;
    @Inject
    protected WorkdaysTools workdaysTools;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected WorkTimeConfigBean workTimeConfigBean;
    @Inject
    protected PopupButton approve;
    @Inject
    protected PopupButton reject;
    @Inject
    protected LookupField user;
    @Inject
    protected LookupField status;
    @Inject
    protected CollectionLoader<Group> groupsDl;
    @Inject
    protected CollectionLoader<Project> projectsDl;
    @Inject
    protected LookupField<Group> group;
    @Inject
    protected LookupField<Project> project;
    @Inject
    protected CollectionLoader<Task> tasksDl;
    @Inject
    protected LookupField<Task> task;
    @Inject
    protected DateField<Date> dateFrom;
    @Inject
    protected DateField<Date> dateTo;

    @Subscribe("timeEntriesTable.refresh")
    protected void onTimeEntriesTableRefresh(Action.ActionPerformedEvent event) {
        refresh();
    }

    @Subscribe(id = "projectsDc", target = Target.DATA_CONTAINER)
    private void onProjectsDcItemChange(InstanceContainer.ItemChangeEvent<Project> e) {
        tasksDl.setParameter("project", e.getItem());
        tasksDl.load();
    }

    @Subscribe
    @Override
    public void onInit(InitEvent event) {
        super.onInit(event);

        timeEntriesTable.getColumn("overtime").setAggregation(
                ScreensHelper.createAggregationInfo(
                        projectsService.getEntityMetaPropertyPath(TimeEntry.class, "overtime"),
                        new TimeEntryOvertimeAggregation()));

        Date previousMonth = DateUtils.addMonths(timeSource.currentTimestamp(), -1);
        dateFrom.setValue(DateUtils.truncate(previousMonth, Calendar.MONTH));
        dateTo.setValue(DateUtils.addDays(DateUtils.truncate(timeSource.currentTimestamp(), Calendar.MONTH), -1));

        approve.addAction(new BaseAction("approveSelected")
                .withHandler(ae -> {
                    setStatus(timeEntriesTable.getSelected(), TimeEntryStatus.APPROVED);
                })
                .withCaption(messageBundle.getMessage("approveSelected")));

        approve.addAction(new BaseAction("approveAll")
                .withHandler(ae -> {
                    setStatus(timeEntriesDc.getMutableItems(), TimeEntryStatus.APPROVED);
                })
                .withCaption(messageBundle.getMessage("approveAll")));

        reject.addAction(new BaseAction("rejectSelected")
                .withHandler(ae -> {
                    setStatus(timeEntriesTable.getSelected(), TimeEntryStatus.REJECTED);
                })
                .withCaption(messageBundle.getMessage("rejectSelected")));

        reject.addAction(new BaseAction("rejectAll")
                .withHandler(ae -> {
                    setStatus(timeEntriesDc.getMutableItems(), TimeEntryStatus.REJECTED);
                })
                .withCaption(messageBundle.getMessage("rejectAll")));

        status.setOptionsList(Arrays.asList(TimeEntryStatus.values()));
        user.setOptionsList(projectsService.getManagedUsers(userSession.getCurrentOrSubstitutedUser(), View.MINIMAL));
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        if (securityAssistant.isSuperUser()) {
            timeEntriesDl.setQuery("select e from ts$TimeEntry e " +
                    "where e.date >= :dateFrom and e.date <= :dateTo");
            projectsDl.setParameter("superUser", true);
        } else {
            timeEntriesDl.setParameter("sessionUser", userSession.getCurrentOrSubstitutedUser());
            projectsDl.setParameter("superUser", false);
        }
        timeEntriesDl.setParameter("dateFrom", dateFrom.getValue());
        timeEntriesDl.setParameter("dateTo", dateTo.getValue());
        timeEntriesDl.load();
        projectsDl.setParameter("user", userSession.getCurrentOrSubstitutedUser());
        projectsDl.load();
        groupsDl.load();
    }

    @Subscribe(id = "timeEntriesDc", target = Target.DATA_CONTAINER)
    protected void onTimeEntriesDcCollectionChange(CollectionContainer.CollectionChangeEvent<TimeEntry> e) {
        Multimap<Map<String, Object>, TimeEntry> map = ArrayListMultimap.create();
        for (TimeEntry item : timeEntriesDc.getMutableItems()) {
            Map<String, Object> key = new TreeMap<>();
            key.put("user", item.getUser());
            key.put("date", item.getDate());
            map.put(key, item);
        }

        for (Map.Entry<Map<String, Object>, Collection<TimeEntry>> entry : map.asMap().entrySet()) {
            BigDecimal thisDaysSummary = BigDecimal.ZERO;
            for (TimeEntry timeEntry : entry.getValue()) {
                thisDaysSummary = thisDaysSummary.add(timeEntry.getTimeInHours());
            }

            for (TimeEntry timeEntry : entry.getValue()) {
                BigDecimal planHoursForDay = workdaysTools.isWorkday(timeEntry.getDate())
                        ? workTimeConfigBean.getWorkHourForDay()
                        : BigDecimal.ZERO;
                BigDecimal overtime = thisDaysSummary.subtract(planHoursForDay);
                timeEntry.setOvertimeInHours(overtime);
            }
        }
    }

    protected void setStatus(final Collection<TimeEntry> timeEntries, final TimeEntryStatus timeEntryStatus) {
        dialogs.createOptionDialog(Dialogs.MessageType.CONFIRMATION)
                .withContentMode(ContentMode.HTML)
                .withCaption(messageBundle.getMessage("notification.confirmation"))
                .withMessage(messageBundle.getMessage("notification.confirmationText"))
                .withActions(
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                super.actionPerform(component);
                                CommitContext commitContext = new CommitContext();
                                for (TimeEntry timeEntry : timeEntries) {
                                    timeEntry.setStatus(timeEntryStatus);
                                    commitContext.addInstanceToCommit(timeEntry);
                                }

                                dataManager.commit(commitContext);
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                )
                .show();
    }

    public void refresh() {
        if (user.getValue() != null)
            timeEntriesDl.setParameter("user", user.getValue());
        else
            timeEntriesDl.removeParameter("user");

        if (task.getValue() != null)
            timeEntriesDl.setParameter("task", task.getValue());
        else
            timeEntriesDl.removeParameter("task");

        if (project.getValue() != null)
            timeEntriesDl.setParameter("project", project.getValue());
        else
            timeEntriesDl.removeParameter("project");

        if (status.getValue() != null)
            timeEntriesDl.setParameter("status", status.getValue());
        else
            timeEntriesDl.removeParameter("status");

        if (group.getValue() != null)
            timeEntriesDl.setParameter("group", group.getValue());
        else
            timeEntriesDl.removeParameter("group");

        timeEntriesDl.setParameter("dateFrom", dateFrom.getValue());
        timeEntriesDl.setParameter("dateTo", dateTo.getValue());

        timeEntriesDl.load();
    }
}