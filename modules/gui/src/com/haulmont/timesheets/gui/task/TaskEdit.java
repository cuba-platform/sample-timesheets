/*
 * Copyright (c) 2015 com.haulmont.ts.gui.task
 */
package com.haulmont.timesheets.gui.task;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.AddAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.timesheets.entity.ProjectParticipant;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.entity.TaskStatus;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * @author gorelov
 */
public class TaskEdit extends AbstractEditor<Task> {

    @Inject
    protected Table participantsTable;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected Datasource<Task> taskDs;
    @Inject
    protected CollectionDatasource<ProjectParticipant, UUID> participantsDs;

    @Named("fieldGroup.project")
    protected PickerField projectField;
    @Named("participantsTable.add")
    protected AddAction participantsTableAdd;

    @Override
    public void init(Map<String, Object> params) {

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

        taskDs.addListener(new DsListenerAdapter<Task>() {
            @Override
            public void valueChanged(Task source, String property, Object prevValue, Object value) {
                if ("project".equals(property)) {
                    updateParticipantsTableAddAction();
                    if (value == null || !value.equals(prevValue)) {
                        Collection<ProjectParticipant> participants = participantsDs.getItems();
                        for (ProjectParticipant participant : participants) {
                            participantsDs.removeItem(participant);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void postInit() {
        super.postInit();
        getItem().setStatus(TaskStatus.ACTIVE);
        projectField.setEnabled(getItem().getProject() == null);
        updateParticipantsTableAddAction();
    }

    protected void updateParticipantsTableAddAction() {
        participantsTableAdd.setWindowParams(ParamsMap.of("project", getItem().getProject(), "multiselect", true));
        participantsTableAdd.setEnabled(getItem().getProject() != null);
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