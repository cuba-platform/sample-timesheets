/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui.activitytype
 */
package com.haulmont.timesheets.gui.activitytype;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;

import javax.inject.Named;
import java.util.Map;

/**
 * @author degtyarjov
 */
public class ActivityTypeBrowse extends AbstractLookup {
    @Named("activityTypesTable.create")
    private CreateAction activityTypesTableCreate;
    @Named("activityTypesTable.edit")
    private EditAction activityTypesTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        getDialogParams().setWidth(800);
        getDialogParams().setHeight(500);

        activityTypesTableCreate.setOpenType(WindowManager.OpenType.DIALOG);
        activityTypesTableEdit.setOpenType(WindowManager.OpenType.DIALOG);
    }
}