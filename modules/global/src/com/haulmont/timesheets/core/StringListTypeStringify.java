
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
