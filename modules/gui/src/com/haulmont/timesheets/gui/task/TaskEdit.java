/*
 * Copyright (c) 2015 com.haulmont.ts.gui.task
 */
package com.haulmont.timesheets.gui.task;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.AddAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.gui.filter.*;
import com.haulmont.cuba.gui.filter.Condition;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.gui.ComponentsHelper;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.concurrent.locks.*;

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
    @Inject
    protected CollectionDatasource<Tag, UUID> allTagsDs;
    @Inject
    protected CollectionDatasource<TagType, UUID> allTagsTypesDs;

    @Named("fieldGroup.project")
    protected PickerField projectField;
    @Named("participantsTable.add")
    protected AddAction participantsTableAdd;

    @Override
    public void init(Map<String, Object> params) {

        fieldGroup.addCustomField("description", ComponentsHelper.getCustomTextArea());
        ComponentsHelper.addRemoveColumn(participantsTable, "remove");

        projectField.addAction(ComponentsHelper.createLookupAction(projectField));

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
                    updateTagTypeQuery(value);
                }
            }
        });
    }

    @Override
    protected void postInit() {
        super.postInit();
        Task task = getItem();
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.ACTIVE);
        }
        projectField.setEnabled(task.getProject() == null);
        updateParticipantsTableAddAction();
        updateTagTypeQuery(task.getProject());
    }

    protected void updateParticipantsTableAddAction() {
        participantsTableAdd.setWindowParams(ParamsMap.of("project", getItem().getProject(), "multiselect", true));
        participantsTableAdd.setEnabled(getItem().getProject() != null);
    }

    private void updateTagTypeQuery(Object value) {
        Condition condition = new LogicalCondition(LogicalOp.OR);
        List<Condition> conditions = new ArrayList<>(2);
        conditions.add(new Clause("e.project is null", null));
        if (value != null) {
            conditions.add(new Clause("e.project.id = :component$fieldGroup.project", null));
        }
        condition.setConditions(conditions);
        allTagsTypesDs.setQueryFilter(new QueryFilter(condition, "ts$TagType"));
        allTagsTypesDs.refresh();
    }
}