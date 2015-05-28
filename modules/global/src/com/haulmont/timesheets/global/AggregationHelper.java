package com.haulmont.timesheets.global;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;

/**
 * @author gorelov
 * @version $Id$
 */
public class AggregationHelper {

    protected static Messages messages = AppBeans.get(Messages.NAME);

    public static String timeToString(String format, HoursAndMinutes time) {
        return String.format(format, time.getHours(), time.getMinutes());
    }

    public static String getDayAggregationString(HoursAndMinutes time) {
        return timeToString(messages.getMessage(AggregationHelper.class, "aggregation.dayHoursSummary"), time);
    }

    public static String getTotalDayAggregationString(HoursAndMinutes time) {
        return timeToString(messages.getMessage(AggregationHelper.class, "aggregation.totalDayHoursSummary"), time);
    }

    public static String getTaskAggregationString(HoursAndMinutes time) {
        return timeToString(messages.getMessage(AggregationHelper.class, "aggregation.taskHoursSummary"), time);
    }

    public static String getWeekAggregationString(HoursAndMinutes time) {
        return timeToString(messages.getMessage(AggregationHelper.class, "aggregation.weekHoursSummary"), time);
    }
}
