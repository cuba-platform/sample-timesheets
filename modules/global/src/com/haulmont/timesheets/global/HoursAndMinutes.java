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
import com.haulmont.timesheets.entity.TimeEntry;

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

    public static HoursAndMinutes fromTimeEntry(@Nullable TimeEntry timeEntry) {
        if (timeEntry == null) {
            return new HoursAndMinutes();
        }

        Integer minutes = timeEntry.getTimeInMinutes();
        return new HoursAndMinutes(0, minutes != null ? minutes : 0);
    }

    public static HoursAndMinutes fromString(@Nullable String timeStr) {
        if (timeStr == null) {
            return new HoursAndMinutes();
        }

        TimeParser timeParser = AppBeans.get(TimeParser.NAME);
        return timeParser.parseToHoursAndMinutes(timeStr);
    }

    public static HoursAndMinutes fromBigDecimal(@Nullable BigDecimal hours) {
        if (hours == null) {
            return new HoursAndMinutes();
        }

        HoursAndMinutes hoursAndMinutes = new HoursAndMinutes();
        hoursAndMinutes.addMinutes(hours.multiply(MINUTES_IN_HOUR).intValue());
        return hoursAndMinutes;
    }

    public static HoursAndMinutes fromDate(@Nullable Date time) {
        if (time == null) {
            return new HoursAndMinutes();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        return new HoursAndMinutes(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }


    public HoursAndMinutes() {
    }

    public HoursAndMinutes(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
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
        add(HoursAndMinutes.fromDate(time));
    }

    public void add(String time) {
        add(HoursAndMinutes.fromString(time));
    }

    public void addHours(int hours) {
        this.hours += hours;
        setupInvariants();
    }

    public void addMinutes(int minutes) {
        this.minutes += minutes;
        setupInvariants();
    }

    protected void setupInvariants() {
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

    public Integer toMinutes() {
        return hours * 60 + minutes;
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
    public String toString() {
        return String.format("%02d:%02d", hours, minutes);
    }

    public String getFormattedCaption() {
        return StringFormatHelper.getDayHoursString(this);
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
