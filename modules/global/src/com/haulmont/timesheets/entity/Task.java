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
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import org.apache.commons.lang.StringUtils;

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

    @Column(name = "CODE", nullable = false, length = 50)
    protected String code;

    @Column(name = "DESCRIPTION")
    protected String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROJECT_ID")
    protected Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    protected TaskType type;

    @Column(name = "STATUS", nullable = false)
    protected String status;

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
    protected Set<ProjectParticipant> exclusiveParticipants;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "task")
    protected Set<TimeEntry> timeEntries;

    public void setExclusiveParticipants(Set<ProjectParticipant> exclusiveParticipants) {
        this.exclusiveParticipants = exclusiveParticipants;
    }

    public Set<ProjectParticipant> getExclusiveParticipants() {
        return exclusiveParticipants;
    }


    public TaskStatus getStatus() {
        return status == null ? null : TaskStatus.fromId(status);
    }

    public void setStatus(TaskStatus status) {
        this.status = status == null ? null : status.getId();
    }

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

    @MetaProperty(related = {"defaultTags"})
    public String getDefaultTagsList() {
        if (!PersistenceHelper.isLoaded(this, "defaultTags")) {
            return null;
        }

        if (defaultTags != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Tag defaultTag : defaultTags) {
                stringBuilder.append(defaultTag.getInstanceName()).append(",");
            }
            return StringUtils.chop(stringBuilder.toString());
        }

        return "";
    }

    @MetaProperty(related = {"requiredTagTypes"})
    public String getRequiredTagTypesList() {
        if (!PersistenceHelper.isLoaded(this, "requiredTagTypes")) {
            return null;
        }

        if (requiredTagTypes != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (TagType requiredTagType : requiredTagTypes) {
                stringBuilder.append(requiredTagType.getInstanceName()).append(",");
            }
            return StringUtils.chop(stringBuilder.toString());
        }

        return "";
    }
}