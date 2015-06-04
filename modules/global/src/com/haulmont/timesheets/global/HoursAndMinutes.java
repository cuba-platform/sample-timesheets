/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

/**
 * @author gorelov
 * @version $Id$
 */

import com.haulmont.cuba.core.global.AppBeans;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static java.math.BigDecimal.valueOf;

/**
 * Helpful {@link java.math.BigDecimal BigDecimal} wrapper that allow to work with individual time fields (hours and minutes)
 */
@SuppressWarnings("unused")
public class HoursAndMinutes {
    public static final BigDecimal MINUTES_IN_HOUR = valueOf(60);
    protected int hours;
    protected int minutes;

    public HoursAndMinutes() {
    }

    public HoursAndMinutes(@Nullable String timeStr) {
        if (timeStr == null) return;
        if (timeStr.contains(":")) {
            String[] parts = timeStr.split(":");
            hours = Integer.parseInt(parts[0]);
            minutes = Integer.parseInt(parts[1]);
        } else {
            TimeParser timeParser = AppBeans.get(TimeParser.NAME);
            hours = timeParser.findHours(timeStr);
            minutes = timeParser.findMinutes(timeStr);
        }
        setupInvariants();
    }

    public HoursAndMinutes(@Nullable BigDecimal hours) {
        if (hours == null) return;
        minutes = hours.multiply(valueOf(60)).intValue();
        setupInvariants();
    }

    public HoursAndMinutes(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
        setupInvariants();
    }

    public HoursAndMinutes(@Nullable Date time) {
        if (time == null) return;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        this.hours = calendar.get(Calendar.HOUR_OF_DAY);
        this.minutes = calendar.get(Calendar.MINUTE);
        setupInvariants();
    }

    public void setTime(HoursAndMinutes augend) {
        this.hours = augend.hours;
        this.minutes = augend.minutes;
        setupInvariants();
    }

    public void add(HoursAndMinutes augend) {
        this.hours += augend.hours;
        this.minutes += augend.minutes;
        setupInvariants();
    }

    public void add(Date time) {
        add(new HoursAndMinutes(time));
    }

    public void add(String time) {
        add(new HoursAndMinutes(time));
    }

    public void addHours(int hours) {
        this.hours += hours;
        setupInvariants();
    }

    public void addMinutes(int minutes) {
        this.minutes += minutes;
        setupInvariants();
    }

    protected void setupInvariants(){
        if (minutes >= 60) {
            int hours = minutes / 60;
            minutes = minutes % 60;
            this.hours += hours;
        }
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public BigDecimal toBigDecimal() {
        BigDecimal minutes = valueOf(this.minutes);
        BigDecimal hours = valueOf(this.hours);
        return hours.add(minutes.divide(MINUTES_IN_HOUR, BigDecimal.ROUND_HALF_EVEN));
    }

    public Date toDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HoursAndMinutes)) return false;

        HoursAndMinutes that = (HoursAndMinutes) o;
        return hours == that.hours && minutes == that.minutes;
    }

    @Override
    public int hashCode() {
        int result = hours;
        result = 31 * result + minutes;
        return result;
    }
}
