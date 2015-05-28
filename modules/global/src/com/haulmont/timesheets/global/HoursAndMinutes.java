/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import java.math.BigDecimal;

/**
 * @author gorelov
 * @version $Id$
 */
public class HoursAndMinutes {

    protected BigDecimal time = BigDecimal.ZERO;

    public BigDecimal getTime() {
        return time;
    }

    public void setTime(BigDecimal time) {
        this.time = time;
    }

    public void addTime(BigDecimal augend) {
        time = time.add(augend);
    }

    public void addHours(int hours) {
        addTime(BigDecimal.valueOf(hours));
    }

    public void addMinutes(int minutes) {
        addTime(BigDecimal.valueOf(minutes / 60.0));
    }

    public int getHours() {
        return time.intValue();
    }

    public int getMinutes() {
        return time.remainder(BigDecimal.ONE)
                .multiply(BigDecimal.valueOf(60))
                .intValue();
    }
}
