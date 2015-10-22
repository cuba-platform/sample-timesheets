/*
 * Copyright (c) 2015 com.haulmont.ts.gui.tag
 */
package com.haulmont.timesheets.gui.tag;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.timesheets.entity.Tag;
import com.haulmont.timesheets.gui.util.ComponentsHelper;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * @author gorelov
 */
public class TagEdit extends AbstractEditor<Tag> {

    @Inject
    protected FieldGroup fieldGroup;

    @Named("fieldGroup.tagType")
    protected LookupPickerField tagTypeField;

    @Inject
    private Datasource<Tag> tagDs;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setWidthAuto();

        tagTypeField.addAction(ComponentsHelper.createLookupAction(tagTypeField));
        tagTypeField.addClearAction();

        fieldGroup.addCustomField("description", ComponentsHelper.getCustomTextArea());

        tagDs.addItemPropertyChangeListener(new ComponentsHelper.EntityCodeGenerationListener<>());
    }
}