/*
 * Copyright (c) 2015 com.haulmont.ts.gui.tag
 */
package com.haulmont.timesheets.gui.tag;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;

import javax.inject.Named;
import java.util.Map;

/**
 * @author gorelov
 */
public class TagBrowse extends AbstractLookup {

    @Named("tagsTable.create")
    protected CreateAction tagsTableCreate;
    @Named("tagsTable.edit")
    protected EditAction tagsTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setWidth(800);
        getDialogParams().setHeight(500);

        tagsTableCreate.setOpenType(WindowManager.OpenType.DIALOG);
        tagsTableEdit.setOpenType(WindowManager.OpenType.DIALOG);
    }
}