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

import com.haulmont.cuba.gui.components.TreeTable;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.gui.util.ScreensHelper;
import com.haulmont.timesheets.service.ProjectsService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author gorelov
 */
@UiController("ts$Project.lookup")
@UiDescriptor("project-lookup.xml")
@LookupComponent("projectsTable")
@LoadDataBeforeShow
public class ProjectLookup extends StandardLookup<Project> {

    @Inject
    protected ProjectsService projectsService;

    @Inject
    protected TreeTable<Project> projectsTable;
    @Inject
    protected CollectionContainer<Project> projectsDc;

    protected Project excludedProject;
    protected Project parentProject;

    public void setExcludedProject(Project excludedProject) {
        this.excludedProject = excludedProject;
    }

    public void setParentProject(Project parentProject) {
        this.parentProject = parentProject;
    }

    @Install(to = "projectsTable", subject = "styleProvider")
    protected String projectsTableStyleProvider(Project entity, String property) {
        if ("status".equals(property)) {
            return ScreensHelper.getProjectStatusStyle(entity);
        }
        return null;
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        if (parentProject != null) {
            projectsDc.getMutableItems().remove(parentProject);
            List<Project> childrenProjects = projectsService.getProjectChildren(parentProject);
            for (Project child : childrenProjects) {
                projectsDc.getMutableItems().remove(child);
            }
        }
        if (excludedProject != null) {
            projectsDc.getMutableItems().remove(excludedProject);
        }

        projectsTable.expandAll();
    }
}