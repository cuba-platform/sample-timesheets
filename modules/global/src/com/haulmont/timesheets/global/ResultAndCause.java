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
