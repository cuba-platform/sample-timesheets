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

package com.haulmont.timesheets.gui.tag;

import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.timesheets.entity.Tag;
import com.haulmont.timesheets.entity.TagType;
import com.haulmont.timesheets.gui.util.ScreensHelper;

import javax.inject.Inject;

/**
 * @author gorelov
 */
@UiController("ts$Tag.edit")
@UiDescriptor("tag-edit.xml")
@EditedEntityContainer("tagDc")
@LoadDataBeforeShow
public class TagEdit extends StandardEditor<Tag> {
    @Inject
    protected ScreenBuilders screenBuilders;

    @Inject
    protected LookupPickerField<TagType> tagType;
    @Inject
    private InstanceContainer<Tag> tagDc;

    @Subscribe("tagType.lookup")
    protected void onTagTypeLookupActionPerformed(Action.ActionPerformedEvent e) {
        screenBuilders.lookup(tagType)
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();
    }

    @Subscribe
    protected void onInit(InitEvent event) {
        tagDc.addItemPropertyChangeListener(new ScreensHelper.EntityCodeGenerationListener<>());
    }
}