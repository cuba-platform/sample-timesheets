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

package com.haulmont.timesheets.gui.activitytype;

import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.timesheets.entity.ActivityType;

import javax.inject.Inject;

/**
 * @author degtyarjov
 */
@UiController("ts$ActivityType.lookup")
@UiDescriptor("activitytype-browse.xml")
@LookupComponent("activityTypesTable")
@LoadDataBeforeShow
public class ActivityTypeBrowse extends StandardLookup<ActivityType> {
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected Table<ActivityType> activityTypesTable;

    @Subscribe("activityTypesTable.create")
    protected void onCreateActionPerformed(Action.ActionPerformedEvent event) {
        screenBuilders.editor(activityTypesTable)
                .newEntity()
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();
    }

    @Subscribe("activityTypesTable.edit")
    protected void onEditActionPerformed(Action.ActionPerformedEvent event) {
        screenBuilders.editor(activityTypesTable)
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();
    }
}