/*
 * Copyright (c) 2015 com.haulmont.ts.entity
 */
package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.global.HoursAndMinutes;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * @author gorelov
 */
@Listeners("ts_TimeEntryListener")
@NamePattern("#getCaption|timeInMinutes")
@Table(name = "TS_TIME_ENTRY")
@Entity(name = "ts$TimeEntry")
public class TimeEntry extends StandardEntity {

    private static final long serialVersionUID = -5042871389501734493L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TASK_ID")
    protected Task task;

    @Column(name = "TASK_NAME", length = 100)
    protected String taskName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID")
    protected User user;

    @JoinTable(name = "TS_TIME_ENTRY_TAG_LINK",
            joinColumns = @JoinColumn(name = "TIME_ENTRY_ID"),
            inverseJoinColumns = @JoinColumn(name = "TAG_ID"))
    @ManyToMany
    protected Set<Tag> tags;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_", nullable = false)
    protected Date date;

    @Column(name = "TIME_IN_MINUTES", nullable = false)
    protected Integer timeInMinutes;

    @Column(name = "STATUS", nullable = false)
    protected String status = TimeEntryStatus.NEW.getId();

    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "REJECTION_REASON")
    protected String rejectionReason;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTIVITY_TYPE_ID")
    protected ActivityType activityType;


    public void setTimeInMinutes(Integer timeInMinutes) {
        this.timeInMinutes = timeInMinutes;
    }

    public Integer getTimeInMinutes() {
        return timeInMinutes;
    }


    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public ActivityType getActivityType() {
        return activityType;
    }


    public TimeEntryStatus getStatus() {
        return status == null ? null : TimeEntryStatus.fromId(status);
    }

    public void setStatus(TimeEntryStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public String getCaption() {
        return HoursAndMinutes.fromTimeEntry(this).toString();
    }
}