/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.security.global.UserSession;
import org.apache.commons.lang.time.DateUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author gorelov
 * @version $Id$
 */

public final class DateTimeUtils {
    public static final String TIME_FORMAT = "HH:mm";

    private DateTimeUtils() {
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
        return getFirstDayOfWeek(date, userSession().getLocale());
    }

    public static Date getFirstDayOfWeek(Date date, Locale locale) {
        Calendar calendar = getCalendarWithoutTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.getInstance(locale).getFirstDayOfWeek());
        return calendar.getTime();
    }

    public static Date getLastDayOfWeek(Date date) {
        return DateUtils.addDays(getFirstDayOfWeek(date), 6);
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

    public static int getCalendarDayOfWeek(Date date) {
        return DateUtils.toCalendar(date).get(Calendar.DAY_OF_WEEK);
    }

    public static BigDecimal timeStringToBigDecimal(String time) {
        if (time.contains(":")) {
            String[] parts = time.split(":");
            return BigDecimal.valueOf(Integer.parseInt(parts[0]) + Integer.parseInt(parts[1]) / 60.0);
        } else {
            TimeParser timeParser = AppBeans.get(TimeParser.NAME);
            return BigDecimal.valueOf(timeParser.findHours(time) + timeParser.findMinutes(time) / 60.0);
        }
    }

    public static DateFormat getDateFormat() {
        return new SimpleDateFormat(messages().getMainMessage("dateFormat"));
    }

    private static Messages messages() {
        return AppBeans.get(Messages.NAME, Messages.class);
    }

    private static UserSession userSession() {
        return AppBeans.get(UserSession.class);
    }
}
