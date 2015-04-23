/*
 * Copyright (c) 2015 com.haulmont.ts.entity
 */
package com.haulmont.ts.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

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

    @Column(name = "CODE")
    protected Integer code;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }


}