/*
 * Copyright (c) 2015 com.haulmont.ts.entity
 */
package com.haulmont.ts.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author gorelov
 */
@NamePattern("%s|name")
@Table(name = "TS_TAG_TYPE")
@Entity(name = "ts$TagType")
public class TagType extends StandardEntity {
    private static final long serialVersionUID = 1694745893454315167L;

    @Column(name = "NAME", nullable = false, length = 100)
    protected String name;

    @Column(name = "CODE", nullable = false, length = 50)
    protected String code;

    @Column(name = "DESCRIPTION")
    protected String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected Project project;

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }


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