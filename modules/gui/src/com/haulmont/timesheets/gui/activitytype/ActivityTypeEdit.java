/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui.activitytype
 */
package com.haulmont.timesheets.gui.activitytype;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.timesheets.entity.ActivityType;
import com.haulmont.timesheets.entity.TagType;
import com.haulmont.timesheets.gui.ComponentsHelper;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author degtyarjov
 */
public class ActivityTypeEdit extends AbstractEditor<ActivityType> {
    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private Datasource<ActivityType> activityTypeDs;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setWidth(600);

        fieldGroup.addCustomField("description", ComponentsHelper.getCustomTextArea());
        activityTypeDs.addListener(new ComponentsHelper.EntityCodeGenerationListener<ActivityType>());
    }
}