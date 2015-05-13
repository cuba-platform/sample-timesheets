/*
 * Copyright (c) 2015 com.haulmont.ts.service
 */
package com.haulmont.timesheets.service;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.Client;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.ProjectParticipant;
import com.haulmont.timesheets.entity.ProjectRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author gorelov
 */
@Service(ProjectsService.NAME)
public class ProjectsServiceBean implements ProjectsService {

    @Inject
    protected DataManager dataManager;

    protected List<Project> getAllProjects() {
        LoadContext loadContext = new LoadContext(Project.class)
                .setView("project-full");
        loadContext.setQueryString("select e from ts$Project e");

        return dataManager.loadList(loadContext);
    }

    @Nonnull
    @Override
    @Transactional
    public List<Project> getChildren(Project parent) {
        List<Project> projects = getAllProjects();
        if (!projects.isEmpty()) {
            List<Project> children = new ArrayList<>();
            for (Project project : projects) {
                if (parent.equals(project.getParent())) {
                    children.add(project);
                    children.addAll(getChildren(project));
                }
            }
            return children;
        }
        return Collections.emptyList();
    }

    @Override
    public void setClient(@Nonnull Project project, @Nullable Client client) {
        project.setClient(client);
        dataManager.commit(project);
    }

    @Nullable
    @Override
    public ProjectRole getUserProjectRole(@Nonnull  Project project, @Nonnull User user) {
        LoadContext loadContext = new LoadContext(ProjectParticipant.class)
                .setView("projectParticipant-full");
        loadContext.setQueryString("select e from ts$ProjectParticipant e where e.user.id = :userId and e.project.id = :projectId")
                .setParameter("userId", user.getId())
                .setParameter("projectId", project.getId());
        ProjectParticipant participant = dataManager.load(loadContext);
        return participant != null ? participant.getRole() : null;
    }

    @Nullable
    @Override
    public ProjectRole getRoleByCode(String code) {
        LoadContext loadContext = new LoadContext(ProjectRole.class);
        loadContext.setQueryString("select e from ts$ProjectRole e where e.code = :code")
                .setParameter("code", code);
        return dataManager.load(loadContext);
    }
}