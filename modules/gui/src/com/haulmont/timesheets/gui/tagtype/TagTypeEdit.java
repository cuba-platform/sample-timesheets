/*
 * Copyright (c) 2015 com.haulmont.ts.gui.tagtype
 */
package com.haulmont.timesheets.gui.tagtype;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.timesheets.entity.TagType;
import com.haulmont.timesheets.gui.util.ComponentsHelper;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author gorelov
 */
public class TagTypeEdit extends AbstractEditor<TagType> {

    @Inject
    protected FieldGroup fieldGroup;

    @Inject
    private Datasource<TagType> tagTypeDs;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setWidth(600);

        fieldGroup.addCustomField("description", ComponentsHelper.getCustomTextArea());
        tagTypeDs.addItemPropertyChangeListener(new ComponentsHelper.EntityCodeGenerationListener<>());
    }
}