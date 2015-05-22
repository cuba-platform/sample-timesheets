/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui
 */
package com.haulmont.timesheets.gui.approve;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.data.impl.CollectionDsListenerAdapter;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.global.DateTimeUtils;
import com.haulmont.timesheets.global.WeeklyReportConverter;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

/**
 * @author gorelov
 */
public class ApproveScreen extends AbstractWindow {

    @Inject
    protected Table usersTable;
    @Inject
    protected Table weeklyReportsTable;
    @Inject
    protected DateField dateField;
    @Inject
    protected Label weekCaption;
    @Inject
    protected OptionsGroup statusOption;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected CollectionDatasource<ExtUser, UUID> usersDs;
    @Inject
    protected CollectionDatasource<WeeklyReportEntry, UUID> weeklyEntriesDs;
    @Inject
    protected Messages messages;
    @Inject
    protected UserSession userSession;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected WeeklyReportConverter reportConverterBean;

    protected Date firstDayOfWeek;

    @Override
    public void init(Map<String, Object> params) {
        firstDayOfWeek = DateTimeUtils.getFirstDayOfWeek(timeSource.currentTimestamp());

        initUsersTable();
        initUserReportsTable();
        initDateField();
        initStatusOption();

        updateWeek();
    }

    protected void initUsersTable() {
        usersTable.addGeneratedColumn("actions", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                User user = (User) entity;

                return getApproveControls(
                        new UserChangeStatusAction("approve", user, TimeEntryStatus.APPROVED),
                        new UserChangeStatusAction("reject", user, TimeEntryStatus.REJECTED)
                );
            }
        });

        usersDs.addListener(new CollectionDsListenerAdapter<ExtUser>() {
            @Override
            public void itemChanged(Datasource<ExtUser> ds, ExtUser prevItem, ExtUser item) {
                super.itemChanged(ds, prevItem, item);
                updateReportTableItems();
            }
        });
    }

    protected void initUserReportsTable() {
        final String projectColumnId = "project";
        weeklyReportsTable.addGeneratedColumn(projectColumnId, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                WeeklyReportEntry weeklyReportEntry = (WeeklyReportEntry) entity;
                Label label = componentsFactory.createComponent(Label.NAME);
                label.setValue(weeklyReportEntry.getProject().getName());
                return label;
            }
        });

        final String taskColumnId = "task";
        weeklyReportsTable.addGeneratedColumn(taskColumnId, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                WeeklyReportEntry weeklyReportEntry = (WeeklyReportEntry) entity;
                Label label = componentsFactory.createComponent(Label.NAME);
                label.setValue(weeklyReportEntry.getTask().getName());
                return label;
            }
        });

        final String totalColumnId = "total";
        for (final DayOfWeek day : DayOfWeek.values()) {
            weeklyReportsTable.addGeneratedColumn(day.getId(), new Table.ColumnGenerator() {
                @Override
                public Component generateCell(final Entity entity) {
                    EntityLinkField linkField = componentsFactory.createComponent(EntityLinkField.NAME);
                    linkField.setOwner(weeklyReportsTable);
                    linkField.setScreenOpenType(WindowManager.OpenType.DIALOG);
                    linkField.setDatasource(weeklyReportsTable.getItemDatasource(entity), day.getId());
                    linkField.addListener(new ValueListener() {
                        @Override
                        public void valueChanged(Object source, String property, Object prevValue, Object value) {
                            weeklyReportsTable.refresh();
                        }
                    });
                    return linkField;
                }
            });
        }

        weeklyReportsTable.addGeneratedColumn(totalColumnId, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                WeeklyReportEntry reportEntry = (WeeklyReportEntry) entity;
                Label label = componentsFactory.createComponent(Label.NAME);
                label.setValue(reportEntry.getTotal());
                return label;
            }
        });
        weeklyReportsTable.setColumnWidth(totalColumnId, 80);
        weeklyReportsTable.setColumnCaption(totalColumnId, messages.getMessage(getClass(), "total"));

        weeklyReportsTable.addGeneratedColumn("actions", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                WeeklyReportEntry reportEntry = (WeeklyReportEntry) entity;
                User user = usersTable.getSingleSelected();

                return getApproveControls(
                        new WeeklyReportChangeStatusAction("approve", user, reportEntry, TimeEntryStatus.APPROVED),
                        new WeeklyReportChangeStatusAction("reject", user, reportEntry, TimeEntryStatus.REJECTED)
                );
            }
        });

        weeklyReportsTable.setStyleProvider(new Table.StyleProvider() {
            @Nullable
            @Override
            public String getStyleName(Entity entity, String property) {
                WeeklyReportEntry reportEntry = (WeeklyReportEntry) entity;
                DayOfWeek day = DayOfWeek.fromId(property);
                if (day != null) {
                    TimeEntry timeEntry = reportEntry.getDayOfWeekTimeEntry(day);
                    if (timeEntry != null) {
                        return ComponentsHelper.getTimeEntryStatusStyleBg(timeEntry);
                    }
                }
                return null;
            }
        });
    }

    protected Component getApproveControls(Action approveAction, Action rejectAction) {
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.NAME);
        hBoxLayout.setSpacing(true);
        hBoxLayout.setWidth("100%");

        hBoxLayout.add(ComponentsHelper.createCaptionlessLinkButton("icons/ok.png",
                messages.getMessage(getClass(), "approve"),
                approveAction));

        hBoxLayout.add(ComponentsHelper.createCaptionlessLinkButton("icons/remove.png",
                messages.getMessage(getClass(), "reject"),
                rejectAction));

        return hBoxLayout;
    }

    protected void initDateField() {
        dateField.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                firstDayOfWeek = DateTimeUtils.getFirstDayOfWeek((Date) value);
                updateWeek();
            }
        });
    }

    protected void initStatusOption() {
        statusOption.setOptionsList(Arrays.asList(TimeEntryStatus.values()));
        statusOption.setValue(Collections.singletonList(TimeEntryStatus.NEW));

        statusOption.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, @Nullable Object prevValue, @Nullable Object value) {
                updateReportTableItems();
            }
        });
    }

    public void setToday() {
        firstDayOfWeek = DateTimeUtils.getFirstDayOfWeek(timeSource.currentTimestamp());
        updateWeek();
    }

    public void showPreviousWeek() {
        firstDayOfWeek = DateUtils.addDays(firstDayOfWeek, -7);
        updateWeek();
    }

    public void showNextWeek() {
        firstDayOfWeek = DateUtils.addDays(firstDayOfWeek, 7);
        updateWeek();
    }

    protected void updateWeek() {
        updateWeekCaption();
        ComponentsHelper.updateWeeklyReportTableCaptions(weeklyReportsTable, firstDayOfWeek);
        updateReportTableItems();
    }

    protected void updateReportTableItems() {
        weeklyEntriesDs.clear();
        User user = usersTable.getSingleSelected();
        if (user != null) {
            fillExistingTimeEntries(user);
            weeklyReportsTable.repaint();
        }
    }

    protected void updateWeekCaption() {
        weekCaption.setValue(String.format("%s - %s",
                DateTimeUtils.getDateFormat().format(firstDayOfWeek),
                DateTimeUtils.getDateFormat().format(DateUtils.addDays(firstDayOfWeek, 6))));
    }

    protected void fillExistingTimeEntries(User user) {
        List<TimeEntry> timeEntries = getUserTimeEntries(user);
        List<WeeklyReportEntry> reportEntries = reportConverterBean.convertFromTimeEntries(timeEntries);
        for (WeeklyReportEntry entry : reportEntries) {
            weeklyEntriesDs.addItem(entry);
        }
    }

    protected List<TimeEntry> getUserTimeEntries(User user) {
        if (statusOption.getValue() == null) {
            return Collections.emptyList();
        }

        List<TimeEntry> timeEntries = new ArrayList<>();
        Collection<TimeEntryStatus> statuses = statusOption.getValue();
        for (TimeEntryStatus status : statuses) {
            timeEntries.addAll(projectsService.getApprovableTimeEntriesForPeriod(firstDayOfWeek,
                    DateUtils.addDays(firstDayOfWeek, 6), userSession.getUser(), user, status));
        }
        return timeEntries;
    }

    protected abstract class AbstractChangeStatusAction extends AbstractAction {

        protected final User user;
        protected final TimeEntryStatus status;

        protected AbstractChangeStatusAction(String id, User user, TimeEntryStatus status) {
            super(id);
            this.user = user;
            this.status = status;
        }

        @Override
        public void actionPerform(Component component) {

            projectsService.updateTimeEntriesStatus(getTimeEntries(), status);
            updateReportTableItems();
        }

        protected abstract List<TimeEntry> getTimeEntries();

        @Override
        public String getCaption() {
            return null;
        }
    }

    protected class UserChangeStatusAction extends AbstractChangeStatusAction {

        protected UserChangeStatusAction(String id, User user, TimeEntryStatus status) {
            super(id, user, status);
        }

        @Override
        protected List<TimeEntry> getTimeEntries() {
            return getUserTimeEntries(user);
        }
    }

    protected class WeeklyReportChangeStatusAction extends AbstractChangeStatusAction {

        protected final WeeklyReportEntry weeklyReportEntry;

        protected WeeklyReportChangeStatusAction(String id, User user, WeeklyReportEntry weeklyReportEntry, TimeEntryStatus status) {
            super(id, user, status);
            this.weeklyReportEntry = weeklyReportEntry;
        }

        @Override
        protected List<TimeEntry> getTimeEntries() {
            return weeklyReportEntry.getExistTimeEntries();
        }
    }
}