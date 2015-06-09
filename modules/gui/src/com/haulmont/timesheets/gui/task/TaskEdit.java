/*
 * Copyright (c) 2015 com.haulmont.ts.gui.task
 */
package com.haulmont.timesheets.gui.task;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.actions.AddAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.gui.ComponentsHelper;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

/**
 * @author gorelov
 */
public class TaskEdit extends AbstractEditor<Task> {
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
    @Inject
    protected CollectionDatasource<Tag, UUID> defaultTagsDs;
    @Inject
    protected CollectionDatasource<TagType, UUID> requiredTagTypesDs;

    @Named("fieldGroup.project")
    protected PickerField projectField;
    @Named("fieldGroup.type")
    protected LookupPickerField typeField;
    @Named("participantsTable.add")
    protected AddAction participantsTableAdd;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setWidthAuto();

        fieldGroup.addCustomField("description", ComponentsHelper.getCustomTextArea());

        projectField.addAction(ComponentsHelper.createLookupAction(projectField));
        typeField.addAction(ComponentsHelper.createLookupAction(typeField));

        taskDs.addListener(new DsListenerAdapter<Task>() {
            @Override
            public void valueChanged(Task source, String property, Object prevValue, Object value) {
                if ("project".equals(property)) {
                    updateParticipantsTableAddAction();
                    participantsDs.clear();
                    allTagsTypesDs.refresh();
                    allTagsDs.refresh();
                }
            }
        });

        taskDs.addListener(new DsListenerAdapter<Task>() {
            @Override
            public void valueChanged(Task source, String property, Object prevValue, Object value) {
                if ("name".equalsIgnoreCase(property)) {
                    String codeValue = source.getCode();
                    if (StringUtils.isBlank(codeValue) && source.getProject() != null) {
                        String newName = String.valueOf(value);
                        String newCode = source.getProject().getCode() + "_" + newName.toUpperCase().replaceAll(" ", "_");
                        source.setCode(newCode);
                    }
                }
            }
        });
    }

    @Override
    protected void initNewItem(Task item) {
        super.initNewItem(item);
        if (item.getStatus() == null) {
            item.setStatus(TaskStatus.ACTIVE);
        }
    }

    @Override
    protected void postInit() {
        super.postInit();
        Task task = getItem();
        projectField.setEnabled(task.getProject() == null);
        updateParticipantsTableAddAction();
    }

    protected void updateParticipantsTableAddAction() {
        participantsTableAdd.setWindowParams(ParamsMap.of("project", getItem().getProject(), "multiselect", true));
        participantsTableAdd.setEnabled(getItem().getProject() != null);
    }
}