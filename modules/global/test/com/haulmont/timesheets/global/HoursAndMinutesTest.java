
/*
 * Copyright (c) 2015 Haulmont
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
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.security.global.UserSession;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;

/**
 * @author degtyarjov
 */
public class HoursAndMinutesTest {
    @Mocked
    protected AppBeans appBeans;

    @Mocked
    protected TimeSource timeSource;

    @Mocked
    protected UserSession userSession;

    @Mocked
    protected UserSessionSource userSessionSource;

    protected TimeParser timeParser = new TimeParser();

    @Before
    public void setUp() throws Exception {
        timeParser.timeSource = new MockUp<TimeSource>() {
            @SuppressWarnings("UnusedDeclaration")
            @Mock
            Date currentTimestamp() {
                return new Date();
            }
        }.getMockInstance();

        new NonStrictExpectations() {
            {
                AppBeans.get(TimeParser.NAME); result = timeParser;
                AppBeans.get(UserSessionSource.class); result = userSessionSource;
                userSessionSource.getUserSession(); result = userSession;
                userSession.getLocale(); result = Locale.ENGLISH;
            }
        };
    }

    @Test
    public void testConstructors() throws Exception {
        HoursAndMinutes hoursAndMinutes = HoursAndMinutes.fromString("8:11");
        assertEquals(8, hoursAndMinutes.getHours());
        assertEquals(11, hoursAndMinutes.getMinutes());

        hoursAndMinutes = HoursAndMinutes.fromString("99:99");
        assertEquals(100, hoursAndMinutes.getHours());
        assertEquals(39, hoursAndMinutes.getMinutes());

        hoursAndMinutes = HoursAndMinutes.fromBigDecimal(BigDecimal.valueOf(100.5));
        assertEquals(100, hoursAndMinutes.getHours());
        assertEquals(30, hoursAndMinutes.getMinutes());

        hoursAndMinutes = HoursAndMinutes.fromDate(time(18, 19));
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
        HoursAndMinutes hoursAndMinutes = HoursAndMinutes.fromString("8:11");
        hoursAndMinutes.add("9:59");
        assertEquals(18, hoursAndMinutes.getHours());
        assertEquals(10, hoursAndMinutes.getMinutes());

        hoursAndMinutes = HoursAndMinutes.fromString("1:00");
        hoursAndMinutes.add(time(18, 19));

        assertEquals(19, hoursAndMinutes.getHours());
        assertEquals(19, hoursAndMinutes.getMinutes());

        hoursAndMinutes = new HoursAndMinutes(24, 0);
        hoursAndMinutes.add(new HoursAndMinutes(24, 60));

        assertEquals(49, hoursAndMinutes.getHours());
        assertEquals(0, hoursAndMinutes.getMinutes());
    }
}
