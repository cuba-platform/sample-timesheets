/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.core;

import com.haulmont.timesheets.entity.Holiday;

import java.util.Date;
import java.util.Set;

/**
 * @author degtyarjov
 * @version $Id$
 */
public interface HolidaysCacheAPI {
    Set<Holiday> getHolidays(Date start, Date end);
    String updateCache();
}
