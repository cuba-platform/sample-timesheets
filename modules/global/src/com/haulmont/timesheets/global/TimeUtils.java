/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gorelov
 * @version $Id$
 */

public class TimeUtils {

    public static final String TIME_FORMAT = "hh:mm";

    public static Calendar getCalendarWithoutTime(Date date) {
        java.util.Calendar calendar = DateUtils.toCalendar(date);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar;
    }
    
    public static Date getDateWithoutTime(Date date) {
        return getCalendarWithoutTime(date).getTime();
    }

    public static Date getFirstDayOfWeek(Date date) {
        Calendar calendar = getCalendarWithoutTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTime();
    }

    public static Date getFirstDayOfMonth(Date date) {
        java.util.Calendar calendar = getCalendarWithoutTime(date);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Date getLastDayOfMonth(Date date) {
        java.util.Calendar calendar = getCalendarWithoutTime(date);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public static DateFormat getDateFormat() {
        return new SimpleDateFormat(messages().getMainMessage("dateFormat"));
    }

    private static Messages messages() {return AppBeans.get(Messages.NAME, Messages.class);}
}
