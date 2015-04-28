/*
 * Copyright (c) 2015 com.haulmont.ts.entity
 */
package com.haulmont.ts.entity;

import javax.persistence.Entity;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.core.entity.annotation.Extends;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;

/**
 * @author gorelov
 */
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("Ext")
@NamePattern("%s %s|firstName,lastName")
@Entity(name = "ts$ExtUser")
@Extends(User.class)
public class ExtUser extends User {
    private static final long serialVersionUID = 4909893210504413352L;

    @Column(name = "RATE")
    protected Double rate;

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }



}