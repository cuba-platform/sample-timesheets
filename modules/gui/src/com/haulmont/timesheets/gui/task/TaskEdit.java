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

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.AddAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.gui.util.ComponentsHelper;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

/**
 * @author gorelov
 */
public class TaskEdit extends AbstractEditor<Task> {
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected Datasource<Task> taskDs;
    @Inject
    protected CollectionDatasource<ProjectParticipant, UUID> participantsDs;
    @Inject
    protected CollectionDatasource<Tag, UUID> allTagsDs;
    @Inject
    protected CollectionDatasource<TagType, UUID> allTagsTypesDs;
    @Inject
    private TabSheet tabsheet;

    @Named("fieldGroup.project")
    protected PickerField projectField;
    @Named("fieldGroup.type")
    protected LookupPickerField typeField;
    @Named("participantsTable.add")
    protected AddAction participantsTableAdd;
    @Inject
    private UserSession userSession;
    @Inject
    private Metadata metadata;

    @Override
    public void init(Map<String, Object> params) {
        fieldGroup.addCustomField("description", ComponentsHelper.getCustomTextArea());

        projectField.addAction(ComponentsHelper.createLookupAction(projectField));
        typeField.addAction(ComponentsHelper.createLookupAction(typeField));

        taskDs.addItemPropertyChangeListener(e -> {
            if ("project".equals(e.getProperty())) {
                updateParticipantsTableAddAction();
                participantsDs.clear();
                allTagsTypesDs.refresh();
                allTagsDs.refresh();
            }
        });

        taskDs.addItemPropertyChangeListener(e -> {
            if ("name".equalsIgnoreCase(e.getProperty())) {
                String codeValue = e.getItem().getCode();
                if (StringUtils.isBlank(codeValue) && e.getItem().getProject() != null) {
                    String newName = String.valueOf(e.getValue());
                    String newCode = e.getItem().getProject().getCode() + "_" + newName.toUpperCase().replaceAll(" ", "_");
                    e.getItem().setCode(newCode);
                }
            }
        });

        if (!userSession.isEntityOpPermitted(metadata.getClassNN(Task.class), EntityOp.UPDATE)) {
            tabsheet.getTab("advanced").setVisible(false);
        }
    }

    @Override
    protected void initNewItem(Task item) {
        super.initNewItem(item);
        if (item.getStatus() == null) {
            item.setStatus(TaskStatus.ACTIVE);
        }
    }

    @Override
    protected void postInit() {
        super.postInit();
        Task task = getItem();
        projectField.setEnabled(task.getProject() == null);
        updateParticipantsTableAddAction();
    }

    protected void updateParticipantsTableAddAction() {
        participantsTableAdd.setWindowParams(ParamsMap.of("project", getItem().getProject(), "multiselect", true));
        participantsTableAdd.setEnabled(getItem().getProject() != null);
    }
}