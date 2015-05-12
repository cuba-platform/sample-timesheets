/*
 * Copyright (c) 2015 com.haulmont.ts.gui.entities
 */
package com.haulmont.timesheets.gui.extuser;

import com.haulmont.cuba.gui.app.security.user.edit.UserEditor;
import com.haulmont.timesheets.entity.ExtUser;
import com.haulmont.timesheets.global.WorkConfigBean;

import javax.inject.Inject;

/**
 * @author gorelov
 */
public class ExtUserEdit extends UserEditor {

    @Inject
    protected WorkConfigBean workConfigBean;

    @Override
    protected void postInit() {
        ExtUser user = (ExtUser) getItem();
        if (user.getWorkHoursForWeek() == null) {
            user.setWorkHoursForWeek(workConfigBean.getWorkHourForWeek());
        }
    }
}