/*
 * Copyright (c) 2016 Haulmont
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.timesheets.gui.projectparticipant;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.ProjectParticipant;
import com.haulmont.timesheets.gui.util.ComponentsHelper;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * @author gorelov
 */
@UiController("ts$ProjectParticipant.edit")
@UiDescriptor("projectparticipant-edit.xml")
@EditedEntityContainer("projectParticipantDc")
@LoadDataBeforeShow
public class ProjectParticipantEdit extends StandardEditor<ProjectParticipant> {
    @Inject
    protected ScreenBuilders screenBuilders;

    @Inject
    protected PickerField<Project> project;
    @Inject
    protected LookupPickerField<User> user;

    @Subscribe("project.lookup")
    protected void onProjectLookup(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(Project.class, this)
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();
    }

    @Subscribe
    protected void onInit(InitEvent event) {
        PickerField.LookupAction lookupAction = new PickerField.LookupAction(user);
        lookupAction.setLookupScreenOpenType(WindowManager.OpenType.DIALOG
                .setWidth(800f)
                .setHeight(500f)
                .setResizable(true));
        lookupAction.setLookupScreenParams(ParamsMap.of("multiselect", false));
        user.addAction(lookupAction, 0);
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        ProjectParticipant participant = getEditedEntity();
        if (participant.getProject() != null) {
            project.setEnabled(false);
        }
    }
}