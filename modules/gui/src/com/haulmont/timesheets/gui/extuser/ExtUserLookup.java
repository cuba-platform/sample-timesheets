/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui.extuser;

import com.haulmont.cuba.gui.app.security.user.browse.UserLookup;

import java.util.Map;

/**
 * @author gorelov
 * @version $Id$
 */
public class ExtUserLookup extends UserLookup {

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        getDialogParams().setWidth(800);
        getDialogParams().setHeight(500);
        getDialogParams().setResizable(true);
    }
}
