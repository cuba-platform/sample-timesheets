
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
import com.haulmont.timesheets.entity.Overtime;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author gorelov
 */
public class TimeEntryOvertimeAggregation implements AggregationStrategy<Overtime, BigDecimal> {
    @Override
    public BigDecimal aggregate(Collection<Overtime> propertyValues) {
        Map<Map<String, Object>, BigDecimal> map = new LinkedHashMap<>();
        for (Overtime propertyValue : propertyValues) {
            Map<String, Object> key = new TreeMap<>();
            key.put("user", propertyValue.getUser());
            key.put("date", propertyValue.getDate());
            map.put(key, propertyValue.getOvertimeInHours());
        }

        BigDecimal result = BigDecimal.ZERO;

        for (Map.Entry<Map<String, Object>, BigDecimal> entry : map.entrySet()) {
            result = result.add(entry.getValue());
        }

        return result;
    }
    @Override
    public Class<BigDecimal> getResultClass() {
        return BigDecimal.class;
    }
}
