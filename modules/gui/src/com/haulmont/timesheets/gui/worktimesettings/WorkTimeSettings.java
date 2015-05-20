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
import java.util.ArrayList;
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
    protected GroupBoxLayout workDaysGroupBox;
    @Inject
    protected ComponentsFactory componentsFactory;

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
                double workHoursValue = workHoursTextField.getValue();
                workTimeConfigBean.setWorkHourForWeek(workHoursValue);
                workTimeConfigBean.setWorkDays(getWorkDays());
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
        List<DayOfWeek> workDays = workTimeConfigBean.getWorkDays();
        for (DayOfWeek day : DayOfWeek.values()) {
            HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.NAME);
            CheckBox checkBox = componentsFactory.createComponent(CheckBox.NAME);
            checkBox.setId(day.getId());
            checkBox.setValue(workDays.contains(day));
            hBoxLayout.add(checkBox);

            Label label = componentsFactory.createComponent(Label.NAME);
            label.setValue(messages.getMessage(DayOfWeek.class, DayOfWeek.getDayOfWeekLocalizationKey(day)));
            hBoxLayout.add(label);

            workDaysGroupBox.add(hBoxLayout);
        }
    }

    protected List<DayOfWeek> getWorkDays() {
        List<DayOfWeek> workDays = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            CheckBox checkBox = getComponent(day.getId());
            if (checkBox != null && (Boolean) checkBox.getValue()) {
                workDays.add(day);
            }
        }
        return workDays;
    }
}