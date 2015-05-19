/*
 * Copyright (c) 2015 com.haulmont.ts.gui.holiday
 */
package com.haulmont.timesheets.gui.holiday;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;

import javax.inject.Named;
import java.util.Map;

/**
 * @author gorelov
 */
public class HolidayBrowse extends AbstractLookup {

    @Named("holidaysTable.edit")
    protected EditAction holidaysTableEdit;
    @Named("holidaysTable.create")
    protected CreateAction holidaysTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        holidaysTableCreate.setOpenType(WindowManager.OpenType.DIALOG);
        holidaysTableEdit.setOpenType(WindowManager.OpenType.DIALOG);
    }
}