/*
 * Copyright (c) 2015 com.haulmont.ts.gui.entities.project
 */
package com.haulmont.ts.gui.entities.project;

import com.haulmont.cuba.gui.DialogParams;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.ts.entity.Project;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * @author gorelov
 */
public class ProjectEdit extends AbstractEditor<Project> {
    @Inject
    private FieldGroup fieldGroup;
    @Inject
    private ComponentsFactory componentsFactory;

    @Named("fieldGroup.parent")
    private LookupPickerField parentField;
    @Named("fieldGroup.client")
    private LookupPickerField clientField;

    @Override
    public void init(final Map<String, Object> params) {

        parentField.addAction(createLookupAction(parentField));
        parentField.addAction(new PickerField.ClearAction(parentField));

        clientField.addAction(createLookupAction(clientField));
        clientField.addAction(new PickerField.ClearAction(clientField));

        parentField.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                clientField.setEnabled(value == null);
                if (value != null) {
                    Project parent = (Project) value;
                    clientField.setValue(parent.getClient());
                }
            }
        });

        fieldGroup.addCustomField("description", new FieldGroup.CustomFieldGenerator() {
            @Override
            public Component generateField(Datasource datasource, String propertyId) {
                ResizableTextArea textArea = componentsFactory.createComponent(ResizableTextArea.NAME);
                textArea.setHeight("100px");
                textArea.setResizable(true);
                return textArea;
            }
        });
    }

    private PickerField.LookupAction createLookupAction(PickerField pickerField) {
        PickerField.LookupAction lookupAction = new PickerField.LookupAction(pickerField);
        lookupAction.setLookupScreenOpenType(WindowManager.OpenType.DIALOG);
        lookupAction.setLookupScreenDialogParams(new DialogParams()
                .setHeight(480)
                .setWidth(640)
                .setResizable(true));
        return lookupAction;
    }
}