
package com.haulmont.timesheets.core;

import com.haulmont.cuba.core.config.type.TypeStringify;

import java.util.List;

/**
 * @author gorelov
 */
public class StringListTypeStringify extends TypeStringify {
    @Override
    public String stringify(Object value) {
        @SuppressWarnings("unchecked")
        List<String> workDays = (List<String>) value;
        if (workDays.isEmpty()) {
            return "";
        }
        int i = 0;
        int iMax = workDays.size() - 1;
        StringBuilder sb = new StringBuilder(workDays.size() * workDays.get(0).length());
        for (String day : workDays) {
            sb.append(day);
            if (i++ < iMax) {
                sb.append("|");
            }
        }
        return sb.toString();
    }
}
