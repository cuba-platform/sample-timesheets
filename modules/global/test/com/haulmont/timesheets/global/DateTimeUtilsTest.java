/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import junit.framework.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author gorelov
 * @version $Id$
 */
public class DateTimeUtilsTest {

    @Test
    public void testTimeStringToDouble() {
        BigDecimal val = DateTimeUtils.timeStringToBigDecimal("2:30");
        Assert.assertEquals(BigDecimal.valueOf(2.5), val);
        val = DateTimeUtils.timeStringToBigDecimal("2:15");
        Assert.assertEquals(BigDecimal.valueOf(2.25), val);
    }
}
