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
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.ProjectParticipant;
import org.apache.commons.lang.BooleanUtils;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

/**
 * @author gorelov
 */
public class ProjectparticipantLookup extends AbstractLookup {

    @Inject
    protected Table<ProjectParticipant> projectParticipantsTable;
    @Inject
    protected CollectionDatasource<ProjectParticipant, UUID> projectParticipantsDs;

    @Override
    public void init(Map<String, Object> params) {
        if (BooleanUtils.isTrue((Boolean) params.get("multiselect"))) {
            projectParticipantsTable.setMultiSelect(true);
        }
        Project project = (Project) params.get("project");
        projectParticipantsDs.refresh(ParamsMap.of("project", project));
    }
}