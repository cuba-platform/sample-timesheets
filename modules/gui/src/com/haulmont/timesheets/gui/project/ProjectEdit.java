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

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.Client;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.ProjectStatus;
import com.haulmont.timesheets.gui.util.ScreensHelper;
import com.haulmont.timesheets.service.ProjectsService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author gorelov
 */
@UiController("ts$Project.edit")
@UiDescriptor("project-edit.xml")
@EditedEntityContainer("projectDc")
@LoadDataBeforeShow
public class ProjectEdit extends StandardEditor<Project> {

    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected UserSession userSession;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected LookupPickerField<Client> client;
    @Inject
    protected LookupPickerField<Project> parent;
    @Inject
    protected InstanceContainer<Project> projectDc;
    @Inject
    protected CollectionContainer<Project> projectsDc;

    @Subscribe(id = "projectDc", target = Target.DATA_CONTAINER)
    protected void onProjectDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<Project> e) {
        if ("parent".equals(e.getProperty())) {
            client.setEnabled(e.getValue() == null);
            if (e.getValue() != null) {
                Project parent = (Project) e.getValue();

                if (!parent.getClient().equals(e.getItem().getClient())) {
                    client.setValue(parent.getClient());
                }
            } else if (e.getPrevValue() != null) {
                client.setValue(null);
            }
        }
    }

    @Subscribe("client.lookup")
    protected void onClientLookup(Action.ActionPerformedEvent e) {
        screenBuilders.lookup(client)
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();
    }

    @Subscribe("parent.lookup")
    protected void onParentLookup(Action.ActionPerformedEvent event) {
        ProjectLookup lookup = screenBuilders.lookup(parent)
                .withScreenClass(ProjectLookup.class)
                .withLaunchMode(OpenMode.DIALOG)
                .build();
        lookup.setParentProject(getEditedEntity());
        lookup.show();
    }

    @Subscribe
    protected void onInit(InitEvent e) {
        projectDc.addItemPropertyChangeListener(new ScreensHelper.EntityCodeGenerationListener<>());
    }

    @Subscribe
    protected void onInitEntity(InitEntityEvent<Project> e) {
        if (e.getEntity().getStatus() == null) {
            e.getEntity().setStatus(ProjectStatus.OPEN);
        }
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent e) {
        Project project = getEditedEntity();

        projectsDc.getMutableItems().remove(project);
        projectsDc.getMutableItems().removeAll(projectsService.getProjectChildren(project));

        client.setEnabled(project.getParent() == null);
    }

    @Subscribe(target = Target.DATA_CONTEXT)
    protected void onPostCommit(DataContext.PostCommitEvent e) {
        Client client = getEditedEntity().getClient();
        List<Project> childrenProjects = projectsService.getProjectChildren(getEditedEntity());
        CommitContext commitContext = new CommitContext();
        for (Project child : childrenProjects) {
            child.setClient(client);
            commitContext.addInstanceToCommit(child);
        }
        dataManager.commit(commitContext);
    }
}