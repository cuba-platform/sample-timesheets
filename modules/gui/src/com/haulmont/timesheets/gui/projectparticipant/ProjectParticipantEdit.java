/*
 * Copyright (c) 2015 com.haulmont.ts.gui.projectparticipant
 */
package com.haulmont.timesheets.gui.projectparticipant;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.timesheets.entity.ProjectParticipant;
import com.haulmont.timesheets.gui.util.ComponentsHelper;

import javax.inject.Named;
import java.util.Map;

/**
 * @author gorelov
 */
public class ProjectParticipantEdit extends AbstractEditor<ProjectParticipant> {

    @Named("fieldGroup.user")
    protected LookupPickerField userField;
    @Named("fieldGroup.project")
    protected PickerField projectField;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setWidthAuto();

        userField.addAction(ComponentsHelper.createLookupAction(userField));
        userField.addAction(new PickerField.ClearAction(userField));

        projectField.addAction(ComponentsHelper.createLookupAction(projectField));
    }

    @Override
    protected void postInit() {
        super.postInit();
        ProjectParticipant participant = getItem();
        if (participant.getProject() != null) {
            projectField.setEnabled(false);
        }
    }
}