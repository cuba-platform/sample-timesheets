/*
 * Copyright (c) 2015 com.haulmont.ts.gui.tasktype
 */
package com.haulmont.timesheets.gui.tasktype;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.timesheets.entity.TaskType;
import com.haulmont.timesheets.gui.ComponentsHelper;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author gorelov
 */
public class TaskTypeEdit extends AbstractEditor<TaskType> {

    @Inject
    protected FieldGroup fieldGroup;

    @Inject
    private Datasource<TaskType> taskTypeDs;

    @Override
    public void init(Map<String, Object> params) {
        fieldGroup.addCustomField("description", ComponentsHelper.getCustomTextArea());
        taskTypeDs.addListener(new ComponentsHelper.EntityCodeGenerationListener<TaskType>());
    }
}