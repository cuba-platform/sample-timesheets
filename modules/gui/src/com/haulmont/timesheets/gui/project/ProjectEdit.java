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

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.timesheets.entity.Client;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.ProjectStatus;
import com.haulmont.timesheets.gui.util.ComponentsHelper;
import com.haulmont.timesheets.service.ProjectsService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author gorelov
 */
public class ProjectEdit extends AbstractEditor<Project> {
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected CollectionDatasource<Project, UUID> projectsDs;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected Datasource<Project> projectDs;

    @Named("fieldGroup.parent")
    protected LookupPickerField parentField;
    @Named("fieldGroup.client")
    protected LookupPickerField clientField;

    @Override
    public void init(final Map<String, Object> params) {
        clientField.addAction(ComponentsHelper.createLookupAction(clientField));
        clientField.addClearAction();

        projectDs.addItemPropertyChangeListener(e -> {
            if ("parent".equals(e.getProperty())) {
                clientField.setEnabled(e.getValue() == null);
                if (e.getValue() != null) {
                    Project parent = (Project) e.getValue();
                    if (!parent.getClient().equals(getItem().getClient())) {
                        clientField.setValue(parent.getClient());
                    }
                } else if (e.getPrevValue() != null) {
                    clientField.setValue(null);
                }
            }
        });

        projectDs.addItemPropertyChangeListener(new ComponentsHelper.EntityCodeGenerationListener<>());
        fieldGroup.addCustomField("description", ComponentsHelper.getCustomTextArea());
    }

    @Override
    protected void initNewItem(Project item) {
        super.initNewItem(item);
        if (item.getStatus() == null) {
            item.setStatus(ProjectStatus.OPEN);
        }
    }

    @Override
    protected void postInit() {
        Project project = getItem();
        projectsDs.excludeItem(project);

        List<Project> childrenProjects = projectsService.getProjectChildren(project);
        for (Project child : childrenProjects) {
            projectsDs.excludeItem(child);
        }

        clientField.setEnabled(project.getParent() == null);

        PickerField.LookupAction lookupAction = ComponentsHelper.createLookupAction(parentField);
        lookupAction.setLookupScreenParams(ParamsMap.of("parentProject", getItem()));
        parentField.addAction(lookupAction);
        parentField.addClearAction();
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed) {
            Client client = getItem().getClient();
            List<Project> childrenProjects = projectsService.getProjectChildren(getItem());
            CommitContext commitContext = new CommitContext();
            for (Project child : childrenProjects) {
                child.setClient(client);
                commitContext.getCommitInstances().add(child);
            }
            getDsContext().getDataSupplier().commit(commitContext);
        }
        return super.postCommit(committed, close);
    }
}