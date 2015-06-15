
package com.haulmont.timesheets.gui.weeklytimesheets;

import com.haulmont.cuba.gui.data.aggregation.AggregationStrategy;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.global.StringFormatHelper;
import com.haulmont.timesheets.global.HoursAndMinutes;

import java.util.Collection;
import java.util.List;

/**
 * @author gorelov
 */
public class TimeEntryAggregation implements AggregationStrategy<List<TimeEntry>, String> {

    @Override
    public String aggregate(Collection<List<TimeEntry>> propertyValues) {
        HoursAndMinutes total = new HoursAndMinutes();
        for (List<TimeEntry> list : propertyValues) {
            for (TimeEntry timeEntry : list) {
                total.add(HoursAndMinutes.fromTimeEntry(timeEntry));
            }
        }
        return StringFormatHelper.getTotalDayAggregationString(total);
    }

    @Override
    public Class<String> getResultClass() {
        return String.class;
    }
}
