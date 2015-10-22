
package com.haulmont.timesheets.global;

import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.TimeSource;
import org.apache.commons.lang.StringUtils;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gorelov
 */
@ManagedBean(TimeParser.NAME)
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
