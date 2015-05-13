/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui.project
 */
package com.haulmont.timesheets.gui.project;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.TreeTable;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.haulmont.timesheets.service.ProjectsService;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author gorelov
 */
public class ProjectLookup extends AbstractLookup {

    @Inject
    protected HierarchicalDatasource<Project, UUID> projectsDs;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected TreeTable projectsTable;

    @Override
    public void init(Map<String, Object> params) {
        Project project = (Project) params.get("parentProject");
        if (project != null) {
            projectsDs.excludeItem(project);
            List<Project> childrenProjects = projectsService.getChildren(project);
            for (Project child : childrenProjects) {
                projectsDs.excludeItem(child);
            }
        }

        projectsTable.setStyleProvider(new Table.StyleProvider() {
            @Nullable
            @Override
            public String getStyleName(Entity entity, String property) {
                if ("status".equals(property)) {
                    Project project = (Project) entity;
                    return ComponentsHelper.getProjectStatusStyle(project);
                }
                return null;
            }
        });
    }

    @Override
    public void ready() {
        projectsTable.expandAll();
    }
}