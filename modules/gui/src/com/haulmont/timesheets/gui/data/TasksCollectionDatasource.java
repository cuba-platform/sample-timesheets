
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

package com.haulmont.timesheets.gui.data;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.GroupDatasourceImpl;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.service.ProjectsService;

import java.util.Map;
import java.util.UUID;

/**
 * @author gorelov
 */
public class TasksCollectionDatasource extends GroupDatasourceImpl<Task, UUID> {

    @Override
    protected void loadData(Map<String, Object> params) {
        detachListener(data.values());
        data.clear();

        ProjectsService projectsService = AppBeans.get(ProjectsService.NAME);
        for (Task task : projectsService.getActiveTasksForUser(userSession.getCurrentOrSubstitutedUser(), "task-full")) {
            data.put(task.getId(), task);
            attachListener(task);
        }
    }
}
