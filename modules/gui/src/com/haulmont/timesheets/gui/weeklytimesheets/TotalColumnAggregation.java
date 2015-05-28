/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui.weeklytimesheets;

import com.haulmont.cuba.gui.data.aggregation.AggregationStrategy;
import com.haulmont.timesheets.global.AggregationHelper;
import com.haulmont.timesheets.global.DateTimeUtils;
import com.haulmont.timesheets.global.HoursAndMinutes;

import java.util.Collection;

/**
 * @author gorelov
 * @version $Id$
 */
public class TotalColumnAggregation implements AggregationStrategy<String, String> {

    @Override
    public String aggregate(Collection<String> propertyValues) {
        HoursAndMinutes total = new HoursAndMinutes();
        for (String time : propertyValues) {
            total.addTime(DateTimeUtils.timeStringToBigDecimal(time));
        }
        return AggregationHelper.getWeekAggregationString(total);
    }

    @Override
    public Class<String> getResultClass() {
        return String.class;
    }
}
