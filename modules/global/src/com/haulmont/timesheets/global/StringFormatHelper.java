/*
 * Copyright (c) 2016 Haulmont
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.timesheets.global;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;

/**
 * @author gorelov
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
