/*
 * Copyright (c) 2015 com.haulmont.ts.entity
 */
package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author gorelov
 */
public enum TaskStatus implements EnumClass<String> {

    ACTIVE("active"),
    INACTIVE("inactive");

    private String id;

    TaskStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    public static TaskStatus fromId(String id) {
        for (TaskStatus at : TaskStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

    public TaskStatus inverted() {
        return TaskStatus.ACTIVE.equals(this) ? TaskStatus.INACTIVE : TaskStatus.ACTIVE;
    }
}