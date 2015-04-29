/*
 * Copyright (c) 2015 com.haulmont.ts.gui.tag
 */
package com.haulmont.timesheets.gui.tag;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.timesheets.entity.Tag;
import com.haulmont.timesheets.gui.ComponentsHelper;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author gorelov
 */
public class TagEdit extends AbstractEditor<Tag> {

    @Inject
    protected FieldGroup fieldGroup;

    @Override
    public void init(Map<String, Object> params) {
        fieldGroup.addCustomField("description", ComponentsHelper.getCustomTextArea());
    }
}