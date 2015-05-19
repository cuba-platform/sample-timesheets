/*
 * Copyright (c) 2015 com.haulmont.ts.gui.tasktype
 */
package com.haulmont.timesheets.gui.tasktype;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;

import javax.inject.Named;
import java.util.Map;

/**
 * @author gorelov
 */
public class TaskTypeBrowse extends AbstractLookup {

    @Named("taskTypesTable.create")
    protected CreateAction taskTypesTableCreate;
    @Named("taskTypesTable.edit")
    protected EditAction taskTypesTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        taskTypesTableCreate.setOpenType(WindowManager.OpenType.DIALOG);
        taskTypesTableEdit.setOpenType(WindowManager.OpenType.DIALOG);
    }
}