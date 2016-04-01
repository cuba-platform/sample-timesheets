/*
 * Copyright (c) 2016 Haulmont
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
@NamePattern("%s|name")
@Table(name = "TS_PROJECT")
@Entity(name = "ts$Project")
public class Project extends StandardEntity {

    private static final long serialVersionUID = 1282645826386756072L;

    @Column(name = "NAME", nullable = false, length = 100)
    protected String name;

    @Column(name = "CODE", nullable = false, length = 50)
    protected String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    protected Project parent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CLIENT_ID")
    protected Client client;

    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "STATUS", nullable = false)
    protected String status;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "project")
    protected Set<ProjectParticipant> participants;

    @OneToMany(mappedBy = "project")
    protected Set<Task> tasks;

    @Column(name = "TIME_ENTRY_NAME_PATTERN", length = 500)
    protected String timeEntryNamePattern;

    public void setTimeEntryNamePattern(String timeEntryNamePattern) {
        this.timeEntryNamePattern = timeEntryNamePattern;
    }

    public String getTimeEntryNamePattern() {
        return timeEntryNamePattern;
    }


    public ProjectStatus getStatus() {
        return status == null ? null : ProjectStatus.fromId(status);
    }

    public void setStatus(ProjectStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setParticipants(Set<ProjectParticipant> participants) {
        this.participants = participants;
    }

    public Set<ProjectParticipant> getParticipants() {
        return participants;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setParent(Project parent) {
        this.parent = parent;
    }

    public Project getParent() {
        return parent;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}