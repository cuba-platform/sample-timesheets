/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.entity;

import java.util.Set;

/**
 * @author degtyarjov
 * @version $Id$
 */
public interface TimeEntryBase {
    Task getTask();
    ActivityType getActivityType();
    Set<Tag> getTags();
}
