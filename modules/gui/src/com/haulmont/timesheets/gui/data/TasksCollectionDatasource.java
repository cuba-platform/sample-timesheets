
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
