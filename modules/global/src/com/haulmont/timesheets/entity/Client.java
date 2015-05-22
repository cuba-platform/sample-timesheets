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
@Table(name = "TS_CLIENT")
@Entity(name = "ts$Client")
public class Client extends StandardEntity {

    private static final long serialVersionUID = -7082258753232993605L;

    @Column(name = "NAME", nullable = false)
    protected String name;

    @Column(name = "CODE", nullable = false, unique = true, length = 50)
    protected String code;

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


}