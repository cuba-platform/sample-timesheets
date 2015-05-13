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

    @Inject
    protected Datasource<ProjectParticipant> projectParticipantDs;

    @Named("fieldGroup.user")
    protected LookupPickerField userField;
    @Named("fieldGroup.project")
    protected PickerField projectField;

    protected final UniqueUserValidator validator = new UniqueUserValidator();

    @Override
    public void init(Map<String, Object> params) {
        userField.addAction(ComponentsHelper.createLookupAction(userField));
        userField.addAction(new PickerField.ClearAction(userField));

        projectField.addAction(ComponentsHelper.createLookupAction(projectField));

        userField.addValidator(validator);

        projectParticipantDs.addListener(new DsListenerAdapter<ProjectParticipant>() {
            @Override
            public void valueChanged(ProjectParticipant source, String property, Object prevValue, Object value) {
                if ("project".equals(property)) {
                    if (value != null) {
                        if (!value.equals(prevValue)) {
                            Project project = (Project) value;
                            validator.setAssignedUsers(getAssignedUsers(project));
                        }
                    } else {
                        validator.setAssignedUsers(null);
                    }
                }
            }
        });
    }

    @Override
    protected void postInit() {
        super.postInit();
        ProjectParticipant participant = getItem();
        if (participant.getProject() != null) {
            projectField.setEnabled(false);
            validator.setAssignedUsers(getAssignedUsers(participant.getProject()));
        }
    }

    protected Collection<User> getAssignedUsers(Project project) {
        Set<ProjectParticipant> participants = project.getParticipants();
        if (participants != null && !participants.isEmpty()) {
            List<User> assignedUsers = new ArrayList<>(participants.size());
            for (ProjectParticipant participant : participants) {
                assignedUsers.add(participant.getUser());
            }
            return assignedUsers;
        }
        return Collections.emptyList();
    }
}