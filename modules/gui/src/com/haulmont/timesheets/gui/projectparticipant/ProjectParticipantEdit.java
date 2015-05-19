/*
 * Copyright (c) 2015 com.haulmont.ts.gui.projectparticipant
 */
package com.haulmont.timesheets.gui.projectparticipant;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.ProjectParticipant;
import com.haulmont.timesheets.gui.ComponentsHelper;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

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