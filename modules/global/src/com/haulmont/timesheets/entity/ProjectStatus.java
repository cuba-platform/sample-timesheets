/*
 * Copyright (c) 2015 com.haulmont.ts.entity
 */
package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author gorelov
 */
public enum ProjectStatus implements EnumClass<Integer>{

    OPEN(10),
    CLOSED(20);

    private Integer id;

    ProjectStatus (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static ProjectStatus fromId(Integer id) {
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