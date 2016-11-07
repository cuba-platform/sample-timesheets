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
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.entity.TimeEntryStatus;
import com.haulmont.timesheets.global.WorkTimeConfigBean;
import com.haulmont.timesheets.global.WorkdaysTools;
import com.haulmont.timesheets.gui.timeentry.AllTimeEntries;
import com.haulmont.timesheets.gui.util.ComponentsHelper;
import com.haulmont.timesheets.gui.util.TimeEntryOvertimeAggregation;
import org.apache.commons.lang.time.DateUtils;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.Calendar;

/**
 * @author gorelov
 */
public class BulkTimeEntriesApprove extends AllTimeEntries {
    @Inject
    protected DateField dateFrom;

    @Inject
    protected DateField dateTo;

    @Inject
    protected TimeSource timeSource;

    @Inject
    protected PopupButton approve;

    @Inject
    protected PopupButton reject;

    @Inject
    protected LookupField status;

    @Inject
    protected LookupField user;

    @Inject
    protected UserSession userSession;

    @Inject
    protected WorkdaysTools workdaysTools;

    @Inject
    protected WorkTimeConfigBean workTimeConfigBean;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (securityAssistant.isSuperUser()) {
            timeEntriesDs.setQuery("select e from ts$TimeEntry e " +
                    "where e.date >= :component$dateFrom and e.date <= :component$dateTo");
        }

        timeEntriesTable.getColumn("overtime").setAggregation(
                ComponentsHelper.createAggregationInfo(
                        projectsService.getEntityMetaPropertyPath(TimeEntry.class, "overtime"),
                        new TimeEntryOvertimeAggregation()));

        timeEntriesDs.addCollectionChangeListener(e -> {
            Multimap<Map<String, Object>, TimeEntry> map = ArrayListMultimap.create();
            for (TimeEntry item : timeEntriesDs.getItems()) {
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
        });

        Date previousMonth = DateUtils.addMonths(timeSource.currentTimestamp(), -1);
        dateFrom.setValue(DateUtils.truncate(previousMonth, Calendar.MONTH));
        dateTo.setValue(DateUtils.addDays(DateUtils.truncate(timeSource.currentTimestamp(), Calendar.MONTH), -1));

        approve.addAction(new AbstractAction("approveAll") {
            @Override
            public void actionPerform(Component component) {
                setStatus(timeEntriesDs.getItems(), TimeEntryStatus.APPROVED);
            }
        });

        approve.addAction(new AbstractAction("approveSelected") {
            @Override
            public void actionPerform(Component component) {
                setStatus(timeEntriesTable.getSelected(), TimeEntryStatus.APPROVED);
            }
        });

        reject.addAction(new AbstractAction("rejectAll") {
            @Override
            public void actionPerform(Component component) {
                setStatus(timeEntriesDs.getItems(), TimeEntryStatus.REJECTED);
            }
        });

        reject.addAction(new AbstractAction("rejectSelected") {
            @Override
            public void actionPerform(Component component) {
                setStatus(timeEntriesTable.getSelected(), TimeEntryStatus.REJECTED);
            }
        });

        status.setOptionsList(Arrays.asList(TimeEntryStatus.values()));
        user.setOptionsList(projectsService.getManagedUsers(userSession.getCurrentOrSubstitutedUser(), View.MINIMAL));
    }

    protected void setStatus(final Collection<TimeEntry> timeEntries, final TimeEntryStatus timeEntryStatus) {
        showOptionDialog(getMessage("notification.confirmation"), getMessage("notification.confirmationText"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                super.actionPerform(component);
                                for (TimeEntry timeEntry : timeEntries) {
                                    timeEntry.setStatus(timeEntryStatus);
                                }

                                getDsContext().commit();
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                });
    }
}