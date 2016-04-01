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

package com.haulmont.timesheets.gui.tasktype;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.timesheets.entity.TaskType;
import com.haulmont.timesheets.gui.util.ComponentsHelper;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author gorelov
 */
public class TaskTypeEdit extends AbstractEditor<TaskType> {

    @Inject
    protected FieldGroup fieldGroup;

    @Inject
    private Datasource<TaskType> taskTypeDs;

    @Override
    public void init(Map<String, Object> params) {
        fieldGroup.addCustomField("description", ComponentsHelper.getCustomTextArea());
        taskTypeDs.addItemPropertyChangeListener(new ComponentsHelper.EntityCodeGenerationListener<>());
    }
}