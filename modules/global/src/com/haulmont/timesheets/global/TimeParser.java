/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import com.haulmont.cuba.core.global.Messages;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gorelov
 * @version $Id$
 */
@ManagedBean
public class TimeParser {
    public static final String NAME = "timesheets_DateWorker";

    @Inject
    protected Messages messages;

    public Date parse(String time) {
        if (StringUtils.isBlank(time)) {
            return null;
        }

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateTimeUtils.TIME_FORMAT);
            return simpleDateFormat.parse(time);
        } catch (ParseException e) {
            //do nothing, let following code to parse it
        }

        Date result = DateTimeUtils.getDateWithoutTime(new Date());
        if (StringUtils.isNumeric(time)) {
            return DateUtils.addHours(result, Integer.parseInt(time));
        }

        result = DateUtils.addHours(result, findHours(time));
        result = DateUtils.addMinutes(result, findMinutes(time));

        return result;
    }

    private int findHours(String time) {
        return findTimeValue(time, messages.getMessage(DateTimeUtils.class, "timeHours"));
    }

    private int findMinutes(String time) {
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
