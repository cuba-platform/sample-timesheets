/*
 * Copyright (c) 2015 com.haulmont.ts.gui.entities
 */
package com.haulmont.timesheets.gui.extuser;

import com.haulmont.cuba.gui.app.security.user.edit.UserEditor;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.ExtUser;
import com.haulmont.timesheets.global.WorkTimeConfigBean;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author gorelov
 */
public class ExtUserEdit extends UserEditor {

    @Inject
    protected WorkTimeConfigBean workTimeConfigBean;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setWidthAuto();
    }

    @Override
    protected void initNewItem(User item) {
        super.initNewItem(item);
        ExtUser user = (ExtUser) item;
        if (user.getWorkHoursForWeek() == null) {
            user.setWorkHoursForWeek(workTimeConfigBean.getWorkHourForWeek());
        }
    }
}