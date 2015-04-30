/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.timesheets.gui.project;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.TreeTable;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author gorelov
 */
public class ProjectBrowse extends AbstractLookup {

    @Inject
    protected TreeTable projectsTable;
    @Inject
    protected Table tasksTable;

    @Override
    public void init(Map<String, Object> params) {
        projectsTable.addAction(new CustomEditAction(projectsTable));
        tasksTable.addAction(new TaskCreateAction(tasksTable));
    }

    protected class CustomEditAction extends EditAction {

        public CustomEditAction(ListComponent target) {
            super(target);
        }

        @Override
        protected void afterCommit(Entity entity) {
            projectsTable.refresh();
        }
    }

    @Override
    public void ready() {
        projectsTable.expandAll();
    }

    protected class TaskCreateAction extends CreateAction {

        public TaskCreateAction(ListComponent target) {
            super(target);
        }

        @Override
        public Map<String, Object> getInitialValues() {
            return ParamsMap.of("project", projectsTable.getSingleSelected());
        }
    }
}