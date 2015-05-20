/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.core;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;
import com.haulmont.cuba.core.config.defaults.DefaultDouble;

/**
 * @author gorelov
 * @version $Id$
 */
@Source(type = SourceType.DATABASE)
public interface WorkTimeConfig extends Config {

    @Property("timesheets.workHourForWeek")
    @DefaultDouble(40)
    double getWorkHourForWeek();

    @Property("timesheets.workDays")
    @Default("Mon|Tue|Wed|Thu|Fri")
    String getWorkDays();
}
