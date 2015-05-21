/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

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
    protected static Messages messages = AppBeans.get(Messages.NAME);
    public static final String TIME_FORMAT = "hh:mm";


    public static Date parse(String time) {
        if (StringUtils.isBlank(time)) {
            return null;
        }

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);
            return simpleDateFormat.parse(time);
        } catch (ParseException e) {
            //do nothing, let following code to parse it
        }

        Date result = getDateWithoutTime(new Date());
        if (StringUtils.isNumeric(time)) {
            return DateUtils.addHours(result, Integer.parseInt(time));
        }

        result = DateUtils.addHours(result, findHours(time));
        result = DateUtils.addMinutes(result, findMinutes(time));

        return result;
    }

    protected static int findHours(String time) {
        return findTimeValue(time, messages.getMessage(TimeUtils.class, "timeHours"));
    }

    protected static int findMinutes(String time) {
        return findTimeValue(time, messages.getMessage(TimeUtils.class, "timeMinutes"));
    }

    protected static int findTimeValue(String time, String units) {
        String regex = "\\d+\\s*(" + units + ")";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(time);
        if (matcher.find()) {
            String value = matcher.group();
            regex = "\\d+";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(value);
            if (matcher.find()) {
                return Integer.valueOf(matcher.group());
            }
        }
        return 0;
    }

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
}
