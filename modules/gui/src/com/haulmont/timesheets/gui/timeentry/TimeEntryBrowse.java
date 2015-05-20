/*
 * Copyright (c) 2015 com.haulmont.ts.gui.timeentry
 */
package com.haulmont.timesheets.gui.timeentry;

import com.haulmont.cuba.core.app.DataService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.SourceCodeEditor;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.autocomplete.AutoCompleteSupport;
import com.haulmont.cuba.gui.components.autocomplete.Suggester;
import com.haulmont.cuba.gui.components.autocomplete.Suggestion;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.global.CommandLineUtils;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.haulmont.timesheets.service.ProjectsService;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author gorelov
 */
public class TimeEntryBrowse extends AbstractLookup {
    @Inject
    protected Table timeEntriesTable;
    @Inject
    protected SourceCodeEditor commandLine;
    @Named("timeEntriesTable.edit")
    protected EditAction timeEntriesTableEdit;
    @Named("timeEntriesTable.create")
    protected CreateAction timeEntriesTableCreate;

    @Inject
    private DataService dataService;

    @Inject
    private ProjectsService projectsService;

    @Inject
    private UserSessionSource userSessionSource;

    @Override
    public void init(Map<String, Object> params) {
        timeEntriesTableCreate.setOpenType(WindowManager.OpenType.DIALOG);
        timeEntriesTableEdit.setOpenType(WindowManager.OpenType.DIALOG);

        timeEntriesTable.setStyleProvider(new Table.StyleProvider() {
            @Nullable
            @Override
            public String getStyleName(Entity entity, String property) {
                if ("status".equals(property)) {
                    TimeEntry timeEntry = (TimeEntry) entity;
                    return ComponentsHelper.getTimeEntryStatusStyle(timeEntry);
                }
                return null;
            }
        });

        commandLine.setSuggester(new Suggester() {
            @Override
            public List<Suggestion> getSuggestions(AutoCompleteSupport source, String text, int cursorPosition) {
                List<Suggestion> suggestions = new ArrayList<>();
                CommandLineUtils commandLineUtils = new CommandLineUtils(text);

                if (text.charAt(cursorPosition - 1) == '@') {
                    List<Project> projects = projectsService.getActiveProjectsForUser(userSessionSource.getUserSession().getUser());
                    for (Project project : projects) {
                        Suggestion suggestion = new Suggestion(commandLine.getAutoCompleteSupport(), project.getName(), project.getCode(), "", cursorPosition, cursorPosition + 10);
                        suggestions.add(suggestion);
                    }
                } else if (text.charAt(cursorPosition - 1) == '#') {
                    String projectCode = commandLineUtils.getProjectCode();
                    Collection<Task> tasks;
                    if (projectCode != null) {
                        Project project = dataService.load(new LoadContext(Task.class).setQuery(
                                new LoadContext.Query("select pr from ts$Project pr where pr.code = :code").setParameter("code", projectCode)));
                        tasks = projectsService.getActiveTasksForUserAndProject(userSessionSource.getUserSession().getUser(), project).values();

                    } else {
                        tasks = projectsService.getActiveTasksForUser(userSessionSource.getUserSession().getUser());
                    }

                    for (Task task : tasks) {
                        Suggestion suggestion = new Suggestion(commandLine.getAutoCompleteSupport(), task.getName(), task.getCode(), "", cursorPosition, cursorPosition + 10);
                        suggestions.add(suggestion);
                    }
                }

                return suggestions;
            }
        });
    }
}