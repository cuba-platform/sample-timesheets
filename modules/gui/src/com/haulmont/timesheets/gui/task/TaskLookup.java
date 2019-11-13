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

package com.haulmont.timesheets.gui.task;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.gui.util.ScreensHelper;
import com.haulmont.timesheets.service.ProjectsService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author gorelov
 */
@UiController("ts$Task.lookup")
@UiDescriptor("task-lookup.xml")
@LookupComponent("tasksTable")
@LoadDataBeforeShow
public class TaskLookup extends StandardLookup<Task> {

    @Inject
    protected UserSession userSession;

    @Install(to = "tasksDl", target = Target.DATA_LOADER)
    protected List<Task> tasksDlLoadDelegate(LoadContext<Task> loadContext) {
        ProjectsService projectsService = AppBeans.get(ProjectsService.NAME);
        return projectsService.getActiveTasksForUser(userSession.getCurrentOrSubstitutedUser(), "task-preview");
    }

    @Install(to = "tasksTable", subject = "styleProvider")
    protected String tasksTableStyleProvider(Task task, String property) {
        if ("status".equals(property)) {
            return ScreensHelper.getTaskStatusStyle(task);
        }
        return null;
    }
}