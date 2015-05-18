/*
 * Copyright (c) 2015 com.haulmont.ts.entity
 */
package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author gorelov
 */
public enum TimeEntryStatus implements EnumClass<Integer> {

    NEW(10),
    APPROVED(20),
    REJECTED(30);

    private Integer id;

    TimeEntryStatus(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static TimeEntryStatus fromId(Integer id) {
        for (TimeEntryStatus at : TimeEntryStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}