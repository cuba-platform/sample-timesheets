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

package com.haulmont.timesheets.gui.extuser;

import com.haulmont.cuba.gui.app.security.user.edit.UserEditor;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.ExtUser;
import com.haulmont.timesheets.global.WorkTimeConfigBean;

import javax.inject.Inject;

/**
 * @author gorelov
 */
public class ExtUserEdit extends UserEditor {

    @Inject
    protected WorkTimeConfigBean workTimeConfigBean;

    @Override
    protected void initNewItem(User item) {
        super.initNewItem(item);
        ExtUser user = (ExtUser) item;
        if (user.getWorkHoursForWeek() == null) {
            user.setWorkHoursForWeek(workTimeConfigBean.getWorkHourForWeek());
        }
    }
}