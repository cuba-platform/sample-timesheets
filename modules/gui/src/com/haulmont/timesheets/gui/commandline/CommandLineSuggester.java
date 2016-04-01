
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

package com.haulmont.timesheets.gui.commandline;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.SourceCodeEditor;
import com.haulmont.cuba.gui.components.autocomplete.AutoCompleteSupport;
import com.haulmont.cuba.gui.components.autocomplete.Suggester;
import com.haulmont.cuba.gui.components.autocomplete.Suggestion;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.ActivityType;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.Tag;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.global.CommandLineProcessor;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author degtyarjov
 */
public class CommandLineSuggester implements Suggester {
    protected SourceCodeEditor sourceCodeEditor;
    protected ProjectsService projectsService = AppBeans.get(ProjectsService.NAME);
    protected UserSessionSource userSessionSource = AppBeans.get(UserSessionSource.NAME);

    public CommandLineSuggester(SourceCodeEditor sourceCodeEditor) {
        this.sourceCodeEditor = sourceCodeEditor;
    }

    @Override
    public List<Suggestion> getSuggestions(AutoCompleteSupport source, String text, int cursorPosition) {
        User currentUser = userSessionSource.getUserSession().getCurrentOrSubstitutedUser();

        List<Suggestion> suggestions = new ArrayList<>();
        CommandLineProcessor commandLineProcessor = new CommandLineProcessor(text);

        if (StringUtils.isBlank(text)) {
            addProjectsToSuggestions(cursorPosition, currentUser, suggestions, "@");
            addTasksToSuggestions(commandLineProcessor, cursorPosition, currentUser, suggestions, "#");
            return suggestions;
        }

        if (text.charAt(cursorPosition - 1) == '@') {
            addProjectsToSuggestions(cursorPosition, currentUser, suggestions, "");
        } else if (text.charAt(cursorPosition - 1) == '#') {
            addTasksToSuggestions(commandLineProcessor, cursorPosition, currentUser, suggestions, "");
        } else if (text.charAt(cursorPosition - 1) == '$') {
            Project project = resolveProjectId(commandLineProcessor);

            if (project != null) {
                List<Tag> tags = projectsService.getTagsForTheProject(project, "tag-with-type");
                for (Tag tag : tags) {
                    Suggestion suggestion = suggestion(tag.getInstanceName(), tag.getCode(), cursorPosition);
                    suggestions.add(suggestion);
                }
            }
        } else if (text.charAt(cursorPosition - 1) == '*') {
            Project project = resolveProjectId(commandLineProcessor);

            if (project != null) {
                List<ActivityType> activityTypes = projectsService.getActivityTypesForProject(project, View.LOCAL);
                for (ActivityType activityType : activityTypes) {
                    Suggestion suggestion = suggestion(activityType.getInstanceName(), activityType.getCode(), cursorPosition);
                    suggestions.add(suggestion);
                }
            }
        } else if (text.charAt(cursorPosition - 1) == ' ') {
            if (StringUtils.isNotBlank(commandLineProcessor.getTaskCode())) {
                suggestions.add(suggestion("8:00", "8:00", cursorPosition));
                suggestions.add(suggestion("4:00", "4:00", cursorPosition));
                suggestions.add(suggestion("2:00", "2:00", cursorPosition));
            } else {
                addTasksToSuggestions(commandLineProcessor, cursorPosition, currentUser, suggestions, "#");
            }
        }

        return suggestions;
    }

    private void addTasksToSuggestions(CommandLineProcessor commandLineProcessor, int cursorPosition, User currentUser, List<Suggestion> suggestions, String prefix) {
        String projectCode = commandLineProcessor.getProjectCode();
        Collection<Task> tasks = Collections.emptyList();
        if (projectCode != null) {
            Project project = projectsService.getEntityByCode(Project.class, projectCode, null);
            if (project != null) {
                tasks = projectsService.getActiveTasksForUserAndProject(currentUser, project, "task-full").values();
            }
        } else {
            tasks = projectsService.getActiveTasksForUser(currentUser, "task-full");
        }

        for (Task task : tasks) {
            Suggestion suggestion = suggestion(prefix + task.getInstanceName(), prefix + task.getCode(), cursorPosition);
            suggestions.add(suggestion);
        }
    }

    private void addProjectsToSuggestions(int cursorPosition, User currentUser, List<Suggestion> suggestions, String prefix) {
        List<Project> projects = projectsService.getActiveProjectsForUser(currentUser, View.LOCAL);
        for (Project project : projects) {
            Suggestion suggestion = suggestion(prefix + project.getInstanceName(), prefix + project.getCode(), cursorPosition);
            suggestions.add(suggestion);
        }
    }

    protected Project resolveProjectId(CommandLineProcessor commandLineProcessor) {
        Project project = null;
        String projectCode = commandLineProcessor.getProjectCode();
        String taskCode = commandLineProcessor.getTaskCode();
        if (projectCode != null) {
            project = projectsService.getEntityByCode(Project.class, projectCode, View.MINIMAL);
        } else if (taskCode != null) {
            Task task = projectsService.getEntityByCode(Task.class, taskCode, "task-full");
            if (task != null) {
                project = task.getProject();
            }
        }
        return project;
    }

    protected Suggestion suggestion(String caption, String value, int cursorPosition) {
        return new Suggestion(sourceCodeEditor.getAutoCompleteSupport(), caption, value + " ",
                "", cursorPosition, cursorPosition + 10);
    }
}
