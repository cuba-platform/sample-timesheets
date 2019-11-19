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

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.actions.list.AddAction;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.InstanceLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.gui.projectparticipant.ProjectparticipantLookup;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author gorelov
 */
@UiController("ts$Task.edit")
@UiDescriptor("task-edit.xml")
@EditedEntityContainer("taskDc")
public class TaskEdit extends StandardEditor<Task> {

    @Inject
    protected UserSession userSession;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ScreenBuilders screenBuilders;

    @Inject
    protected TabSheet tabsheet;
    @Inject
    protected InstanceContainer<Task> taskDc;
    @Inject
    protected InstanceLoader<Task> taskDl;
    @Inject
    protected CollectionLoader<TagType> allTagsTypesDl;
    @Inject
    protected CollectionLoader<Tag> allTagsDl;
    @Inject
    protected CollectionLoader<TaskType> taskTypesDl;
    @Inject
    protected LookupPickerField<TaskType> type;

    @Inject
    protected Form form;

    @Inject
    protected PickerField<Project> project;
    @Named("participantsTable.add")
    protected AddAction participantsTableAdd;
    @Inject
    protected Table<ProjectParticipant> participantsTable;

    @Subscribe
    protected void onInit(InitEvent event) {
        if (!userSession.isEntityOpPermitted(metadata.getClassNN(Task.class), EntityOp.UPDATE)) {
            tabsheet.getTab("advanced").setVisible(false);
        }
    }

    @Subscribe
    protected void onInitEntity(InitEntityEvent<Task> event) {
        Task item = event.getEntity();
        if (item.getStatus() == null) {
            item.setStatus(TaskStatus.ACTIVE);
        }
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        taskDl.load();
        taskTypesDl.load();
        project.setEnabled(getEditedEntity() == null);
    }

    @Subscribe("project.lookup")
    protected void onProjectLookupActionPerformed(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(project)
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();
    }

    @Subscribe("type.lookup")
    protected void onTypeLookupActionPerformed(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(type)
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();
    }

    @Subscribe("participantsTable.add")
    protected void participantsTableAddAction(Action.ActionPerformedEvent e) {
        ProjectparticipantLookup lookup = screenBuilders.lookup(ProjectParticipant.class, this)
                .withScreenClass(ProjectparticipantLookup.class)
                .withListComponent(participantsTable)
                .withLaunchMode(OpenMode.THIS_TAB)
                .build();
        lookup.setProject(getEditedEntity().getProject());
        lookup.show();
    }

    protected void onProjectChange() {
        Project pr = getEditedEntity().getProject();
        participantsTableAdd.setEnabled(pr != null);
        allTagsTypesDl.setParameter("project", pr);
        allTagsTypesDl.load();
        allTagsDl.setParameter("project", pr);
        allTagsDl.load();
    }

    @Subscribe(id = "taskDc", target = Target.DATA_CONTAINER)
    protected void onTaskDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<Task> e) {
        if ("project".equalsIgnoreCase(e.getProperty())) {
            onProjectChange();
        }
        if ("name".equalsIgnoreCase(e.getProperty())) {
            Task source = e.getItem();
            String codeValue = source.getCode();
            if (StringUtils.isBlank(codeValue) && source.getProject() != null) {
                String newName = String.valueOf(e.getValue());
                String newCode = e.getItem().getProject().getCode() + "_" + newName.toUpperCase().replaceAll(" ", "_");
                e.getItem().setCode(newCode);
            }
        }
    }

    @Subscribe(id = "taskDc", target = Target.DATA_CONTAINER)
    protected void onTaskDcItemChange(InstanceContainer.ItemChangeEvent<Task> event) {
        onProjectChange();
    }
}