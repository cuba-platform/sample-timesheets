/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author gorelov
 * @version $Id$
 */
public class DateTimeUtilsTest {

    @Test
    public void testTimeStringToDouble() {
        double val = DateTimeUtils.timeStringToDouble("2:30");
        Assert.assertEquals(2.5, val);
        val = DateTimeUtils.timeStringToDouble("2:15");
        Assert.assertEquals(2.25, val);
    }
}
