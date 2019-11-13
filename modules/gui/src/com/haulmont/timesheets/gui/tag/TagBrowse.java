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
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.timesheets.entity.Tag;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * @author gorelov
 */
@UiController("ts$Tag.browse")
@UiDescriptor("tag-browse.xml")
@LookupComponent("tagsTable")
@LoadDataBeforeShow
public class TagBrowse extends StandardLookup<Tag> {
    @Inject
    protected ScreenBuilders screenBuilders;

    @Inject
    protected Table<Tag> tagsTable;

    @Subscribe("tagsTable.create")
    protected void onTagsTableCreateActionPerformed(Action.ActionPerformedEvent e) {
        screenBuilders.editor(tagsTable)
                .newEntity()
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();
    }

    @Subscribe("tagsTable.edit")
    protected void onTagsTableEditActionPerformed(Action.ActionPerformedEvent e) {
        screenBuilders.editor(tagsTable)
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();
    }
}