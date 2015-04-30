/*
 * Copyright (c) 2015 com.haulmont.ts.gui.timeentry
 */
package com.haulmont.timesheets.gui.timeentry;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.entity.TimeEntryStatus;
import com.haulmont.timesheets.gui.ComponentsHelper;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * @author gorelov
 */
public class TimeEntryEdit extends AbstractEditor<TimeEntry> {

    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected UserSession userSession;

    @Named("fieldGroup.task")
    protected LookupPickerField taskField;

    @Override
    public void init(Map<String, Object> params) {
        taskField.addAction(ComponentsHelper.createLookupAction(taskField));
        taskField.addClearAction();
        fieldGroup.addCustomField("description", ComponentsHelper.getCustomTextArea());
    }

    @Override
    protected void postInit() {
        super.postInit();
        TimeEntry timeEntry = getItem();
        if (timeEntry.getStatus() == null) {
            timeEntry.setStatus(TimeEntryStatus.NEW);
        }
        if (timeEntry.getUser() == null) {
            timeEntry.setUser(userSession.getUser());
        }
    }
}