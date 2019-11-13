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

import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.ProjectParticipant;

import javax.inject.Inject;

/**
 * @author gorelov
 */
@UiController("ts$ProjectParticipant.lookup")
@UiDescriptor("projectparticipant-lookup.xml")
@LookupComponent("projectParticipantsTable")
@LoadDataBeforeShow
public class ProjectparticipantLookup extends StandardLookup<ProjectParticipant> {

    @Inject
    protected Table<ProjectParticipant> projectParticipantsTable;
    @Inject
    protected CollectionLoader<ProjectParticipant> projectParticipantsDl;

    protected Project project;

    public void setProject(Project project) {
        this.project = project;
        projectParticipantsDl.setParameter("project", project);
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        if (project != null)
            projectParticipantsDl.load();
    }
}