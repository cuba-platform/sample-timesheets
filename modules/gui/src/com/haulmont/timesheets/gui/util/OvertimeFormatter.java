
/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
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
