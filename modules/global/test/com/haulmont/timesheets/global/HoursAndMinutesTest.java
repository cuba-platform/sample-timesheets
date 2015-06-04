/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import junit.framework.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * @author degtyarjov
 * @version $Id$
 */
public class HoursAndMinutesTest {
    @Test
    public void testConstructors() throws Exception {
        HoursAndMinutes hoursAndMinutes = new HoursAndMinutes("8:11");
        assertEquals(8, hoursAndMinutes.getHours());
        assertEquals(11, hoursAndMinutes.getMinutes());

        hoursAndMinutes = new HoursAndMinutes("99:99");
        assertEquals(100, hoursAndMinutes.getHours());
        assertEquals(39, hoursAndMinutes.getMinutes());

        hoursAndMinutes = new HoursAndMinutes(BigDecimal.valueOf(100.5));
        assertEquals(100, hoursAndMinutes.getHours());
        assertEquals(30, hoursAndMinutes.getMinutes());

        hoursAndMinutes = new HoursAndMinutes(time(18, 19));
        assertEquals(18, hoursAndMinutes.getHours());
        assertEquals(19, hoursAndMinutes.getMinutes());
    }

    private Date time(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    @Test
    public void testModifiers() throws Exception {
        HoursAndMinutes hoursAndMinutes = new HoursAndMinutes("8:11");
        hoursAndMinutes.add("9:59");
        assertEquals(18, hoursAndMinutes.getHours());
        assertEquals(10, hoursAndMinutes.getMinutes());

        hoursAndMinutes = new HoursAndMinutes("1:00");
        hoursAndMinutes.add(time(18, 19));

        assertEquals(19, hoursAndMinutes.getHours());
        assertEquals(19, hoursAndMinutes.getMinutes());

        hoursAndMinutes = new HoursAndMinutes(24, 0);
        hoursAndMinutes.add(new HoursAndMinutes(24, 60));

        assertEquals(49, hoursAndMinutes.getHours());
        assertEquals(0, hoursAndMinutes.getMinutes());
    }
}
