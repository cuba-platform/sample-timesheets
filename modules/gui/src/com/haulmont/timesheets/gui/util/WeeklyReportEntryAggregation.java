
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

package com.haulmont.timesheets.gui.util;

import com.haulmont.cuba.gui.data.aggregation.AggregationStrategy;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.global.StringFormatHelper;
import com.haulmont.timesheets.global.HoursAndMinutes;

import java.util.Collection;
import java.util.List;

/**
 * @author gorelov
 */
public class WeeklyReportEntryAggregation implements AggregationStrategy<List<TimeEntry>, String> {

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
