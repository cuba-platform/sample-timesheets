/*
 * Copyright (c) 2015 com.haulmont.ts.entity
 */
package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.util.Set;

/**
 * @author gorelov
 */
@NamePattern("[%s] %s|project,name")
@Table(name = "TS_TASK")
@Entity(name = "ts$Task")
public class Task extends StandardEntity {

    private static final long serialVersionUID = 4693836896751773146L;

    @Column(name = "NAME", nullable = false, length = 100)
    protected String name;

    @Column(name = "CODE", nullable = false, unique = true, length = 50)
    protected String code;

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
    protected Set<TagType> requiredTagTypes;

    @JoinTable(name = "TS_TASK_TAG_LINK",
            joinColumns = @JoinColumn(name = "TASK_ID"),
            inverseJoinColumns = @JoinColumn(name = "TAG_ID"))
    @ManyToMany
    protected Set<Tag> defaultTags;

    @JoinTable(name = "TS_TASK_PROJECT_PARTICIPANT_LINK",
            joinColumns = @JoinColumn(name = "TASK_ID"),
            inverseJoinColumns = @JoinColumn(name = "PROJECT_PARTICIPANT_ID"))
    @ManyToMany
    protected Set<ProjectParticipant> participants;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "task")
    protected Set<TimeEntry> timeEntries;

    public void setRequiredTagTypes(Set<TagType> requiredTagTypes) {
        this.requiredTagTypes = requiredTagTypes;
    }

    public Set<TagType> getRequiredTagTypes() {
        return requiredTagTypes;
    }


    public void setTimeEntries(Set<TimeEntry> timeEntries) {
        this.timeEntries = timeEntries;
    }

    public Set<TimeEntry> getTimeEntries() {
        return timeEntries;
    }


    public void setParticipants(Set<ProjectParticipant> participants) {
        this.participants = participants;
    }

    public Set<ProjectParticipant> getParticipants() {
        return participants;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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