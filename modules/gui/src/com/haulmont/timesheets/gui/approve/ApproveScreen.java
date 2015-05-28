/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui
 */
package com.haulmont.timesheets.gui.approve;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.View;
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
import com.haulmont.timesheets.gui.timeentry.TimeEntryEdit;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

/**
 * @author gorelov
 */
public class ApproveScreen extends AbstractWindow {

    public interface Companion {
        void initTable(Table table);
    }

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
    protected OptionsGroup typeOption;
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
    @Inject
    protected Companion companion;

    protected final String totalColumnId = "total";

    protected Map<String, Label> totalLabelsMap = new HashMap<>();

    protected Date firstDayOfWeek;
    protected Date lastDayOfWeek;
    protected List<Project> approvableProjects;

    @Override
    public void init(Map<String, Object> params) {
        if (companion != null) {
            companion.initTable(weeklyReportsTable);
        }

        setWeekRange(DateTimeUtils.getFirstDayOfWeek(timeSource.currentTimestamp()));
        approvableProjects = projectsService.getActiveManagedProjectsForUser(userSession.getUser(), View.LOCAL);

        initUsersTable();
        initUserReportsTable();
        initDateField();
        initStatusOption();
        initTypeOptions();

        updateWeek();
    }

    protected void initUsersTable() {
        final String actionsColumnId = "actions";
        usersTable.addGeneratedColumn(actionsColumnId, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                User user = (User) entity;

                return getApproveControls(
                        new UserChangeStatusAction("approve", user, TimeEntryStatus.APPROVED),
                        new UserChangeStatusAction("reject", user, TimeEntryStatus.REJECTED)
                );
            }
        });
        usersTable.setColumnWidth(actionsColumnId, 90);
        usersTable.setColumnCaption(actionsColumnId, messages.getMessage(getClass(), actionsColumnId));

        usersDs.addListener(new CollectionDsListenerAdapter<ExtUser>() {
            @Override
            public void itemChanged(Datasource<ExtUser> ds, ExtUser prevItem, ExtUser item) {
                super.itemChanged(ds, prevItem, item);
                updateReportTableItems();
            }
        });
    }

    protected void initUserReportsTable() {

        initProjectColumn();
        initTaskColumn();
        initDaysColumns();
        initTotalColumn();
        initActionsColumn();

        weeklyEntriesDs.addListener(new CollectionDsListenerAdapter<WeeklyReportEntry>() {
            @Override
            public void collectionChanged(CollectionDatasource ds, Operation operation, List<WeeklyReportEntry> items) {
                if (Operation.REMOVE.equals(operation) || Operation.CLEAR.equals(operation)) {
                    for (WeeklyReportEntry entry : items) {
                        totalLabelsMap.remove(ComponentsHelper.getCacheKeyForEntity(entry, totalColumnId));
                    }
                }
            }
        });

        weeklyReportsTable.setStyleProvider(new Table.StyleProvider() {
            @Nullable
            @Override
            public String getStyleName(Entity entity, String property) {
                WeeklyReportEntry reportEntry = (WeeklyReportEntry) entity;
                String id = null;
                if (property != null && property.endsWith("Column")) {
                    id = property.replace("Column", "");
                }
                DayOfWeek day = DayOfWeek.fromId(id != null ? id : property);
                if (day != null) {
                    List<TimeEntry> timeEntries = reportEntry.getDayOfWeekTimeEntries(day);
                    if (CollectionUtils.isNotEmpty(timeEntries)) {
                        return ComponentsHelper.getTimeEntryStatusStyleBg(timeEntries);
                    }
                }
                return null;
            }
        });
    }

    protected void initProjectColumn() {
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
    }

    protected void initTaskColumn() {
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
    }

    protected void initDaysColumns() {
        for (Date current = firstDayOfWeek; current.getTime() <= lastDayOfWeek.getTime(); current = DateUtils.addDays(current, 1)) {
            final DayOfWeek day = DayOfWeek.fromCalendarDay(DateUtils.toCalendar(current).get(Calendar.DAY_OF_WEEK));
            final String columnId = day.getId() + "Column";
            final Date finalCurrent = current;
            weeklyReportsTable.addGeneratedColumn(columnId, new Table.ColumnGenerator() {
                @Override
                public Component generateCell(final Entity entity) {
                    final WeeklyReportEntry reportEntry = (WeeklyReportEntry) entity;
                    List<TimeEntry> timeEntries = reportEntry.getDayOfWeekTimeEntries(day);
                    if (CollectionUtils.isNotEmpty(timeEntries)) {
                        if (timeEntries.size() == 1) {
                            final TimeEntry timeEntry = timeEntries.get(0);
                            final LinkButton linkButton = componentsFactory.createComponent(LinkButton.NAME);
                            linkButton.setCaption(reportEntry.getTotalForDay(day));
                            linkButton.setAction(new AbstractAction("edit") {
                                @Override
                                public void actionPerform(Component component) {
                                    openTimeEntryEditor(timeEntry, linkButton, reportEntry, day);
                                }
                            });
                            return linkButton;
                        } else {
                            final LinkButton linkButton = componentsFactory.createComponent(LinkButton.NAME);
                            linkButton.setCaption(reportEntry.getTotalForDay(day));
                            linkButton.setAction(new AbstractAction("edit") {

                                @Override
                                public void actionPerform(Component component) {
                                    openLookup(
                                            "ts$TimeEntry.lookup",
                                            new Lookup.Handler() {
                                                @Override
                                                public void handleLookup(Collection items) {
                                                    if (CollectionUtils.isNotEmpty(items)) {
                                                        TimeEntry timeEntry = (TimeEntry) items.iterator().next();
                                                        openTimeEntryEditor(timeEntry, linkButton, reportEntry, day);
                                                    }
                                                }
                                            },
                                            WindowManager.OpenType.DIALOG,
                                            ParamsMap.of("date", finalCurrent, "taskId", reportEntry.getTask().getId()));
                                }
                            });
                            return linkButton;
                        }
                    }
                    return null;
                }
            });
            weeklyReportsTable.setColumnWidth(columnId, 80);
            weeklyReportsTable.setColumnCaption(columnId, ComponentsHelper.getColumnCaption(day.getId(), current));
        }
    }

    protected void initTotalColumn() {
        weeklyReportsTable.addGeneratedColumn(totalColumnId, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                WeeklyReportEntry reportEntry = (WeeklyReportEntry) entity;
                Label label = componentsFactory.createComponent(Label.NAME);
                label.setValue(reportEntry.getTotal());
                totalLabelsMap.put(ComponentsHelper.getCacheKeyForEntity(reportEntry, totalColumnId), label);
                return label;
            }
        });
        weeklyReportsTable.setColumnWidth(totalColumnId, 80);
        weeklyReportsTable.setColumnCaption(totalColumnId, messages.getMessage(getClass(), "total"));
    }

    protected void initActionsColumn() {
        final String actionsColumnId = "actions";
        weeklyReportsTable.addGeneratedColumn(actionsColumnId, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                WeeklyReportEntry reportEntry = (WeeklyReportEntry) entity;
                User user = usersTable.getSingleSelected();

                return isApprovableEntry(reportEntry) ? getApproveControls(
                        new WeeklyReportChangeStatusAction("approve", user, reportEntry, TimeEntryStatus.APPROVED),
                        new WeeklyReportChangeStatusAction("reject", user, reportEntry, TimeEntryStatus.REJECTED)
                ) : null;
            }
        });
        weeklyReportsTable.setColumnWidth(actionsColumnId, 90);
        weeklyReportsTable.setColumnCaption(actionsColumnId, messages.getMessage(getClass(), actionsColumnId));
    }

    protected void openTimeEntryEditor(
            TimeEntry timeEntry, final LinkButton linkButton, final WeeklyReportEntry reportEntry, final DayOfWeek day) {
        final TimeEntryEdit editor = openEditor(
                "ts$TimeEntry.edit", timeEntry, WindowManager.OpenType.DIALOG);
        editor.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (COMMIT_ACTION_ID.equals(actionId)) {
                    updateReportTableItems();
                }
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

    protected boolean isApprovableEntry(WeeklyReportEntry reportEntry) {
        return approvableProjects.contains(reportEntry.getProject());
    }

    protected void initDateField() {
        dateField.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                setWeekRange(DateTimeUtils.getFirstDayOfWeek((Date) value));
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

    protected void initTypeOptions() {
        String all = messages.getMessage(getClass(), "all");
        typeOption.setOptionsList(Arrays.asList(messages.getMessage(getClass(), "approvable"), all));
        typeOption.setValue(all);

        typeOption.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, @Nullable Object prevValue, @Nullable Object value) {
                updateReportTableItems();
            }
        });
    }

    protected void setWeekRange(Date start) {
        firstDayOfWeek = start;
        lastDayOfWeek = DateTimeUtils.getLastDayOfWeek(firstDayOfWeek);
    }

    public void setToday() {
        setWeekRange(DateTimeUtils.getFirstDayOfWeek(timeSource.currentTimestamp()));
        updateWeek();
    }

    public void showPreviousWeek() {
        setWeekRange(DateUtils.addWeeks(firstDayOfWeek, -1));
        updateWeek();
    }

    public void showNextWeek() {
        setWeekRange(DateUtils.addWeeks(firstDayOfWeek, 1));
        updateWeek();
    }

    protected void updateWeek() {
        updateWeekCaption();
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
                DateTimeUtils.getDateFormat().format(lastDayOfWeek)));
    }

    protected boolean showApprovable() {
        return StringUtils.equals(messages.getMessage(getClass(), "approvable"), (String) typeOption.getValue());
    }

    protected void fillExistingTimeEntries(User user) {
        List<TimeEntry> timeEntries = getUserTimeEntries(user, showApprovable());
        List<WeeklyReportEntry> reportEntries = reportConverterBean.convertFromTimeEntries(timeEntries);
        for (WeeklyReportEntry entry : reportEntries) {
            weeklyEntriesDs.addItem(entry);
        }
    }

    protected List<TimeEntry> getUserTimeEntries(User user, boolean isApprovable) {
        if (statusOption.getValue() == null) {
            return Collections.emptyList();
        }

        List<TimeEntry> timeEntries = new ArrayList<>();
        Collection<TimeEntryStatus> statuses = statusOption.getValue();
        for (TimeEntryStatus status : statuses) {
            timeEntries.addAll(getTimeEntriesForPeriod(firstDayOfWeek,
                    lastDayOfWeek, userSession.getUser(), user, status, isApprovable));
        }
        return timeEntries;
    }

    private Collection<? extends TimeEntry> getTimeEntriesForPeriod(
            Date start, Date end, User approver, User user, TimeEntryStatus status, boolean isApprovable) {
        if (isApprovable) {
            return projectsService.getApprovableTimeEntriesForPeriod(start, end, approver, user, status, "timeEntry-full");
        } else {
            return projectsService.getTimeEntriesForPeriod(start, end, user, status, "timeEntry-full");
        }
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
            return getUserTimeEntries(user, true);
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