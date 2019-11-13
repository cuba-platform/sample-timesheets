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

package com.haulmont.timesheets.gui.project;

import com.google.common.collect.ImmutableMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.LookupComponent;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.gui.util.ScreensHelper;
import com.haulmont.timesheets.gui.util.SecurityAssistant;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.*;

/**
 * @author gorelov
 */
@UiController("ts$Project.browse")
@UiDescriptor("project-browse.xml")
@LookupComponent("projectsTable")
public class ProjectBrowse extends StandardLookup<Project> {
    @Inject
    protected TreeTable<Project> projectsTable;
    @Inject
    protected Table<Task> tasksTable;
    @Inject
    protected Table<ProjectParticipant> participantsTable;
    @Inject
    protected PopupButton assignBtn;
    @Inject
    protected SecurityAssistant securityAssistant;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected CollectionLoader<Project> projectsDl;
    @Inject
    protected CollectionLoader<ProjectParticipant> participantsDl;
    @Inject
    protected CollectionLoader<Task> tasksDl;
    @Inject
    private CollectionContainer<Project> projectsDc;
    @Inject
    protected Metadata metadata;
    @Inject
    protected UserSession userSession;
    @Inject
    protected Messages messages;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected Notifications notifications;
    @Inject
    protected DataManager dataManager;

    protected List<Action> editActions = new ArrayList<>();
    protected List<ProjectRole> projectCreateRoles;

    @Install(to = "projectsTable", subject = "styleProvider")
    protected String projectsTableStyleProvider(Project entity, String property) {
        if ("status".equals(property)) {
            return ScreensHelper.getProjectStatusStyle(entity);
        }
        return null;
    }

    @Install(to = "tasksTable", subject = "styleProvider")
    protected String tasksTableStyleProvider(Task entity, String property) {
        if ("status".equals(property)) {
            return ScreensHelper.getTaskStatusStyle(entity);
        }
        return null;
    }

    @Subscribe
    protected void onInit(InitEvent event) {
        if (securityAssistant.isSuperUser()) {
            projectsDl.setQuery("select e from ts$Project e");
        } else {
            projectsDl.setParameter("user", userSession.getCurrentOrSubstitutedUser());
        }
        initTasksTable();
    }

    @Subscribe("tasksTable.create")
    protected void onTasksTableCreate(Action.ActionPerformedEvent event) {
        if (projectsTable.getSingleSelected() != null) {
            Task newTask = metadata.create(Task.class);
            newTask.setProject(projectsTable.getSingleSelected());
            screenBuilders.editor(tasksTable)
                    .newEntity(newTask)
                    .build()
                    .show();
        } else {
            showNotification(messageBundle.getMessage("notification.pleaseSelectProject"), Notifications.NotificationType.HUMANIZED);
        }
    }

    @Subscribe("participantsTable.create")
    protected void onParticipantsTableCreate(Action.ActionPerformedEvent event) {
        ProjectParticipant newParticipant = metadata.create(ProjectParticipant.class);
        newParticipant.setProject(projectsTable.getSingleSelected());
        if (projectsTable.getSingleSelected() != null) {
            screenBuilders.editor(participantsTable)
                    .newEntity(newParticipant)
                    .withLaunchMode(OpenMode.DIALOG)
                    .build()
                    .show();
        } else {
            showNotification(messageBundle.getMessage("notification.pleaseSelectProject"), Notifications.NotificationType.HUMANIZED);
        }
    }

    @Subscribe("participantsTable.edit")
    protected void onParticipantsTableEdit(Action.ActionPerformedEvent event) {
        screenBuilders.editor(participantsTable)
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();
    }

    @Subscribe("participantsTable.copy")
    protected void onParticipantsTableCopy(Action.ActionPerformedEvent event) {
        Project project = projectsTable.getSingleSelected();
        if (project != null) {
            ProjectLookup lookup = screenBuilders.lookup(Project.class, this)
                    .withScreenClass(ProjectLookup.class)
                    .withLaunchMode(OpenMode.DIALOG)
                    .withSelectHandler(items -> {
                        if (CollectionUtils.isNotEmpty(items)) {
                            CommitContext commitContext = new CommitContext();
                            for (Project selected : items) {
                                commitContext.getCommitInstances().addAll(
                                        updateParticipants(projectsService.getProjectParticipants(selected, "projectParticipant-full"), project));
                            }
                            dataManager.commit(commitContext);
                            tasksDl.setParameter("project", project);
                            participantsDl.load();
                        }
                    })
                    .build();
            lookup.setExcludedProject(project);
            lookup.show();
        } else {
            showNotificationProjectIsNotSelected();
        }
    }

    @Subscribe("projectsTable.create")
    protected void onProjectsTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.editor(projectsTable)
                .newEntity()
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();
    }

    @Subscribe("projectsTable.edit")
    protected void onProjectsTableEdit(Action.ActionPerformedEvent event) {
        Screen editor = screenBuilders.editor(projectsTable)
                .withLaunchMode(OpenMode.DIALOG)
                .build();
        editor.addAfterCloseListener(afterCloseEvent -> projectsDl.load());
        editor.show();
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        projectsDc.addItemChangeListener(e -> {
            tasksDl.setParameter("project", e.getItem());
            tasksDl.load();
            participantsDl.setParameter("project", e.getItem());
            participantsDl.load();
        });

        projectsDl.load();

        initProjectsTable();

        projectsTable.expandAll();
    }

    private void initTasksTable() {
        tasksTable.addAction(new ItemTrackingAction("switchStatus") {
            @Override
            public void actionPerform(Component component) {
                Task task = tasksTable.getSingleSelected();
                if (task != null) {
                    if (task.getStatus() != null) {
                        task.setStatus(task.getStatus().inverted());
                    }
                }
            }

            @Override
            public void refreshState() {
                super.refreshState();
                String captionKey = "closeTask";
                Task selected = (Task) target.getSingleSelected();
                if (selected != null) {
                    TaskStatus status = selected.getStatus();
                    if (TaskStatus.INACTIVE.equals(status)) {
                        captionKey = "openTask";
                    }
                }
                setCaption(messages.getMessage(getClass(), captionKey));
            }
        }.withIcon("font-icon:EXCHANGE"));
    }

    private void initProjectsTable() {
        LoadContext<ProjectRole> loadContext = new LoadContext<>(ProjectRole.class);
        loadContext.setQueryString("select pr from ts$ProjectRole pr order by pr.name");
        List<ProjectRole> loadedRoles = dataManager.loadList(loadContext);
        projectCreateRoles = new ArrayList<>(loadedRoles);

        sortProjectRoles(projectCreateRoles);
        for (final ProjectRole projectRole : projectCreateRoles) {
            assignBtn.addAction(createAssignAction(projectRole));
        }
        assignBtn.addAction(createAssignAction(null));
    }

    protected Action createAssignAction(final ProjectRole projectRole) {
        final String assignCode = "assign" + StringUtils.capitalize(projectRole.getCode().getId().toLowerCase());

        final String assignCaption = messageBundle.getMessage("caption.assign" + StringUtils.capitalize(projectRole.getCode().getId().toLowerCase()));

        return new BaseAction(assignCode)
                .withCaption(assignCaption)
                .withHandler(actionPerformedEvent -> {
                    Set<Project> selected = projectsTable.getSelected();
                    if (CollectionUtils.isNotEmpty(selected)) {
                        doAssign(projectRole, selected);
                    } else {
                        showNotification(messageBundle.getMessage("notification.pleaseSelectProject"), Notifications.NotificationType.HUMANIZED);
                    }
                });
    }

    protected void doAssign(final ProjectRole projectRole, final Collection<Project> selectedProjects) {
        getWindow().openLookup("sec$User.lookup", items -> {
            if (CollectionUtils.isNotEmpty(items)) {
                boolean needToRefresh = projectsService.assignUsersToProjects(items, selectedProjects, projectRole);
                if (projectRole == null || needToRefresh) {
                    participantsDl.setParameter("project", selectedProjects.iterator().next());
                    participantsDl.load();
                }
            }
        }, WindowManager.OpenType.DIALOG);
    }

    protected void sortProjectRoles(List<ProjectRole> projectRoles) {
        Collections.sort(projectRoles, new Comparator<ProjectRole>() {
            private Map<ProjectRoleCode, Integer> order = ImmutableMap.<ProjectRoleCode, Integer>builder()
                    .put(ProjectRoleCode.WORKER, 1)
                    .put(ProjectRoleCode.MANAGER, 2)
                    .put(ProjectRoleCode.APPROVER, 3)
                    .put(ProjectRoleCode.OBSERVER, 4).build();

            @Override
            public int compare(ProjectRole o1, ProjectRole o2) {
                ProjectRoleCode code1 = o1.getCode();
                ProjectRoleCode code2 = o2.getCode();

                Integer order1 = order.get(code1);
                Integer order2 = order.get(code2);

                return ObjectUtils.compare(order1, order2);
            }
        });
    }


    protected Collection<? extends Entity> updateParticipants(List<ProjectParticipant> participants, Project project) {
        if (participants.isEmpty()) {
            return Collections.emptyList();
        }
        List<ProjectParticipant> copies = new ArrayList<>(participants.size());
        List<User> assignedUsers = projectsService.getProjectUsers(project, View.MINIMAL);
        for (ProjectParticipant existParticipant : participants) {
            if (!assignedUsers.contains(existParticipant.getUser())) {
                ProjectParticipant participant = metadata.create(ProjectParticipant.class);
                participant.setUser(existParticipant.getUser());
                participant.setRole(existParticipant.getRole());
                participant.setProject(project);

                copies.add(participant);
            }
        }
        return copies;
    }

    protected void showNotificationProjectIsNotSelected() {
        showNotification(messageBundle.getMessage("notification.pleaseSelectProject"), Notifications.NotificationType.HUMANIZED);
    }

    protected void showNotification(String caption, Notifications.NotificationType type) {
        notifications.create()
                .withCaption(caption)
                .withType(type)
                .show();
    }
}