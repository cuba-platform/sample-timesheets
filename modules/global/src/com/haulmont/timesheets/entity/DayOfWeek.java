/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author gorelov
 * @version $Id$
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
        return null;
    }

    public static DayOfWeek fromAbbreviation(String abb) {
        for (DayOfWeek at : DayOfWeek.values()) {
            if (at.getId().substring(0, 3).equals(abb.toLowerCase())) {
                return at;
            }
        }
        return null;
    }

    public static int getDayOffset(DayOfWeek day) {
        switch (day) {
            case TUESDAY:
                return 1;
            case WEDNESDAY:
                return 2;
            case THURSDAY:
                return 3;
            case FRIDAY:
                return 4;
            case SATURDAY:
                return 5;
            case SUNDAY:
                return 6;
            default:
                return 0;
        }
    }

    public static int convertToDayOfWeekNumber(DayOfWeek day, Locale locale) {
        // TODO: gg use calendar with locale?
        int firstDayOfWeek = Calendar.getInstance(locale).getFirstDayOfWeek();
        int offset = firstDayOfWeek - 1;

        switch (day) {
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

    protected static int getComputedNumber(int origin, int offset) {
        int value = origin - offset;
        return value > 0 ? value : 7 - value;
    }
}
