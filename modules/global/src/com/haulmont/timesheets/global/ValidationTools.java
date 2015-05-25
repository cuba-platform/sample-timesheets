/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import org.apache.commons.math3.util.Precision;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

/**
 * @author gorelov
 * @version $Id$
 */
@ManagedBean(ValidationTools.NAME)
public class ValidationTools {

    public static final String NAME = "timesheets_ValidationTools";
    public static final int PRECISION = 2;

    @Inject
    protected WorkTimeConfigBean workTimeConfigBean;

    public boolean isWeekOvertime(double hours) {
        return Precision.compareTo(workTimeConfigBean.getWorkHourForWeek(), hours, PRECISION) < 0;
    }

    public boolean isWeekOvertime(String time) {
        return isWeekOvertime(DateTimeUtils.timeStringToDouble(time));
    }

    public boolean isDayOvertime(double hours) {
        return Precision.compareTo(workTimeConfigBean.getWorkHourForDay(), hours, PRECISION) < 0;
    }

    public boolean isDayOvertime(String time) {
        return isDayOvertime(DateTimeUtils.timeStringToDouble(time));
    }

    public boolean isNotMatchWithWeekPlan(double hours) {
        return Precision.compareTo(workTimeConfigBean.getWorkHourForWeek(), hours, PRECISION) != 0;
    }

    public boolean isNotMatchWithWeekPlan(String time) {
        return isNotMatchWithWeekPlan(DateTimeUtils.timeStringToDouble(time));
    }
}
