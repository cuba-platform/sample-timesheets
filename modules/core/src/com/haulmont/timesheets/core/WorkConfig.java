/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.core;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.defaults.Default;
import com.haulmont.cuba.core.config.defaults.DefaultFloat;

/**
 * @author gorelov
 * @version $Id$
 */
public interface WorkConfig extends Config {

    @Property("timesheets.workHourForWeek")
    @DefaultFloat(40)
    float getWorkHourForWeek();

    @Property("timesheets.workDays")
    @Default("Mon|Tue|Wed|Thu|Fri")
    String getWorkDays();
}
