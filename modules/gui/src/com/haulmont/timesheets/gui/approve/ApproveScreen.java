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

import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.global.DateTimeUtils;
import com.haulmont.timesheets.global.StringFormatHelper;
import com.haulmont.timesheets.global.ValidationTools;
import com.haulmont.timesheets.global.WeeklyReportConverter;
import com.haulmont.timesheets.gui.rejection.RejectionReason;
import com.haulmont.timesheets.gui.timeentry.TimeEntryEdit;
import com.haulmont.timesheets.gui.timeentry.TimeEntryLookup;
import com.haulmont.timesheets.gui.util.ScreensHelper;
import com.haulmont.timesheets.gui.util.SecurityAssistant;
import com.haulmont.timesheets.gui.util.WeeklyReportEntryAggregation;
import com.haulmont.timesheets.gui.weeklytimesheets.TotalColumnAggregation;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.*;

/**
 * @author gorelov
 */
@UiController("approve-screen")
@UiDescriptor("approve-screen.xml")
public class ApproveScreen extends Screen {

    protected static final String COLUMN_SUFFIX = "Column";
    protected static final String TOTAL_COLUMN_ID = "totalColumn";

    @Inject
    protected TimeSource timeSource;
    @Inject
    protected UserSession userSession;
    @Inject
    protected UiComponents uiComponents;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected MetadataTools metadataTools;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected SecurityAssistant securityAssistant;
    @Inject
    protected ValidationTools validationTools;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected WeeklyReportConverter weeklyReportConverter;
    @Inject
    protected Messages messages;

    @Inject
    protected CollectionLoader<Project> projectsDl;
    @Inject
    protected CollectionLoader<Group> groupsDl;
    @Inject
    protected LookupField status;
    @Inject
    protected LookupField user;
    @Inject
    protected LookupField<Task> task;
    @Inject
    protected LookupField<Project> project;
    @Inject
    protected Table<ExtUser> usersTable;
    @Inject
    protected CheckBox hasTimeEntries;
    @Inject
    protected CollectionContainer<ExtUser> usersDc;
    @Inject
    protected RadioButtonGroup typeOption;
    @Inject
    protected CheckBoxGroup statusOption;
    @Inject
    protected DateField<java.sql.Date> dateField;
    @Inject
    protected Table<WeeklyReportEntry> weeklyReportsTable;
    @Inject
    protected CollectionLoader<ExtUser> usersDl;
    @Inject
    protected CollectionLoader<Task> tasksDl;
    @Inject
    protected LookupField<Group> group;
    @Inject
    protected CollectionContainer<WeeklyReportEntry> weeklyEntriesDc;
    @Inject
    protected Label<String> weekCaption;

    protected Date firstDayOfWeek;
    protected Date lastDayOfWeek;
    protected List<Project> managedProjects;
    protected List<User> managedUsers;

    protected boolean isSuperUser = false;

    public void setSuperUser(boolean superUser) {
        isSuperUser = superUser;
    }

    @Install(to = "weeklyReportsTable", subject = "styleProvider")
    private String weeklyReportsTableStyleProvider(WeeklyReportEntry entity, String property) {
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
                return ScreensHelper.getTimeEntryStatusStyleBg(timeEntries);
            }
        }
        return null;
    }

    @Subscribe(id = "projectsDc", target = Target.DATA_CONTAINER)
    protected void onProjectsDcItemChange(InstanceContainer.ItemChangeEvent<Project> event) {
        tasksDl.setParameter("project", event.getItem());
        tasksDl.load();
    }

    @Subscribe("usersTable.refresh")
    protected void onUsersTableRefresh(Action.ActionPerformedEvent event) {
        updateUsersTable();
    }

    @Subscribe
    private void onInit(InitEvent event) {
        setWeekRange(DateTimeUtils.getFirstDayOfWeek(DateUtils.addWeeks(timeSource.currentTimestamp(), -1)));

        User currentOrSubstitutedUser = userSession.getCurrentOrSubstitutedUser();
        managedProjects = projectsService.getActiveManagedProjectsForUser(currentOrSubstitutedUser, View.LOCAL);
        managedUsers = projectsService.getManagedUsers(currentOrSubstitutedUser, View.LOCAL);

        projectsDl.setParameter("superuser", isSuperUser);
        projectsDl.setParameter("user", userSession.getCurrentOrSubstitutedUser());
        projectsDl.load();
        groupsDl.load();

        initUsersTable();
        initUserReportsTable();
        initDateField();
        initStatusOption();
        initTypeOptions();

        updateWeek();

        project.addValueChangeListener(e -> task.setValue(null));
        status.setOptionsList(Arrays.asList(TimeEntryStatus.values()));
        user.setOptionsList(projectsService.getManagedUsers(userSession.getCurrentOrSubstitutedUser(), View.MINIMAL));
    }

    protected void initUsersTable() {
        final String actionsColumnId = "actions";
        usersTable.addGeneratedColumn(actionsColumnId, entity -> {
            UserChangeStatusActionsProvider provider = new UserChangeStatusActionsProvider(entity);
            return getApproveControls(provider.getApproveAction(), provider.getRejectAction(),
                    provider.getCloseAction());
        });
        usersTable.getColumn(actionsColumnId).setWidth(100);
        usersTable.getColumn(actionsColumnId).setCaption(messages.getMessage(getClass(), actionsColumnId));

        usersDc.addItemChangeListener(e -> {
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
    }

    protected void initProjectColumn() {
        final String projectColumnId = "project";
        weeklyReportsTable.addGeneratedColumn(projectColumnId, entity -> {
            Label label = uiComponents.create(Label.class);
            label.setValue(entity.getProject().getName());
            return label;
        });
    }

    protected void initTaskColumn() {
        final String taskColumnId = "task";
        weeklyReportsTable.addGeneratedColumn(taskColumnId, entity -> {
            Label label = uiComponents.create(Label.class);
            String caption;
            if (entity.getActivityType() == null) {
                caption = entity.getTask().getName();
            } else {
                caption = String.format("%s (%s)",
                        entity.getTask().getName(),
                        metadataTools.getInstanceName(entity));
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
                    final LinkButton linkButton = uiComponents.create(LinkButton.class);
                    linkButton.setCaption(StringFormatHelper.getDayHoursString(reportEntry.getTotalForDay(day)));
                    linkButton.setAction(new BaseAction("edit") {

                        @Override
                        public void actionPerform(Component component) {
                            User user = usersTable.getSingleSelected();
                            if (user != null) {
                                TimeEntryLookup lookup = screenBuilders.lookup(TimeEntry.class, ApproveScreen.this)
                                        .withScreenClass(TimeEntryLookup.class)
                                        .withLaunchMode(OpenMode.DIALOG)
                                        .withSelectHandler(items -> {
                                            if (CollectionUtils.isNotEmpty(items)) {
                                                TimeEntry timeEntry = items.iterator().next();
                                                openTimeEntryEditor(timeEntry);
                                            }
                                        })
                                        .build();
                                lookup.setDate(date);
                                lookup.setActivityType(reportEntry.getActivityType());
                                lookup.setTask(reportEntry.getTask());
                                lookup.setUser(user);
                                lookup.show();
                            }
                        }
                    });
                    return linkButton;
                }

                private Component createLinkToSingleTimeEntry(WeeklyReportEntry reportEntry, List<TimeEntry> timeEntries) {
                    final TimeEntry timeEntry = timeEntries.get(0);
                    final LinkButton linkButton = uiComponents.create(LinkButton.class);
                    linkButton.setCaption(StringFormatHelper.getDayHoursString(reportEntry.getTotalForDay(day)));
                    linkButton.setAction(new BaseAction("edit") {
                        @Override
                        public void actionPerform(Component component) {
                            openTimeEntryEditor(timeEntry);
                        }
                    });
                    return linkButton;
                }
            });
            weeklyReportsTable.getColumn(columnId).setWidth(80);
            weeklyReportsTable.getColumn(columnId).setCaptionAsHtml(true);

            Table.Column column = weeklyReportsTable.getColumn(columnId);
            column.setAggregation(ScreensHelper.createAggregationInfo(
                    projectsService.getEntityMetaPropertyPath(WeeklyReportEntry.class, day.getId()),
                    new WeeklyReportEntryAggregation()
            ));
        }
    }

    protected void initTotalColumn() {
        weeklyReportsTable.addGeneratedColumn(TOTAL_COLUMN_ID, entity -> {
            Label label = uiComponents.create(Label.class);
            label.setValue(entity.getTotal());
            return label;
        });
        weeklyReportsTable.getColumn(TOTAL_COLUMN_ID).setWidth(80);
        weeklyReportsTable.getColumn(TOTAL_COLUMN_ID).setCaption(messages.getMessage(getClass(), "total"));

        Table.Column column = weeklyReportsTable.getColumn(TOTAL_COLUMN_ID);
        column.setAggregation(ScreensHelper.createAggregationInfo(
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
        weeklyReportsTable.getColumn(actionsColumnId).setWidth(100);
        weeklyReportsTable.getColumn(actionsColumnId).setCaption(messages.getMessage(getClass(), actionsColumnId));
    }

    protected void openTimeEntryEditor(
            TimeEntry timeEntry) {
        screenBuilders.editor(TimeEntry.class, this)
                .withScreenClass(TimeEntryEdit.class)
                .withLaunchMode(OpenMode.DIALOG)
                .editEntity(timeEntry)
                .withAfterCloseListener(ev -> {
                    if (Window.COMMIT_ACTION_ID.equals(((StandardCloseAction) ev.getCloseAction()).getActionId())) {
                        updateReportTableItems();
                    }
                })
                .build()
                .show();
    }

    protected Component getApproveControls(@Nullable Action approveAction,
                                           @Nullable Action rejectAction,
                                           @Nullable Action closeAction) {
        HBoxLayout hBoxLayout = uiComponents.create(HBoxLayout.class);
        hBoxLayout.setSpacing(true);
        hBoxLayout.setWidth("100%");

        if (approveAction != null) {
            hBoxLayout.add(ScreensHelper.createCaptionlessLinkButton("icons/ok.png",
                    messages.getMessage(getClass(), AbstractChangeStatusAction.APPROVE_ACTION_ID),
                    approveAction));
        }
        if (rejectAction != null) {
            hBoxLayout.add(ScreensHelper.createCaptionlessLinkButton("icons/remove.png",
                    messages.getMessage(getClass(), AbstractChangeStatusAction.REJECT_ACTION_ID),
                    rejectAction));
        }
        if (closeAction != null) {
            hBoxLayout.add(ScreensHelper.createCaptionlessLinkButton("font-icon:LOCK",
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
        usersDl.setParameter("sessionUser", userSession.getCurrentOrSubstitutedUser());
        if (user.getValue() != null) {
            usersDl.setParameter("user", user.getValue());
        } else {
            usersDl.removeParameter("user");
        }
        if (task.getValue() != null) {
            usersDl.setParameter("task", task.getValue());
        } else {
            usersDl.removeParameter("task");
        }
        if (status.getValue() != null) {
            usersDl.setParameter("status", status.getValue());
        } else {
            usersDl.removeParameter("status");
        }
        if (group.getValue() != null) {
            usersDl.setParameter("group", group.getValue());
        } else {
            usersDl.removeParameter("group");
        }
        if (project.getValue() != null) {
            usersDl.setParameter("project", project.getValue());
        } else {
            usersDl.removeParameter("project");
        }
        usersDl.setParameter("from", firstDayOfWeek);
        usersDl.setParameter("to", lastDayOfWeek);
        usersDl.setParameter("hasTimeEntries", Boolean.TRUE.equals(hasTimeEntries.getValue()));
        usersDl.load();
    }

    protected void updateReportTableItems() {
        weeklyEntriesDc.getMutableItems().clear();
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
            weeklyReportsTable.getColumn(columnId).setCaption(ScreensHelper.getColumnCaption(day.getId(), current));
        }
    }

    protected void updateStatusOption(User user) {
        List<TimeEntryStatus> values;
        if (statusOption.getValue() != null)
            values = new ArrayList<>((Collection) statusOption.getValue());
        else
            values = new ArrayList<>();
        if (securityAssistant.isSuperUser() || managedUsers.contains(user)) {
            if (!values.contains(TimeEntryStatus.NEW)) {
                values.add(TimeEntryStatus.NEW);
            }
        }

        if (isUserCanClose()) {
            if (!values.contains(TimeEntryStatus.APPROVED)) {
                values.add(TimeEntryStatus.APPROVED);
            }

            if (!values.contains(TimeEntryStatus.REJECTED)) {
                values.add(TimeEntryStatus.REJECTED);
            }
        }
        statusOption.setValue(values);
    }

    protected boolean showApprovable() {
        return StringUtils.equals(messages.getMessage(getClass(), "approvable"), typeOption.getValue().toString());
    }

    protected void fillExistingTimeEntries(User user) {
        weeklyEntriesDc.getMutableItems().clear();
        List<TimeEntry> timeEntries = new TimeEntriesProvider(user).setApprovable(showApprovable()).getUserTimeEntries();
        List<WeeklyReportEntry> reportEntries = weeklyReportConverter.convertFromTimeEntries(timeEntries);
        for (WeeklyReportEntry entry : reportEntries) {
            weeklyEntriesDc.getMutableItems().add(entry);
        }
    }

    protected boolean isUserCanClose() {
        return securityAssistant.isSuperUser() || securityAssistant.isUserCloser();
    }

    protected abstract class AbstractChangeStatusAction extends BaseAction {

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
                screenBuilders.screen(ApproveScreen.this)
                        .withScreenClass(RejectionReason.class)
                        .withLaunchMode(OpenMode.DIALOG)
                        .withAfterCloseListener(e -> {
                            if (RejectionReason.CONFIRM_ACTION_AD.equals(((StandardCloseAction) e.getCloseAction()).getActionId())) {
                                rejectReason = e.getScreen().getRejectionReason();
                                commitTimeEntries(timeEntries);
                            }
                        })
                        .build()
                        .show();
            } else {
                commitTimeEntries(timeEntries);
            }
        }

        protected void commitTimeEntries(List<TimeEntry> timeEntries) {
            CommitContext commitContext = new CommitContext();
            for (TimeEntry entry : timeEntries) {
                entry.setStatus(status);
                entry.setRejectionReason(rejectReason);
                commitContext.addInstanceToCommit(entry, "timeEntry-full");
            }
            dataManager.commit(commitContext);

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

            List<TimeEntryStatus> statuses = new ArrayList<>((Collection) statusOption.getValue());
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