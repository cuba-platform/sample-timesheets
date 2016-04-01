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

import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.timesheets.entity.DayOfWeek;
import com.haulmont.timesheets.global.WorkTimeConfigBean;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author gorelov
 */
public class WorkTimeSettings extends AbstractWindow {

    @Inject
    protected Configuration configuration;
    @Inject
    protected WorkTimeConfigBean workTimeConfigBean;
    @Inject
    protected TextField workHoursTextField;
    @Inject
    protected OptionsGroup workDaysOptions;
    @Inject
    private DateField openPeriodStart;

    @Override
    public void init(Map<String, Object> params) {
        workHoursTextField.setValue(workTimeConfigBean.getWorkHourForWeek());

        initWorkDays();

        AbstractAction commitAction = new AbstractAction(Editor.WINDOW_COMMIT) {

            @Override
            public String getCaption() {
                return messages.getMainMessage("actions.Ok");
            }

            @Override
            public void actionPerform(Component component) {
                BigDecimal workHoursValue = workHoursTextField.getValue();
                workTimeConfigBean.setWorkHourForWeek(workHoursValue);
                workTimeConfigBean.setWorkDays(workDaysOptions.<List<DayOfWeek>>getValue());
                workTimeConfigBean.setOpenPeriodStart(openPeriodStart.getValue());
                close(getId());
            }
        };
        commitAction.setShortcut(configuration.getConfig(ClientConfig.class).getCommitShortcut());
        addAction(commitAction);

        addAction(new AbstractAction(Editor.WINDOW_CLOSE) {
                      @Override
                      public String getCaption() {
                          return messages.getMainMessage("actions.Cancel");
                      }

                      @Override
                      public void actionPerform(Component component) {
                          close(getId());
                      }
                  }
        );
    }

    protected void initWorkDays() {
        workDaysOptions.setOptionsList(Arrays.asList(DayOfWeek.values()));
        workDaysOptions.setValue(workTimeConfigBean.getWorkDays());
        openPeriodStart.setValue(workTimeConfigBean.getOpenPeriodStart());
    }
}