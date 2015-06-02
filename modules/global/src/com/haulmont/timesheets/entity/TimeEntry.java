/*
 * Copyright (c) 2015 com.haulmont.ts.entity
 */
package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.security.entity.User;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * @author gorelov
 */
@Listeners("ts_TimeEntryListener")
@NamePattern("#getCaption|time")
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

    @Temporal(TemporalType.TIME)
    @Column(name = "TIME_", nullable = false)
    protected Date time;

    @Column(name = "STATUS", nullable = false)
    protected String status;

    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "REJECTION_REASON")
    protected String rejectionReason;

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

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
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
        Messages messages = AppBeans.get(Messages.NAME);
        DateFormat df = new SimpleDateFormat(messages.getMainMessage("timeFormat"));
        MessageFormat fmt = new MessageFormat("{0}");
        return StringUtils.trimToEmpty(fmt.format(new Object[]{
                StringUtils.trimToEmpty(df.format(getTime()))
        }));
    }
}