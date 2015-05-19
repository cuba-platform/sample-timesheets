/*
 * Copyright (c) 2015 com.haulmont.ts.entity
 */
package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author gorelov
 */
public enum ProjectStatus implements EnumClass<String> {

    OPEN("open"),
    CLOSED("closed");

    private String id;

    ProjectStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    public static ProjectStatus fromId(String id) {
        for (ProjectStatus at : ProjectStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

    ProjectStatus inverted() {
        return ProjectStatus.OPEN.equals(this) ? ProjectStatus.CLOSED : ProjectStatus.OPEN;
    }
}