/*
 * Copyright (c) 2015 com.haulmont.ts.gui.client
 */
package com.haulmont.timesheets.gui.client;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;

import javax.inject.Named;
import java.util.Map;

/**
 * @author gorelov
 */
public class ClientBrowse extends AbstractLookup {

    @Named("clientsTable.create")
    protected CreateAction clientsTableCreate;
    @Named("clientsTable.edit")
    protected EditAction clientsTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        clientsTableCreate.setOpenType(WindowManager.OpenType.DIALOG);
        clientsTableEdit.setOpenType(WindowManager.OpenType.DIALOG);
    }
}