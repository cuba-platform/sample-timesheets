/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.exception;

/**
 * @author degtyarjov
 * @version $Id$
 */
public class ClosedPeriodException extends RuntimeException {
    public ClosedPeriodException() {
    }

    public ClosedPeriodException(String message) {
        super(message);
    }

    public ClosedPeriodException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClosedPeriodException(Throwable cause) {
        super(cause);
    }

    public ClosedPeriodException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
