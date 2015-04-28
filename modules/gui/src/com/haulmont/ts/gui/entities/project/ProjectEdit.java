/*
 * Copyright (c) 2015 com.haulmont.ts.gui.entities.project
 */
package com.haulmont.ts.gui.entities.project;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.DialogParams;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.ts.entity.Client;
import com.haulmont.ts.entity.Project;
import com.haulmont.ts.entity.ProjectStatus;
import com.haulmont.ts.service.ProjectsService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * @author gorelov
 */
public class ProjectEdit extends AbstractEditor<Project> {
    @Inject
    private FieldGroup fieldGroup;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private CollectionDatasource<Project, UUID> projectsDs;
    @Inject
    private ProjectsService projectsService;
    @Inject
    private Table participantsTable;

    @Named("fieldGroup.parent")
    private LookupPickerField parentField;
    @Named("fieldGroup.client")
    private LookupPickerField clientField;
    @Named("participantsTable.create")
    private CreateAction participantsTableCreate;

    private List<Project> childrenProjects;

    @Override
    public void init(final Map<String, Object> params) {

        parentField.addAction(createLookupAction(parentField));
        parentField.addAction(new PickerField.ClearAction(parentField));

        clientField.addAction(createLookupAction(clientField));
        clientField.addAction(new PickerField.ClearAction(clientField));

        parentField.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                clientField.setEnabled(value == null);
                if (value != null) {
                    Project parent = (Project) value;
                    if (!parent.getClient().equals(getItem().getClient()))
                        clientField.setValue(parent.getClient());
                } else if (prevValue != null) {
                    clientField.setValue(null);
                }
            }
        });

        clientField.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                if (childrenProjects != null && value != null) {
                    Client client = (Client) value;
                    for (Project child : childrenProjects)
                        projectsService.setClient(child, client);
                }
            }
        });

        fieldGroup.addCustomField("description", new FieldGroup.CustomFieldGenerator() {
            @Override
            public Component generateField(Datasource datasource, String propertyId) {
                ResizableTextArea textArea = componentsFactory.createComponent(ResizableTextArea.NAME);
                textArea.setHeight("100px");
                textArea.setResizable(true);
                return textArea;
            }
        });

        participantsTableCreate.setOpenType(WindowManager.OpenType.DIALOG);

        participantsTable.addGeneratedColumn("remove", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                LinkButton removeButton = componentsFactory.createComponent(LinkButton.NAME);
                removeButton.setIcon("icons/remove.png");
                removeButton.setAction(new ParticipantRemoveAction(participantsTable, entity));
                return removeButton;
            }
        });
        participantsTable.setColumnCaption("remove", "");
    }

    @Override
    protected void postInit() {
        Project project = getItem();
        projectsDs.excludeItem(project);
        childrenProjects = projectsService.getChildren(project);
        for (Project child : childrenProjects)
            projectsDs.excludeItem(child);
        project.setStatus(ProjectStatus.OPEN);
    }

    private PickerField.LookupAction createLookupAction(PickerField pickerField) {
        PickerField.LookupAction lookupAction = new PickerField.LookupAction(pickerField);
        lookupAction.setLookupScreenOpenType(WindowManager.OpenType.DIALOG);
        lookupAction.setLookupScreenDialogParams(new DialogParams()
                .setHeight(480)
                .setWidth(640)
                .setResizable(true));
        return lookupAction;
    }

    private class ParticipantRemoveAction extends RemoveAction {

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