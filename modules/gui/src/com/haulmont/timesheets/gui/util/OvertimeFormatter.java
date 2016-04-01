
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

import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.cuba.gui.components.Formatter;
import com.haulmont.timesheets.entity.Overtime;

import java.math.BigDecimal;

/**
 * @author degtyarjov
 */
public class OvertimeFormatter implements Formatter<Overtime> {
    @Override
    public String format(Overtime value) {
        if (value.getOvertimeInHours() != null) {
            Datatype<BigDecimal> decimalDatatype = Datatypes.get(BigDecimal.class);
            if (decimalDatatype != null) {
                return decimalDatatype.format(value.getOvertimeInHours());
            } else {
                return value.getOvertimeInHours().toString();
            }
        } else {
            return null;
        }
    }
}
