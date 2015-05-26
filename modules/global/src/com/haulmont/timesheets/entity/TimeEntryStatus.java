/*
 * Copyright (c) 2015 com.haulmont.ts.entity
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