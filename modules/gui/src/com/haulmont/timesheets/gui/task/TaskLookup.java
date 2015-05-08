/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui.task
 */
package com.haulmont.timesheets.gui.task;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.timesheets.entity.Task;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Map;

/**
 * @author gorelov
 */
public class TaskLookup extends AbstractLookup {

    @Inject
    protected Table tasksTable;

    @Override
    public void init(Map<String, Object> params) {
        tasksTable.setStyleProvider(new Table.StyleProvider() {
            @Nullable
            @Override
            public String getStyleName(Entity entity, @Nullable String property) {
                if ("status".equals(property)) {
                    Task task = (Task) entity;
                    switch (task.getStatus()) {
                        case ACTIVE:
                            return "task-active";
                        case INACTIVE:
                            return "task-inactive";
                        default:
                            return null;
                    }
                }
                return null;
            }
        });
    }
}