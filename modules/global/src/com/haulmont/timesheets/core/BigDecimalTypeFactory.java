/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.core;

import com.haulmont.cuba.core.config.type.TypeFactory;

import java.math.BigDecimal;

/**
 * @author gorelov
 * @version $Id$
 */
public class BigDecimalTypeFactory extends TypeFactory {
    @Override
    public Object build(String string) {
        return new BigDecimal(string);
    }
}
