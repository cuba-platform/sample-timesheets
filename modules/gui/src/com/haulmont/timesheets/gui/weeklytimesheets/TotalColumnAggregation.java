
package com.haulmont.timesheets.gui.weeklytimesheets;

import com.haulmont.cuba.gui.data.aggregation.AggregationStrategy;
import com.haulmont.timesheets.global.HoursAndMinutes;
import com.haulmont.timesheets.global.StringFormatHelper;

import java.util.Collection;

/**
 * @author gorelov
 */
public class TotalColumnAggregation implements AggregationStrategy<String, String> {

    @Override
    public String aggregate(Collection<String> propertyValues) {
        HoursAndMinutes total = new HoursAndMinutes();
        for (String time : propertyValues) {
            total.add(HoursAndMinutes.fromString(time));
        }
        return StringFormatHelper.getWeekAggregationString(total);
    }

    @Override
    public Class<String> getResultClass() {
        return String.class;
    }
}
