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

package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author gorelov
 */
public enum TimeEntryStatus implements EnumClass<String> {
    NEW("new"),
    APPROVED("approved"),
    REJECTED("rejected"),
    CLOSED("closed");

    private String id;

    TimeEntryStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    public static TimeEntryStatus fromId(String id) {
        for (TimeEntryStatus at : TimeEntryStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}