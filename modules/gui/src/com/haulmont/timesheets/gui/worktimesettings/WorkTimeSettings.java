/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui
 */
package com.haulmont.timesheets.gui.worktimesettings;

import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.timesheets.entity.DayOfWeek;
import com.haulmont.timesheets.global.WorkTimeConfigBean;

import javax.inject.Inject;
import java.util.*;

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

    @Override

    public void init(Map<String, Object> params) {
        getDialogParams().setWidthAuto();

        workHoursTextField.setValue(workTimeConfigBean.getWorkHourForWeek());

        initWorkDays();

        AbstractAction commitAction = new AbstractAction(Editor.WINDOW_COMMIT) {

            @Override
            public String getCaption() {
                return messages.getMainMessage("actions.Ok");
            }

            @Override
            public void actionPerform(Component component) {
                double workHoursValue = workHoursTextField.getValue();
                workTimeConfigBean.setWorkHourForWeek(workHoursValue);
                workTimeConfigBean.setWorkDays(workDaysOptions.<List<DayOfWeek>>getValue());
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
    }
}