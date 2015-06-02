/*
 * Copyright (c) 2015 com.haulmont.ts.service
 */
package com.haulmont.timesheets.service;

import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.SystemDataManager;
import com.haulmont.timesheets.entity.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

import static com.haulmont.timesheets.entity.ProjectRoleCode.APPROVER;
import static com.haulmont.timesheets.entity.ProjectRoleCode.MANAGER;

/**
 * @author gorelov
 */
@Service(ProjectsService.NAME)
public class ProjectsServiceBean implements ProjectsService {
    @Inject
    protected DataManager dataManager;

    @Inject
    protected SystemDataManager systemDataManager;

    @Inject
    protected Persistence persistence;

    protected List<Project> getAllProjects() {
        LoadContext loadContext = new LoadContext(Project.class)
                .setView("project-full");
        loadContext.setQueryString("select e from ts$Project e");

        return dataManager.loadList(loadContext);
    }


    @Override
    @Transactional
    public List<Project> getProjectChildren(Project parent) {
        List<Project> projects = getAllProjects();
        if (!projects.isEmpty()) {
            List<Project> children = new ArrayList<>();
            for (Project project : projects) {
                if (parent.equals(project.getParent())) {
                    children.add(project);
                    children.addAll(getProjectChildren(project));
                }
            }
            return children;
        }
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public ProjectRole getUserProjectRole(Project project, User user) {
        LoadContext loadContext = new LoadContext(ProjectParticipant.class)
                .setView("projectParticipant-full");
        loadContext.setQueryString("select e from ts$ProjectParticipant e " +
                "where e.user.id = :userId and e.project.id = :projectId")
                .setParameter("userId", user.getId())
                .setParameter("projectId", project.getId());
        ProjectParticipant participant = dataManager.load(loadContext);
        return participant != null ? participant.getRole() : null;
    }

    @Nullable
    @Override
    public <T extends Entity> T getEntityByCode(Class<T> clazz, String code, String viewName) {
        return systemDataManager.getEntityByCode(clazz, code, viewName);
    }

    @Override
    public <T extends Entity> MetaPropertyPath getEntityMetaPropertyPath(Class<T> clazz, String property) {
        return systemDataManager.getEntityMetaPropertyPath(clazz, property);
    }

    @Override
    public List<TimeEntry> getTimeEntriesForPeriod(Date start, Date end, User user, @Nullable TimeEntryStatus status, @Nullable String viewName) {
        LoadContext loadContext = new LoadContext(TimeEntry.class);
        if (viewName != null) {
            loadContext.setView(viewName);
        }
        String queryStr = "select e from ts$TimeEntry e where e.user.id = :userId and (e.date between :start and :end)";
        if (status != null) {
            queryStr += " and e.status = :status";
        }
        LoadContext.Query query = loadContext.setQueryString(queryStr)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("userId", user.getId());
        if (status != null) {
            query.setParameter("status", status.getId());
        }
        return dataManager.loadList(loadContext);
    }

    @Override
    public List<TimeEntry> getApprovableTimeEntriesForPeriod(
            Date start, Date end, User approver, User user, @Nullable TimeEntryStatus status, @Nullable String viewName
    ) {
        LoadContext loadContext = new LoadContext(TimeEntry.class);
        if (viewName != null) {
            loadContext.setView(viewName);
        }
        String queryStr = "select e from ts$TimeEntry e join e.task t join t.project pr join pr.participants p " +
                "where p.user.id = :approverId and (p.role.code = '" + MANAGER.getId() + "' or p.role.code = '" + APPROVER.getId() + "') " +
                "and e.user.id = :userId and (e.date between :start and :end)";
        if (status != null) {
            queryStr += " and e.status = :status";
        }
        LoadContext.Query query = loadContext.setQueryString(queryStr)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("approverId", approver.getId())
                .setParameter("userId", user.getId());
        if (status != null) {
            query.setParameter("status", status.getId());
        }
        return dataManager.loadList(loadContext);
    }

    @Override
    public List<TimeEntry> getTimeEntriesForUser(User user, @Nullable String viewName) {
        LoadContext loadContext = new LoadContext(TimeEntry.class);
        if (viewName != null) {
            loadContext.setView(viewName);
        }
        loadContext.setQueryString("select e from ts$TimeEntry e where e.user.id = :userId")
                .setParameter("userId", user.getId());
        return dataManager.loadList(loadContext);
    }

    @Override
    public List<Holiday> getHolidays() {
        LoadContext loadContext = new LoadContext(Holiday.class);
        loadContext.setQueryString("select e from ts$Holiday e");
        return dataManager.loadList(loadContext);
    }

    @Override
    public List<Holiday> getHolidaysForPeriod(Date start, Date end) {
        LoadContext loadContext = new LoadContext(Holiday.class);
        loadContext.setQueryString("select e from ts$Holiday e " +
                "where (e.startDate between :start and :end)" +
                " or (e.endDate between :start and :end)" +
                " or (:start between e.startDate and e.endDate)" +
                " or (:end between e.startDate and e.endDate)")
                .setParameter("start", start)
                .setParameter("end", end);
        return dataManager.loadList(loadContext);
    }

    @Override
    public List<Task> getActiveTasksForUser(User user, @Nullable String viewName) {
        LoadContext loadContext = new LoadContext(Task.class);
        if (viewName != null) {
            loadContext.setView(viewName);
        }
        loadContext.setQueryString("select e from ts$Task e join e.exclusiveParticipants p " +
                "where p.user.id = :userId and e.status = 'active' order by e.project")
                .setParameter("userId", user.getId());
        List<Task> assignedTasks = dataManager.loadList(loadContext);
        loadContext.setQueryString("select e from ts$Task e join e.project pr join pr.participants p " +
                "where p.user.id = :userId and e.exclusiveParticipants is null and e.status = 'active' order by e.project")
                .setParameter("userId", user.getId());
        List<Task> commonTasks = dataManager.loadList(loadContext);
        if (assignedTasks.isEmpty() && commonTasks.isEmpty()) {
            return Collections.emptyList();
        }
        List<Task> allTasks = new ArrayList<>(assignedTasks.size() + commonTasks.size());
        allTasks.addAll(assignedTasks);
        allTasks.addAll(commonTasks);
        return allTasks;
    }

    @Override
    public Map<String, Task> getActiveTasksForUserAndProject(User user, Project project, @Nullable String viewName) {
        LoadContext loadContext = new LoadContext(Task.class);
        if (viewName != null) {
            loadContext.setView(viewName);
        }
        loadContext.setQueryString("select e from ts$Task e join e.exclusiveParticipants p " +
                "where p.user.id = :userId and e.project.id = :projectId and e.status = 'active' order by e.project")
                .setParameter("projectId", project.getId())
                .setParameter("userId", user.getId());
        List<Task> assignedTasks = dataManager.loadList(loadContext);
        loadContext.setQueryString("select e from ts$Task e join e.project pr join pr.participants p " +
                "where p.user.id = :userId and e.project.id = :projectId and e.exclusiveParticipants is null " +
                "and e.status = 'active' order by e.project")
                .setParameter("projectId", project.getId())
                .setParameter("userId", user.getId());
        List<Task> commonTasks = dataManager.loadList(loadContext);
        if (assignedTasks.isEmpty() && commonTasks.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Task> allTasks = new ArrayList<>(assignedTasks.size() + commonTasks.size());
        allTasks.addAll(assignedTasks);
        allTasks.addAll(commonTasks);
        Map<String, Task> tasksMap = new HashMap<>(allTasks.size());
        for (Task task : allTasks) {
            tasksMap.put(task.getName(), task);
        }
        return tasksMap;
    }

    public List<Project> getActiveProjectsForUser(User user, @Nullable String viewName) {
        LoadContext loadContext = new LoadContext(Project.class);
        if (viewName != null) {
            loadContext.setView(viewName);
        }
        LoadContext.Query query =
                new LoadContext.Query("select pr from ts$Project pr, in(pr.participants) p " +
                        "where p.user.id = :userId and pr.status = 'open'")
                        .setParameter("userId", user.getId());
        loadContext.setQuery(query);
        return dataManager.loadList(loadContext);
    }

    public List<Project> getActiveManagedProjectsForUser(User user, @Nullable String viewName) {
        LoadContext loadContext = new LoadContext(Project.class);
        if (viewName != null) {
            loadContext.setView(viewName);
        }
        LoadContext.Query query =
                new LoadContext.Query("select pr from ts$Project pr, in(pr.participants) p " +
                        "where p.user.id = :userId " +
                        "and (p.role.code = '" + MANAGER.getId() + "' or p.role.code = '" + APPROVER.getId() + "') " +
                        "and pr.status = 'open'")
                        .setParameter("userId", user.getId());
        loadContext.setQuery(query);
        return dataManager.loadList(loadContext);
    }

    @Override
    public boolean assignUsersToProjects(Collection<User> users, Collection<Project> projects, ProjectRole projectRole) {
        List<ProjectParticipant> result = new ArrayList<>();
        Transaction tx = persistence.createTransaction();
        try {
            List<UUID> ids = new ArrayList<>();
            for (Project project : projects) {
                ids.add(project.getId());
            }

            projects = persistence.getEntityManager().createQuery("select pr from ts$Project pr where pr.id in (:ids)", Project.class)
                    .setViewName("project-full")
                    .setParameter("ids", ids)
                    .getResultList();

            for (Project project : projects) {
                Set<User> assignedUsers = new HashSet<>();
                for (ProjectParticipant projectParticipant : project.getParticipants()) {
                    assignedUsers.add(projectParticipant.getUser());
                }

                for (User user : users) {
                    if (!assignedUsers.contains(user)) {
                        ProjectParticipant projectParticipant = new ProjectParticipant();
                        projectParticipant.setRole(projectRole);
                        projectParticipant.setUser(user);
                        projectParticipant.setProject(project);
                        result.add(projectParticipant);
                    }
                }
            }

            for (ProjectParticipant projectParticipant : result) {
                persistence.getEntityManager().persist(projectParticipant);
            }

            tx.commit();
        } finally {
            tx.end();
        }

        return CollectionUtils.isNotEmpty(result);
    }

    public List<Tag> getTagsForTheProject(@Nullable Project project, @Nullable String viewName) {
        LoadContext loadContext = new LoadContext(Tag.class);
        if (viewName != null) {
            loadContext.setView(viewName);
        }
        LoadContext.Query query =
                new LoadContext.Query("select e from ts$Tag e left join e.tagType.projects pr where pr.id is null" +
                        " or (pr.id = :project)")
                        .setParameter("project", project);
        loadContext.setQuery(query);
        return dataManager.loadList(loadContext);
    }

    @Override
    public List<ProjectParticipant> getProjectParticipants(Project project, @Nullable String viewName) {
        LoadContext loadContext = new LoadContext(ProjectParticipant.class);
        if (viewName != null) {
            loadContext.setView(viewName);
        }
        loadContext.setQueryString("select e from ts$ProjectParticipant e where e.project.id = :projectId")
                .setParameter("projectId", project.getId());
        return dataManager.loadList(loadContext);
    }

    @Override
    public List<User> getProjectUsers(Project project, @Nullable String viewName) {
        LoadContext loadContext = new LoadContext(User.class);
        if (viewName != null) {
            loadContext.setView(viewName);
        }
        loadContext.setQueryString("select u from sec$User u, ts$ProjectParticipant pp " +
                "where pp.project.id = :projectId and pp.user.id = u.id")
                .setParameter("projectId", project.getId());
        return dataManager.loadList(loadContext);
    }

    @Override
    public List<User> getManagedUsersForUser(User manager, String viewName) {
        LoadContext loadContext = new LoadContext(User.class);
        if (viewName != null) {
            loadContext.setView(viewName);
        }
        loadContext.setQueryString("select u from sec$User u, ts$ProjectParticipant pp " +
                "join pp.project pr join pr.participants me " +
                "where pp.user.id = u.id and me.user.id = :managerId " +
                "and (me.role.code = '" + MANAGER.getId() + "' or me.role.code = '" + APPROVER.getId() + "')")
                .setParameter("managerId", manager.getId());
        return dataManager.loadList(loadContext);
    }
}