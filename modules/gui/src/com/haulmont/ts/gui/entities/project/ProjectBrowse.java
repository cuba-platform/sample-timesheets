/*
 * Copyright (c) 2015 com.haulmont.ts.gui.entities.project
 */
package com.haulmont.ts.gui.entities.project;

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
    private TreeTable projectsTable;

    @Override
    public void init(Map<String, Object> params) {
        projectsTable.addAction(new CustomEditAction(projectsTable));
    }

    private class CustomEditAction extends EditAction {

        public CustomEditAction(ListComponent target) {
            super(target);
        }

        @Override
        protected void afterCommit(Entity entity) {
            projectsTable.refresh();
        }
    }
}