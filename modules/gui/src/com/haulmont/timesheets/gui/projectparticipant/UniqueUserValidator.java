/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui.projectparticipant;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.Field;
import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.security.entity.User;

import java.util.Collection;

/**
 * @author gorelov
 * @version $Id$
 */
public class UniqueUserValidator implements Field.Validator {

    protected Collection<User> assignedUsers;

    @Override
    public void validate(Object value) throws ValidationException {
        if (assignedUsers == null)
            return;
        User validateUser = (User) value;
        for (User user : assignedUsers) {
            if (user.equals(validateUser)) {
                Messages messages = AppBeans.get(Messages.NAME);
                throw new ValidationException(messages.getMessage(getClass(), "validationFailed"));
            }
        }
    }

    public void setAssignedUsers(Collection<User> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }
}
