/*
 * Copyright (c) 2015 com.haulmont.ts.gui.tagtype
 */
package com.haulmont.timesheets.gui.tagtype;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.timesheets.entity.TagType;
import com.haulmont.timesheets.gui.ComponentsHelper;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * @author gorelov
 */
public class TagTypeEdit extends AbstractEditor<TagType> {

    @Inject
    protected FieldGroup fieldGroup;

    @Named("fieldGroup.project")
    protected PickerField projectField;

    @Override
    public void init(Map<String, Object> params) {

        projectField.addAction(ComponentsHelper.createLookupAction(projectField));

        fieldGroup.addCustomField("description", ComponentsHelper.getCustomTextArea());
    }
}