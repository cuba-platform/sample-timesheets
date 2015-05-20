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

    public static String getDayOfWeekLocalizationKey(DayOfWeek day) {
        return day.getClass().getSimpleName() + "." + day.getId().toUpperCase();
    }

    public static int convertToDayOfWeekNumber(DayOfWeek day, Locale locale) {
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

    protected static int getComputedNumber(int origin, int offset) {
        int value = origin - offset;
        return value > 0 ? value : 7 - value;
    }
}
