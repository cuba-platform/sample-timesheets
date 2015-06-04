/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import java.util.Set;

/**
 * @author gorelov
 */
@NamePattern("%s|name")
@Table(name = "TS_ACTIVITY_TYPE")
@Entity(name = "ts$ActivityType")
public class ActivityType extends StandardEntity {
    private static final long serialVersionUID = 1694745893454315167L;

    @Column(name = "NAME", nullable = false, length = 100)
    protected String name;

    @Column(name = "CODE", nullable = false, length = 50)
    protected String code;

    @Column(name = "DESCRIPTION")
    protected String description;

    @JoinTable(name = "TS_ACTIVITY_TYPE_PROJECT_LINK",
        joinColumns = @JoinColumn(name = "ACTIVITY_TYPE_ID"),
        inverseJoinColumns = @JoinColumn(name = "PROJECT_ID"))
    @ManyToMany
    protected Set<Project> projects;

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public Set<Project> getProjects() {
        return projects;
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