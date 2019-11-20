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

package com.haulmont.timesheets.gui.worktimesettings;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.timesheets.entity.DayOfWeek;
import com.haulmont.timesheets.global.WorkTimeConfigBean;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * @author gorelov
 */
@UiController("work-time-settings")
@UiDescriptor("work-time-settings.xml")
public class WorkTimeSettings extends Screen {

    @Inject
    protected WorkTimeConfigBean workTimeConfigBean;

    @Inject
    protected OptionsGroup workDaysOptions;
    @Inject
    protected DateField<Date> openPeriodStart;
    @Inject
    protected TextField<BigDecimal> workHoursTextField;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        workHoursTextField.setValue(workTimeConfigBean.getWorkHourForWeek());
        initWorkDays();
    }

    @Subscribe("confirm")
    protected void onConfirm(Action.ActionPerformedEvent event) {
        BigDecimal workHoursValue = workHoursTextField.getValue();
        workTimeConfigBean.setWorkHourForWeek(workHoursValue);
        workTimeConfigBean.setWorkDays(new ArrayList<DayOfWeek>((Collection) workDaysOptions.getValue()));
        workTimeConfigBean.setOpenPeriodStart(openPeriodStart.getValue());
        close(new StandardCloseAction(Window.COMMIT_ACTION_ID));
    }

    @Subscribe("cancel")
    protected void onCancel(Action.ActionPerformedEvent event) {
        close(new StandardCloseAction(Window.CLOSE_ACTION_ID));
    }

    protected void initWorkDays() {
        workDaysOptions.setOptionsList(Arrays.asList(DayOfWeek.values()));
        workDaysOptions.setValue(workTimeConfigBean.getWorkDays());
        openPeriodStart.setValue(workTimeConfigBean.getOpenPeriodStart());
    }
}