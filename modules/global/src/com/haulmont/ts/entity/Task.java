/*
 * Copyright (c) 2015 com.haulmont.ts.entity
 */
package com.haulmont.ts.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Column;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Set;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * @author gorelov
 */
@NamePattern("%s|name")
@Table(name = "TS_TASK")
@Entity(name = "ts$Task")
public class Task extends StandardEntity {
    private static final long serialVersionUID = 4693836896751773146L;

    @Column(name = "NAME", nullable = false, length = 100)
    protected String name;

    @Column(name = "CODE")
    protected Integer code;

    @Column(name = "DESCRIPTION")
    protected String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROJECT_ID")
    protected Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TYPE_ID")
    protected TaskType type;

    @Column(name = "STATUS", nullable = false)
    protected Integer status;

    @JoinTable(name = "TS_TASK_TAG_TYPE_LINK",
        joinColumns = @JoinColumn(name = "TASK_ID"),
        inverseJoinColumns = @JoinColumn(name = "TAG_TYPE_ID"))
    @ManyToMany
    protected Set<TagType> requaredTagTypes;

    @JoinTable(name = "TS_TASK_TAG_LINK",
        joinColumns = @JoinColumn(name = "TASK_ID"),
        inverseJoinColumns = @JoinColumn(name = "TAG_ID"))
    @ManyToMany
    protected Set<Tag> defaultTags;

    public void setRequaredTagTypes(Set<TagType> requaredTagTypes) {
        this.requaredTagTypes = requaredTagTypes;
    }

    public Set<TagType> getRequaredTagTypes() {
        return requaredTagTypes;
    }

    public void setDefaultTags(Set<Tag> defaultTags) {
        this.defaultTags = defaultTags;
    }

    public Set<Tag> getDefaultTags() {
        return defaultTags;
    }


    public void setStatus(TaskStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public TaskStatus getStatus() {
        return status == null ? null : TaskStatus.fromId(status);
    }


    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }


    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}