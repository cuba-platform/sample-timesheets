/*
 * Copyright (c) 2015 com.haulmont.ts.gui.tagtype
 */
package com.haulmont.timesheets.gui.tagtype;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;

import javax.inject.Named;
import java.util.Map;

/**
 * @author gorelov
 */
public class TagTypeBrowse extends AbstractLookup {

    @Named("tagTypesTable.create")
    protected CreateAction tagTypesTableCreate;
    @Named("tagTypesTable.edit")
    protected EditAction tagTypesTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setWidth(800);
        getDialogParams().setHeight(500);

        tagTypesTableCreate.setOpenType(WindowManager.OpenType.DIALOG);
        tagTypesTableEdit.setOpenType(WindowManager.OpenType.DIALOG);
    }
}