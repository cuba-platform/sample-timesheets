/*
 * Copyright (c) 2015 com.haulmont.ts.entity
 */
package com.haulmont.ts.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author gorelov
 */
public enum TaskStatus implements EnumClass<Integer>{

    OPEN(10),
    IN_PROGRESS(20),
    COMPLETE(30),
    CLOSE(40);

    private Integer id;

    TaskStatus (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static TaskStatus fromId(Integer id) {
        for (TaskStatus at : TaskStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}