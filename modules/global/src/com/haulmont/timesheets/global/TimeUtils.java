/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author gorelov
 * @version $Id$
 */
public class TimeUtils {

    public static Date parse(String time) {
        if (StringUtils.isBlank(time)) {
            return null;
        }

        Date result = getZeroTime();
        if (StringUtils.isNumeric(time)) {
            return DateUtils.addHours(result, Integer.parseInt(time));
        }

        result = DateUtils.addHours(result, findHours(time));
        result = DateUtils.addMinutes(result, findMinutes(time));

        return result;
    }

    protected static int findHours(String time) {
        return 0;
    }

    protected static int findMinutes(String time) {
        return 0;
    }

    public static Date getZeroTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();

    }
}
