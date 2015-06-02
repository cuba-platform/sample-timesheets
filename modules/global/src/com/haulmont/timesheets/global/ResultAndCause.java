/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.timesheets.global;

import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;

@ThreadSafe
public class ResultAndCause implements Serializable {
    public final boolean isPositive;
    public final boolean isNegative;
    public final String cause;
    public static final ResultAndCause POSITIVE = new ResultAndCause(true, null);
    public static final ResultAndCause NEGATIVE = new ResultAndCause(false, null);


    public ResultAndCause(boolean isPositive, String cause) {
        this.isPositive = isPositive;
        this.isNegative = !isPositive;
        this.cause = cause;
    }

    public static ResultAndCause positive() {
        return POSITIVE;
    }

    public static ResultAndCause positive(String cause) {
        return new ResultAndCause(true, cause);
    }

    public static ResultAndCause negative() {
        return NEGATIVE;
    }

    public static ResultAndCause negative(String cause) {
        return new ResultAndCause(false, cause);
    }

    @Override
    public String toString() {
        return cause;
    }
}
