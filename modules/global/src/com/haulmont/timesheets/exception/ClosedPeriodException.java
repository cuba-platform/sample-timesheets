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
