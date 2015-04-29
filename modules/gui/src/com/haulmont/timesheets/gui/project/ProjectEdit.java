/*
 * Copyright (c) 2015 com.haulmont.ts.gui.project
 */
package com.haulmont.timesheets.gui.project;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.DialogParams;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.timesheets.entity.Client;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.ProjectStatus;
import com.haulmont.timesheets.service.ProjectsService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * @author gorelov
 */
public class ProjectEdit extends AbstractEditor<Project> {

    @Inject
    protected Messages messages;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected CollectionDatasource<Project, UUID> projectsDs;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected Table participantsTable;
    @Inject
    private Datasource<Project> projectDs;

    @Named("fieldGroup.parent")
    protected LookupPickerField parentField;
    @Named("fieldGroup.client")
    protected LookupPickerField clientField;
    @Named("participantsTable.create")
    protected CreateAction participantsTableCreate;

    @Override
    public void init(final Map<String, Object> params) {

        parentField.addAction(createLookupAction(parentField));
        parentField.addAction(new PickerField.ClearAction(parentField));

        clientField.addAction(createLookupAction(clientField));
        clientField.addAction(new PickerField.ClearAction(clientField));

        participantsTableCreate.setOpenType(WindowManager.OpenType.DIALOG);

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

        fieldGroup.addCustomField("description", new FieldGroup.CustomFieldGenerator() {
            @Override
            public Component generateField(Datasource datasource, String propertyId) {
                ResizableTextArea textArea = componentsFactory.createComponent(ResizableTextArea.NAME);
                textArea.setDatasource(datasource, propertyId);
                textArea.setHeight("100px");
                textArea.setResizable(true);
                return textArea;
            }
        });

        String removeColumnName = "remove";
        participantsTable.addGeneratedColumn(removeColumnName, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                LinkButton removeButton = componentsFactory.createComponent(LinkButton.NAME);
                removeButton.setIcon("icons/remove.png");
                removeButton.setAction(new ParticipantRemoveAction(participantsTable, entity));
                return removeButton;
            }
        });
        participantsTable.setColumnCaption(removeColumnName, "");
        participantsTable.setColumnWidth(removeColumnName, 35);
    }

    @Override
    protected void postInit() {
        Project project = getItem();
        projectsDs.excludeItem(project);

        List<Project> childrenProjects = projectsService.getChildren(project);
        for (Project child : childrenProjects) {
            projectsDs.excludeItem(child);
        }

        if (project.getParent() != null) {
            clientField.setEnabled(false);
        }

        project.setStatus(ProjectStatus.OPEN);
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

    protected PickerField.LookupAction createLookupAction(PickerField pickerField) {
        PickerField.LookupAction lookupAction = new PickerField.LookupAction(pickerField);
        lookupAction.setLookupScreenOpenType(WindowManager.OpenType.DIALOG);
        lookupAction.setLookupScreenDialogParams(new DialogParams()
                .setWidth(800)
                .setHeight(500)
                .setResizable(true));
        return lookupAction;
    }

    protected class ParticipantRemoveAction extends RemoveAction {

        private Entity entity;

        public ParticipantRemoveAction(ListComponent target, Entity entity) {
            super(target);
            this.entity = entity;
        }

        @Override
        public void actionPerform(Component component) {
            if (!isEnabled()) {
                return;
            }

            Set<Entity> selected = new HashSet<>(1);
            selected.add(entity);
            confirmAndRemove(selected);
        }

        @Override
        public String getCaption() {
            return null;
        }
    }
}