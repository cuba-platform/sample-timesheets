/*
 * Copyright (c) 2015 com.haulmont.ts.gui.project
 */
package com.haulmont.timesheets.gui.project;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.timesheets.entity.Client;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.ProjectStatus;
import com.haulmont.timesheets.gui.ComponentsHelper;
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
        getDialogParams().setWidthAuto();

        clientField.addAction(ComponentsHelper.createLookupAction(clientField));
        clientField.addClearAction();

        projectDs.addListener(new DsListenerAdapter<Project>() {
            @Override
            public void valueChanged(Project source, String property, Object prevValue, Object value) {
                if ("parent".equals(property)) {
                    clientField.setEnabled(value == null);
                    if (value != null) {
                        Project parent = (Project) value;
                        if (!parent.getClient().equals(getItem().getClient())) {
                            clientField.setValue(parent.getClient());
                        }
                    } else if (prevValue != null) {
                        clientField.setValue(null);
                    }
                }
            }
        });

        projectDs.addListener(new ComponentsHelper.EntityCodeGenerationListener<Project>());
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

        List<Project> childrenProjects = projectsService.getChildren(project);
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
            List<Project> childrenProjects = projectsService.getChildren(getItem());
            for (Project child : childrenProjects) {
                projectsService.setClient(child, client);
            }
        }
        return super.postCommit(committed, close);
    }
}