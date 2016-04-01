
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

package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author gorelov
 */
public enum DayOfWeek implements EnumClass<String> {

    MONDAY("monday"),
    TUESDAY("tuesday"),
    WEDNESDAY("wednesday"),
    THURSDAY("thursday"),
    FRIDAY("friday"),
    SATURDAY("saturday"),
    SUNDAY("sunday");

    private String id;

    DayOfWeek(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    public static DayOfWeek fromId(String id) {
        for (DayOfWeek at : DayOfWeek.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

    @Nonnull
    public static DayOfWeek fromCalendarDay(int calendarDay) {
        switch (calendarDay) {
            case Calendar.MONDAY:
                return DayOfWeek.MONDAY;
            case Calendar.TUESDAY:
                return DayOfWeek.TUESDAY;
            case Calendar.WEDNESDAY:
                return DayOfWeek.WEDNESDAY;
            case Calendar.THURSDAY:
                return DayOfWeek.THURSDAY;
            case Calendar.FRIDAY:
                return DayOfWeek.FRIDAY;
            case Calendar.SATURDAY:
                return DayOfWeek.SATURDAY;
            case Calendar.SUNDAY:
                return DayOfWeek.SUNDAY;
        }
        throw new IllegalArgumentException("Wrong java.util.Calendar number of day: " + calendarDay);
    }

    public static DayOfWeek fromAbbreviation(String abb) {
        for (DayOfWeek at : DayOfWeek.values()) {
            if (at.getId().substring(0, 3).equals(abb.toLowerCase())) {
                return at;
            }
        }
        return null;
    }

    public int convertToDayOfWeekNumber(Locale locale) {
        int firstDayOfWeek = Calendar.getInstance(locale).getFirstDayOfWeek();
        int offset = firstDayOfWeek - 1;

        switch (this) {
            case SUNDAY:
                return getComputedNumber(1, offset);
            case MONDAY:
                return getComputedNumber(2, offset);
            case TUESDAY:
                return getComputedNumber(3, offset);
            case WEDNESDAY:
                return getComputedNumber(4, offset);
            case THURSDAY:
                return getComputedNumber(5, offset);
            case FRIDAY:
                return getComputedNumber(6, offset);
            case SATURDAY:
                return getComputedNumber(7, offset);
            default:
                return 0;
        }
    }

    public int getJavaCalendarDay() {
        switch (this) {
            case SUNDAY:
                return Calendar.SUNDAY;
            case MONDAY:
                return Calendar.MONDAY;
            case TUESDAY:
                return Calendar.TUESDAY;
            case WEDNESDAY:
                return Calendar.WEDNESDAY;
            case THURSDAY:
                return Calendar.THURSDAY;
            case FRIDAY:
                return Calendar.FRIDAY;
            case SATURDAY:
                return Calendar.SATURDAY;
            default:
                return 0;
        }
    }

    protected int getComputedNumber(int origin, int offset) {
        int value = origin - offset;
        return value > 0 ? value : 7 - value;
    }
}
