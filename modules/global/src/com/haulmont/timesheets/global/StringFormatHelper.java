package com.haulmont.timesheets.global;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;

/**
 * @author gorelov
 * @version $Id$
 */
public class StringFormatHelper {

    protected static Messages messages = AppBeans.get(Messages.NAME);

    public static String timeToString(String format, HoursAndMinutes time) {
        return String.format(format, time.getHours(), time.getMinutes());
    }

    public static String getDayHoursString(HoursAndMinutes time) {
        return timeToString(messages.getMessage(StringFormatHelper.class, "format.dayHoursSummary"), time);
    }

    public static String getTotalDayAggregationString(HoursAndMinutes time) {
        return timeToString(messages.getMessage(StringFormatHelper.class, "format.totalDayHoursSummary"), time);
    }

    public static String getTaskAggregationString(HoursAndMinutes time) {
        return timeToString(messages.getMessage(StringFormatHelper.class, "format.taskHoursSummary"), time);
    }

    public static String getWeekAggregationString(HoursAndMinutes time) {
        return timeToString(messages.getMessage(StringFormatHelper.class, "format.weekHoursSummary"), time);
    }
}
