/*
 * Copyright (c) 2015 com.haulmont.ts.gui.entities.task
 */
package com.haulmont.ts.gui.entities.task;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.ts.entity.Task;
import com.haulmont.ts.entity.TaskStatus;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author gorelov
 */
public class TaskEdit extends AbstractEditor<Task> {

    @Inject
    private Table participantsTable;
    @Inject
    private ComponentsFactory componentsFactory;

    @Named("fieldGroup.project")
    private PickerField projectField;

    @Override
    public void init(Map<String, Object> params) {
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
        super.postInit();
        getItem().setStatus(TaskStatus.ACTIVE);
        if (getItem().getProject() != null)
            projectField.setEnabled(false);
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