/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.timesheets.gui.project;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.TreeTable;
import com.haulmont.cuba.gui.components.actions.EditAction;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author gorelov
 */
public class ProjectBrowse extends AbstractLookup {

    @Inject
    protected TreeTable projectsTable;

    @Override
    public void init(Map<String, Object> params) {
        projectsTable.addAction(new CustomEditAction(projectsTable));
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
}