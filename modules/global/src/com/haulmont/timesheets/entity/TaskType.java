/*
 * Copyright (c) 2015 com.haulmont.ts.entity
 */
package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author gorelov
 */
@NamePattern("%s|name")
@Table(name = "TS_TASK_TYPE")
@Entity(name = "ts$TaskType")
public class TaskType extends StandardEntity {

    private static final long serialVersionUID = 2147635406394713896L;

    @Column(name = "NAME", nullable = false, length = 100)
    protected String name;

    @Column(name = "CODE", nullable = false, unique = true, length = 50)
    protected String code;

    @Column(name = "DESCRIPTION")
    protected String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}