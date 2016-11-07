
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

import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.TimeSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gorelov
 */
@Component(TimeParser.NAME)
public class TimeParser {

    public static final String NAME = "ts_TimeParser";

    @Inject
    protected Messages messages;

    @Inject
    protected TimeSource timeSource;

    public HoursAndMinutes parseToHoursAndMinutes(String time) {
        HoursAndMinutes result = new HoursAndMinutes();
        try {
            if (StringUtils.isBlank(time)) {
                return new HoursAndMinutes();
            }

            if (time.contains(":")) {
                String[] parts = time.split(":");
                result.addHours(Integer.parseInt(parts[0]));
                result.addMinutes(Integer.parseInt(parts[1]));
                return result;
            }

            if (time.matches("[0-9]{1,2} +[0-9]{1,2}")) {
                String[] parts = time.split(" ");
                result.addHours(Integer.parseInt(parts[0]));
                result.addMinutes(Integer.parseInt(parts[1]));
                return result;
            }

            if (StringUtils.isNumeric(time)) {
                result.addHours(Integer.parseInt(time));
                return result;
            }

            result.addHours(findHours(time));
            result.addMinutes(findMinutes(time));

            return result;
        } catch (NumberFormatException e) {
            return result;
        }
    }

    public int findHours(String time) {
        return findTimeValue(time, messages.getMessage(DateTimeUtils.class, "timeHours"));
    }

    public int findMinutes(String time) {
        return findTimeValue(time, messages.getMessage(DateTimeUtils.class, "timeMinutes"));
    }

    private int findTimeValue(String time, String units) {
        String regex = "\\d+\\s*(" + units + ")";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(time);
        if (matcher.find()) {
            String value = matcher.group();
            regex = "\\d+";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(value);
            if (matcher.find()) {
                return Integer.valueOf(matcher.group());
            }
        }
        return 0;
    }
}
