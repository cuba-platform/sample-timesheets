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

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.RefreshAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.CollectionDatasource.Operation;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.global.DateTimeUtils;
import com.haulmont.timesheets.global.StringFormatHelper;
import com.haulmont.timesheets.global.ValidationTools;
import com.haulmont.timesheets.global.WeeklyReportConverter;
import com.haulmont.timesheets.gui.rejection.RejectionReason;
import com.haulmont.timesheets.gui.timeentry.TimeEntryEdit;
import com.haulmont.timesheets.gui.util.ComponentsHelper;
import com.haulmont.timesheets.gui.util.SecurityAssistant;
import com.haulmont.timesheets.gui.util.WeeklyReportEntryAggregation;
import com.haulmont.timesheets.gui.weeklytimesheets.TotalColumnAggregation;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.Calendar;

/**
 * @author gorelov
 */
@SuppressWarnings("WeakerAccess")
public class ApproveScreen extends AbstractWindow {

    protected static final String COLUMN_SUFFIX = "Column";
    protected static final String TOTAL_COLUMN_ID = "totalColumn";

    public interface Companion {
        void initTable(Table table);
    }

    @Inject
    private CheckBox hasTimeEntries;
    @Inject
    protected Table<ExtUser> usersTable;
    @Inject
    protected Table<WeeklyReportEntry> weeklyReportsTable;
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
    @Inject
    protected ValidationTools validationTools;
    @Inject
    protected SecurityAssistant securityAssistant;

    @Inject
    private LookupField user;

    @Inject
    private LookupField status;

    @Inject
    private Button refresh;

    @Inject
    private LookupField project;

    @Inject
    private LookupField task;

    protected Date firstDayOfWeek;
    protected Date lastDayOfWeek;
    protected List<Project> managedProjects;
    protected List<User> managedUsers;

    @Override
    public void init(Map<String, Object> params) {
        if (companion != null) {
            companion.initTable(weeklyReportsTable);
        }

        setWeekRange(DateTimeUtils.getFirstDayOfWeek(DateUtils.addWeeks(timeSource.currentTimestamp(), -1)));
        User currentOrSubstitutedUser = userSession.getCurrentOrSubstitutedUser();
        managedProjects = projectsService.getActiveManagedProjectsForUser(currentOrSubstitutedUser, View.LOCAL);
        managedUsers = projectsService.getManagedUsers(currentOrSubstitutedUser, View.LOCAL);

        initUsersTable();
        initUserReportsTable();
        initDateField();
        initStatusOption();
        initTypeOptions();

        updateWeek();

        project.addValueChangeListener(e -> task.setValue(null));
        status.setOptionsList(Arrays.asList(TimeEntryStatus.values()));
        refresh.setAction(new RefreshAction(usersTable));
        user.setOptionsList(projectsService.getManagedUsers(userSession.getCurrentOrSubstitutedUser(), View.MINIMAL));
    }

    protected void initUsersTable() {
        final String actionsColumnId = "actions";
        usersTable.addGeneratedColumn(actionsColumnId, entity -> {
            UserChangeStatusActionsProvider provider = new UserChangeStatusActionsProvider(entity);
            return getApproveControls(provider.getApproveAction(),
                    provider.getRejectAction(), provider.getCloseAction());
        });
        usersTable.setColumnWidth(actionsColumnId, 100);
        usersTable.setColumnCaption(actionsColumnId, messages.getMessage(getClass(), actionsColumnId));

        usersDs.addItemChangeListener(e -> {
            updateReportTableItems();
            updateStatusOption(e.getItem());
        });

        hasTimeEntries.setValue(true);
        hasTimeEntries.addValueChangeListener(e -> updateUsersTable());

    }

    protected void initUserReportsTable() {
        weeklyReportsTable.setSettingsEnabled(false);

        initProjectColumn();
        initTaskColumn();
        initDaysColumns();
        initTotalColumn();
        initActionsColumn();

        weeklyReportsTable.setStyleProvider((entity, property) -> {
            String id = null;
            if (property != null && property.endsWith(COLUMN_SUFFIX)) {
                id = property.replace(COLUMN_SUFFIX, "");
            }
            DayOfWeek day = DayOfWeek.fromId(id != null ? id : property);
            if (entity == null && usersTable.getSingleSelected() != null) {
                if (day != null) {
                    return validationTools.isWorkTimeMatchToPlanForDay(
                            DateTimeUtils.getSpecificDayOfWeek(firstDayOfWeek, day.getJavaCalendarDay()),
                            usersTable.<User>getSingleSelected()) ? null : "overtime";
                } else if (TOTAL_COLUMN_ID.equals(property)) {
                    return validationTools.isWorkTimeMatchToPlanForWeek(
                            firstDayOfWeek, usersTable.<User>getSingleSelected()) ? null : "overtime";
                }
            } else if (entity != null && day != null) {
                List<TimeEntry> timeEntries = entity.getDayOfWeekTimeEntries(day);
                if (CollectionUtils.isNotEmpty(timeEntries)) {
                    return ComponentsHelper.getTimeEntryStatusStyleBg(timeEntries);
                }
            }
            return null;
        });
    }

    protected void initProjectColumn() {
        final String projectColumnId = "project";
        weeklyReportsTable.addGeneratedColumn(projectColumnId, entity -> {
            Label label = componentsFactory.createComponent(Label.class);
            label.setValue(entity.getProject().getName());
            return label;
        });
    }

    protected void initTaskColumn() {
        final String taskColumnId = "task";
        weeklyReportsTable.addGeneratedColumn(taskColumnId, entity -> {
            Label label = componentsFactory.createComponent(Label.class);
            String caption;
            if (entity.getActivityType() == null) {
                caption = entity.getTask().getName();
            } else {
                caption = String.format("%s (%s)",
                        entity.getTask().getName(),
                        entity.getActivityType().getInstanceName());
            }
            label.setValue(caption);
            return label;
        });
    }

    protected void initDaysColumns() {
        for (Date current = firstDayOfWeek; current.getTime() <= lastDayOfWeek.getTime(); current = DateUtils.addDays(current, 1)) {
            final DayOfWeek day = DayOfWeek.fromCalendarDay(DateUtils.toCalendar(current).get(Calendar.DAY_OF_WEEK));
            final String columnId = day.getId() + COLUMN_SUFFIX;
            weeklyReportsTable.addGeneratedColumn(columnId, new Table.ColumnGenerator<WeeklyReportEntry>() {
                @Override
                public Component generateCell(final WeeklyReportEntry entity) {
                    List<TimeEntry> timeEntries = entity.getDayOfWeekTimeEntries(day);
                    if (CollectionUtils.isNotEmpty(timeEntries)) {
                        if (timeEntries.size() == 1) {
                            return createLinkToSingleTimeEntry(entity, timeEntries);
                        } else {
                            return createLinkToMultipleTimeEntries(entity, timeEntries.get(0).getDate());
                        }
                    }
                    return null;
                }

                private Component createLinkToMultipleTimeEntries(final WeeklyReportEntry reportEntry, final Date date) {
                    final LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
                    linkButton.setCaption(StringFormatHelper.getDayHoursString(reportEntry.getTotalForDay(day)));
                    linkButton.setAction(new AbstractAction("edit") {

                        @Override
                        public void actionPerform(Component component) {
                            User user = usersTable.getSingleSelected();
                            if (user != null) {
                                openLookup(
                                        "ts$TimeEntry.lookup",
                                        items -> {
                                            if (CollectionUtils.isNotEmpty(items)) {
                                                TimeEntry timeEntry = (TimeEntry) items.iterator().next();
                                                openTimeEntryEditor(timeEntry);
                                            }
                                        },
                                        WindowManager.OpenType.DIALOG,
                                        ParamsMap.of("date", date,
                                                "task", reportEntry.getTask(),
                                                "activityType", reportEntry.getActivityType(),
                                                "user", user.getId()));
                            }
                        }
                    });
                    return linkButton;
                }

                private Component createLinkToSingleTimeEntry(WeeklyReportEntry reportEntry, List<TimeEntry> timeEntries) {
                    final TimeEntry timeEntry = timeEntries.get(0);
                    final LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
                    linkButton.setCaption(StringFormatHelper.getDayHoursString(reportEntry.getTotalForDay(day)));
                    linkButton.setAction(new AbstractAction("edit") {
                        @Override
                        public void actionPerform(Component component) {
                            openTimeEntryEditor(timeEntry);
                        }
                    });
                    return linkButton;
                }
            });
            weeklyReportsTable.setColumnWidth(columnId, 80);

            Table.Column column = weeklyReportsTable.getColumn(columnId);
            column.setAggregation(ComponentsHelper.createAggregationInfo(
                    projectsService.getEntityMetaPropertyPath(WeeklyReportEntry.class, day.getId()),
                    new WeeklyReportEntryAggregation()
            ));
        }
    }

    protected void initTotalColumn() {
        weeklyReportsTable.addGeneratedColumn(TOTAL_COLUMN_ID, entity -> {
            Label label = componentsFactory.createComponent(Label.class);
            label.setValue(entity.getTotal());
            return label;
        });
        weeklyReportsTable.setColumnWidth(TOTAL_COLUMN_ID, 80);
        weeklyReportsTable.setColumnCaption(TOTAL_COLUMN_ID, messages.getMessage(getClass(), "total"));

        Table.Column column = weeklyReportsTable.getColumn(TOTAL_COLUMN_ID);
        column.setAggregation(ComponentsHelper.createAggregationInfo(
                projectsService.getEntityMetaPropertyPath(WeeklyReportEntry.class, "total"),
                new TotalColumnAggregation()
        ));
    }

    protected void initActionsColumn() {
        final String actionsColumnId = "actions";
        weeklyReportsTable.addGeneratedColumn(actionsColumnId, entity -> {
            User user1 = usersTable.getSingleSelected();
            WeeklyReportChangeStatusActionProvider provider = new WeeklyReportChangeStatusActionProvider(user1, entity);
            return getApproveControls(provider.getApproveAction(),
                    provider.getRejectAction(), provider.getCloseAction());
        });
        weeklyReportsTable.setColumnWidth(actionsColumnId, 100);
        weeklyReportsTable.setColumnCaption(actionsColumnId, messages.getMessage(getClass(), actionsColumnId));
    }

    protected void openTimeEntryEditor(
            TimeEntry timeEntry) {
        final TimeEntryEdit editor = (TimeEntryEdit) openEditor(
                "ts$TimeEntry.edit", timeEntry, WindowManager.OpenType.DIALOG);
        editor.addListener(actionId -> {
            if (COMMIT_ACTION_ID.equals(actionId)) {
                updateReportTableItems();
            }
        });
    }

    protected Component getApproveControls(@Nullable Action approveAction,
                                           @Nullable Action rejectAction,
                                           @Nullable Action closeAction) {
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        hBoxLayout.setSpacing(true);
        hBoxLayout.setWidth("100%");

        if (approveAction != null) {
            hBoxLayout.add(ComponentsHelper.createCaptionlessLinkButton("icons/ok.png",
                    messages.getMessage(getClass(), AbstractChangeStatusAction.APPROVE_ACTION_ID),
                    approveAction));
        }
        if (rejectAction != null) {
            hBoxLayout.add(ComponentsHelper.createCaptionlessLinkButton("icons/remove.png",
                    messages.getMessage(getClass(), AbstractChangeStatusAction.REJECT_ACTION_ID),
                    rejectAction));
        }
        if (closeAction != null) {
            hBoxLayout.add(ComponentsHelper.createCaptionlessLinkButton("font-icon:LOCK",
                    messages.getMessage(getClass(), AbstractChangeStatusAction.CLOSE_ACTION_ID),
                    closeAction));
        }

        return hBoxLayout;
    }

    protected void initDateField() {
        dateField.addValueChangeListener(e -> {
            setWeekRange(DateTimeUtils.getFirstDayOfWeek((Date) e.getValue()));
            updateWeek();
        });
    }

    protected void initStatusOption() {
        statusOption.setOptionsList(Arrays.asList(TimeEntryStatus.values()));
        statusOption.addValueChangeListener(e -> updateReportTableItems());
    }

    protected void initTypeOptions() {
        String all = messages.getMessage(getClass(), "all");
        typeOption.setOptionsList(Arrays.asList(messages.getMessage(getClass(), "approvable"), all));
        typeOption.setValue(all);

        typeOption.addValueChangeListener(e -> updateReportTableItems());
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
        updateUsersTable();
        updateReportTableItems();
        updateDayColumnsCaptions();
    }

    protected void updateUsersTable() {
        Map<String, Object> params = new HashMap<>();
        params.put("from", firstDayOfWeek);
        params.put("to", lastDayOfWeek);
        params.put("hasTimeEntries", Boolean.TRUE.equals(hasTimeEntries.getValue()));
        usersDs.refresh(params);
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

    protected void updateDayColumnsCaptions() {
        for (Date current = firstDayOfWeek; current.getTime() <= lastDayOfWeek.getTime(); current = DateUtils.addDays(current, 1)) {
            DayOfWeek day = DayOfWeek.fromCalendarDay(DateTimeUtils.getCalendarDayOfWeek(current));
            String columnId = day.getId() + COLUMN_SUFFIX;
            weeklyReportsTable.setColumnCaption(columnId, ComponentsHelper.getColumnCaption(day.getId(), current));
        }
    }

    protected void updateStatusOption(User user) {
        List<TimeEntryStatus> values = new ArrayList<>(statusOption.getValue());
        if (securityAssistant.isSuperUser() || managedUsers.contains(user)) {
            if (!values.contains(TimeEntryStatus.NEW)) {
                values.add(TimeEntryStatus.NEW);
            }
        }
        if (isUserCanClose()) {
            if (!values.contains(TimeEntryStatus.APPROVED)) {
                values.add(TimeEntryStatus.APPROVED);
            }
        }
        statusOption.setValue(values);
    }

    protected boolean showApprovable() {
        return StringUtils.equals(messages.getMessage(getClass(), "approvable"), typeOption.getValue());
    }

    protected void fillExistingTimeEntries(User user) {
        List<TimeEntry> timeEntries = new TimeEntriesProvider(user).setApprovable(showApprovable()).getUserTimeEntries();
        List<WeeklyReportEntry> reportEntries = reportConverterBean.convertFromTimeEntries(timeEntries);
        for (WeeklyReportEntry entry : reportEntries) {
            weeklyEntriesDs.addItem(entry);
        }
    }

    protected boolean isUserCanClose() {
        return securityAssistant.isSuperUser() || securityAssistant.isUserCloser();
    }

    protected abstract class AbstractChangeStatusAction extends AbstractAction {

        public static final String APPROVE_ACTION_ID = "approve";
        public static final String REJECT_ACTION_ID = "reject";
        public static final String CLOSE_ACTION_ID = "close";

        protected final User user;
        protected final TimeEntryStatus status;
        protected String rejectReason;

        protected AbstractChangeStatusAction(String id, User user, TimeEntryStatus status) {
            super(id);
            this.user = user;
            this.status = status;
        }

        @Override
        public void actionPerform(Component component) {
            rejectReason = null;
            final List<TimeEntry> timeEntries = getTimeEntries();
            if (timeEntries.isEmpty()) {
                return;
            }
            if (TimeEntryStatus.REJECTED.equals(status)) {
                final RejectionReason rejectionReasonWindow = (RejectionReason) openWindow(
                        "rejection-reason", WindowManager.OpenType.DIALOG);
                rejectionReasonWindow.addListener(actionId -> {
                    if (RejectionReason.CONFIRM_ACTION_AD.equals(actionId)) {
                        rejectReason = rejectionReasonWindow.getRejectionReason();
                        commitTimeEntries(timeEntries);
                    }
                });
            } else {
                commitTimeEntries(timeEntries);
            }
        }

        protected void commitTimeEntries(List<TimeEntry> timeEntries) {
            CommitContext commitContext = new CommitContext();
            for (TimeEntry entry : timeEntries) {
                entry.setStatus(status);
                entry.setRejectionReason(rejectReason);
                commitContext.getCommitInstances().add(entry);
            }
            getDsContext().getDataSupplier().commit(commitContext);

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
            TimeEntriesProvider provider = new TimeEntriesProvider(user);
            if (CLOSE_ACTION_ID.equals(id)) {
                provider.setSpecificStatus(TimeEntryStatus.APPROVED);
            } else {
                if (!securityAssistant.isSuperUser()) {
                    provider.setApprovable(true);
                }
                provider.addExcludeStatus(TimeEntryStatus.CLOSED);
                if (REJECT_ACTION_ID.equals(id)) {
                    provider.addExcludeStatus(TimeEntryStatus.REJECTED);
                }
            }
            return provider.getUserTimeEntries();
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
            List<TimeEntry> exist = weeklyReportEntry.getExistTimeEntries();
            if (exist.isEmpty()) {
                return Collections.emptyList();
            }
            TimeEntriesProvider provider = new TimeEntriesProvider(exist);
            if (CLOSE_ACTION_ID.equals(id)) {
                provider.setSpecificStatus(TimeEntryStatus.APPROVED);
            } else {
                provider.addExcludeStatus(TimeEntryStatus.CLOSED);
                if (REJECT_ACTION_ID.equals(id)) {
                    provider.addExcludeStatus(TimeEntryStatus.REJECTED);
                }
            }
            return provider.getUserTimeEntries();
        }
    }

    @SuppressWarnings("unused")
    protected abstract class AbstractChangeStatusActionsProvider {
        protected User user;

        protected AbstractChangeStatusActionsProvider(User user) {
            this.user = user;
        }

        @Nullable
        public abstract AbstractChangeStatusAction getApproveAction();

        @Nullable
        public abstract AbstractChangeStatusAction getRejectAction();

        @Nullable
        public abstract AbstractChangeStatusAction getCloseAction();
    }

    protected class UserChangeStatusActionsProvider extends AbstractChangeStatusActionsProvider {

        public UserChangeStatusActionsProvider(User user) {
            super(user);
        }

        @Nullable
        @Override
        public AbstractChangeStatusAction getApproveAction() {
            return securityAssistant.isSuperUser() || isApprovableUser()
                    ? new UserChangeStatusAction(AbstractChangeStatusAction.APPROVE_ACTION_ID,
                    user, TimeEntryStatus.APPROVED)
                    : null;
        }

        @Nullable
        @Override
        public AbstractChangeStatusAction getRejectAction() {
            return securityAssistant.isSuperUser() || isApprovableUser()
                    ? new UserChangeStatusAction(AbstractChangeStatusAction.REJECT_ACTION_ID,
                    user, TimeEntryStatus.REJECTED)
                    : null;
        }

        @Nullable
        @Override
        public AbstractChangeStatusAction getCloseAction() {
            return isUserCanClose()
                    ? new UserChangeStatusAction(AbstractChangeStatusAction.CLOSE_ACTION_ID,
                    user, TimeEntryStatus.CLOSED)
                    : null;
        }

        protected boolean isApprovableUser() {
            return managedUsers.contains(user);
        }
    }

    protected class WeeklyReportChangeStatusActionProvider extends AbstractChangeStatusActionsProvider {

        protected WeeklyReportEntry reportEntry;

        public WeeklyReportChangeStatusActionProvider(User user, WeeklyReportEntry reportEntry) {
            super(user);
            this.reportEntry = reportEntry;
        }

        @Nullable
        @Override
        public AbstractChangeStatusAction getApproveAction() {
            return securityAssistant.isSuperUser() || isApprovableEntry(reportEntry)
                    ? new WeeklyReportChangeStatusAction(AbstractChangeStatusAction.APPROVE_ACTION_ID,
                    user, reportEntry, TimeEntryStatus.APPROVED)
                    : null;
        }

        @Nullable
        @Override
        public AbstractChangeStatusAction getRejectAction() {
            return securityAssistant.isSuperUser() || isApprovableEntry(reportEntry)
                    ? new WeeklyReportChangeStatusAction(AbstractChangeStatusAction.REJECT_ACTION_ID,
                    user, reportEntry, TimeEntryStatus.REJECTED)
                    : null;
        }

        @Nullable
        @Override
        public AbstractChangeStatusAction getCloseAction() {
            return isUserCanClose()
                    ? new WeeklyReportChangeStatusAction(AbstractChangeStatusAction.CLOSE_ACTION_ID,
                    user, reportEntry, TimeEntryStatus.CLOSED)
                    : null;
        }

        protected boolean isApprovableEntry(WeeklyReportEntry reportEntry) {
            return managedProjects.contains(reportEntry.getProject());
        }
    }

    protected class TimeEntriesProvider {

        protected User user;
        protected boolean isApprovable = false;
        protected TimeEntryStatus specificStatus = null;
        protected List<TimeEntryStatus> excludeStatuses = null;
        protected List<TimeEntry> fixedTimeEntries = null;

        public TimeEntriesProvider(User user) {
            this.user = user;
        }

        public TimeEntriesProvider(List<TimeEntry> timeEntries) {
            this.fixedTimeEntries = timeEntries;
        }

        public TimeEntriesProvider setApprovable(boolean isApprovable) {
            this.isApprovable = isApprovable;
            return this;
        }

        public TimeEntriesProvider setSpecificStatus(TimeEntryStatus specificStatus) {
            this.specificStatus = specificStatus;
            return this;
        }

        public TimeEntriesProvider addExcludeStatus(TimeEntryStatus excludeStatus) {
            if (this.excludeStatuses == null) {
                this.excludeStatuses = new ArrayList<>();
            }
            this.excludeStatuses.add(excludeStatus);
            return this;
        }

        public List<TimeEntry> getUserTimeEntries() {
            if (statusOption.getValue() == null) {
                return Collections.emptyList();
            }

            List<TimeEntryStatus> statuses = new ArrayList<>(statusOption.getValue());
            if (specificStatus != null) {
                if (statuses.contains(specificStatus)) {
                    statuses = Collections.singletonList(specificStatus);
                } else {
                    return Collections.emptyList();
                }
            }

            if (excludeStatuses != null) {
                for (TimeEntryStatus excludeStatus : excludeStatuses) {
                    statuses.remove(excludeStatus);
                }
            }
            List<TimeEntry> timeEntries = new ArrayList<>();
            if (fixedTimeEntries == null) {
                for (TimeEntryStatus status : statuses) {
                    timeEntries.addAll(getTimeEntriesForPeriod(firstDayOfWeek, lastDayOfWeek, status));
                }
            } else {
                for (TimeEntry timeEntry : fixedTimeEntries) {
                    if (statuses.contains(timeEntry.getStatus())) {
                        timeEntries.add(timeEntry);
                    }
                }
            }
            return timeEntries;
        }

        protected Collection<? extends TimeEntry> getTimeEntriesForPeriod(
                Date start, Date end, TimeEntryStatus status) {
            if (isApprovable) {
                return projectsService.getApprovableTimeEntriesForPeriod(start, end, userSession.getCurrentOrSubstitutedUser(), user, status, "timeEntry-full");
            } else {
                return projectsService.getTimeEntriesForPeriod(start, end, user, status, "timeEntry-full");
            }
        }
    }
}