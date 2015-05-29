/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui
 */
package com.haulmont.timesheets.gui.rejection;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.TextArea;

import javax.inject.Inject;

/**
 * @author gorelov
 */
public class RejectionReason extends AbstractWindow {

    public static final String CONFIRM_ACTION_AD = "confirm";
    public static final String CANCEL_ACTION_AD = "cancel";

    @Inject
    protected TextArea rejectionReasonText;

    public void confirm() {
        close(CONFIRM_ACTION_AD);
    }

    public void cancel() {
        rejectionReasonText.setValue(null);
        close(CANCEL_ACTION_AD);
    }

    public String getRejectionReason() {
        return rejectionReasonText.getValue();
    }
}