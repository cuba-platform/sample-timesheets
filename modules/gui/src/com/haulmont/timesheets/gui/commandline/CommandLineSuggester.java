/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
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
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.global.CommandLineUtils;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author degtyarjov
 * @version $Id$
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
        User currentUser = userSessionSource.getUserSession().getUser();

        List<Suggestion> suggestions = new ArrayList<>();
        CommandLineUtils commandLineUtils = new CommandLineUtils(text);

        if (StringUtils.isBlank(text)) {
            List<Project> projects = projectsService.getActiveProjectsForUser(currentUser, View.LOCAL);
            for (Project project : projects) {
                Suggestion suggestion = suggestion("@" + project.getInstanceName(), "@" + project.getCode(), cursorPosition);
                suggestions.add(suggestion);
            }

            Collection<Task> tasks = projectsService.getActiveTasksForUser(currentUser, "task-full");
            for (Task task : tasks) {
                Suggestion suggestion = suggestion("#" + task.getInstanceName(), "#" + task.getCode(), cursorPosition);
                suggestions.add(suggestion);
            }

            return suggestions;
        }

        if (text.charAt(cursorPosition - 1) == '@') {
            List<Project> projects = projectsService.getActiveProjectsForUser(currentUser, View.LOCAL);
            for (Project project : projects) {
                Suggestion suggestion = suggestion(project.getInstanceName(), project.getCode(), cursorPosition);
                suggestions.add(suggestion);
            }
        } else if (text.charAt(cursorPosition - 1) == '#') {
            String projectCode = commandLineUtils.getProjectCode();
            Collection<Task> tasks;
            if (projectCode != null) {
                Project project = projectsService.getEntityByCode(Project.class, projectCode, null);
                tasks = projectsService.getActiveTasksForUserAndProject(currentUser, project, "task-full").values();

            } else {
                tasks = projectsService.getActiveTasksForUser(currentUser, "task-full");
            }

            for (Task task : tasks) {
                Suggestion suggestion = suggestion(task.getInstanceName(), task.getCode(), cursorPosition);
                suggestions.add(suggestion);
            }
        }

        return suggestions;
    }

    protected Suggestion suggestion(String caption, String value, int cursorPosition) {
        return new Suggestion(sourceCodeEditor.getAutoCompleteSupport(), caption, value,
                "", cursorPosition, cursorPosition + 10);
    }
}
