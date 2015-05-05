/*
 * Copyright (c) 2015 com.haulmont.ts.gui.tagtype
 */
package com.haulmont.timesheets.gui.tagtype;

import com.haulmont.cuba.gui.components.AbstractLookup;

import java.util.Map;

/**
 * @author gorelov
 */
public class TagTypeBrowse extends AbstractLookup {
    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setWidth(800);
        getDialogParams().setHeight(500);
    }
}