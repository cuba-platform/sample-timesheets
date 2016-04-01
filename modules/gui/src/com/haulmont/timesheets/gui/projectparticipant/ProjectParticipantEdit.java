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