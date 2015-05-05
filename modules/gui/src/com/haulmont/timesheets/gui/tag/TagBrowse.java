/*
 * Copyright (c) 2015 com.haulmont.ts.gui.tag
 */
package com.haulmont.timesheets.gui.tag;

import com.haulmont.cuba.gui.components.AbstractLookup;

import java.util.Map;

/**
 * @author gorelov
 */
public class TagBrowse extends AbstractLookup {
    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setWidth(800);
        getDialogParams().setHeight(500);
    }
}