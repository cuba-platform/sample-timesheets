
/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
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
