/*
 * Copyright (c) 2015 com.haulmont.ts.gui.timeentry
 */
package com.haulmont.timesheets.gui.timeentry;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.SourceCodeEditor;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.gui.CommandLineSuggester;
import com.haulmont.timesheets.gui.ComponentsHelper;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
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

        commandLine.setSuggester(new CommandLineSuggester(commandLine));
    }

}