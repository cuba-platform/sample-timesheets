/*
 * Copyright (c) 2015 com.haulmont.ts.entity
 */
package com.haulmont.timesheets.entity;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.security.entity.User;

import javax.persistence.*;

/**
 * @author gorelov
 */
@Table(name = "TS_PROJECT_PARTICIPANT", uniqueConstraints = {
        @UniqueConstraint(name = "IDX_TS_PROJECT_PARTICIPANT_UNIQ_USER_PROJECT", columnNames = {"USER_ID", "PROJECT_ID"})
})
@Entity(name = "ts$ProjectParticipant")
public class ProjectParticipant extends StandardEntity {

    private static final long serialVersionUID = -59738612053122808L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID")
    protected User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROJECT_ID")
    protected Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ROLE_ID")
    protected ProjectRole role;

    public void setRole(ProjectRole role) {
        this.role = role;
    }

    public ProjectRole getRole() {
        return role;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}