/*
 * Copyright (c) 2015 com.haulmont.ts.service
 */
package com.haulmont.timesheets.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    protected Persistence persistence;
    @Inject
    protected DataManager dataManager;

    protected List<Project> getAllProjects() {
        EntityManager entityManager = persistence.getEntityManager();
        TypedQuery<Project> query = entityManager.createQuery("select e from ts$Project e", Project.class);
        return query.getResultList();
    }

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
    public void setClient(Project project, Client client) {
        Transaction tx = persistence.createTransaction();
        try {
            List<Project> projects = getAllProjects();
            for (Project entity : projects) {
                if (entity.equals(project)) {
                    entity.setClient(client);
                }
            }
            tx.commit();
        } finally {
            tx.end();
        }
    }

    @Override
    public ProjectRole getUserProjectRole(Project project, User user) {
        if (project == null || user == null) {
            return null;
        }
        LoadContext loadContext = new LoadContext(ProjectParticipant.class)
                .setView("projectParticipant-full");
        loadContext.setQueryString("select e from ts$ProjectParticipant e where e.user.id = :userId and e.project.id = :projectId")
                .setParameter("userId", user.getId())
                .setParameter("projectId", project.getId());
        ProjectParticipant participant = dataManager.load(loadContext);
        return participant != null ? participant.getRole() : null;
    }

    @Override
    public ProjectRole getRoleByCode(String code) {
        LoadContext loadContext = new LoadContext(ProjectRole.class);
        loadContext.setQueryString("select e from ts$ProjectRole e where e.code = :code")
                .setParameter("code", code);
        return dataManager.load(loadContext);
    }

    @Override
    public void updateTask(Task task) {
        CommitContext commitContext = new CommitContext();
        commitContext.getCommitInstances().add(task);

        dataManager.commit(commitContext);
    }
}