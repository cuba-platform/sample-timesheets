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
@NamePattern("#getCaption|name")
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

    @JoinTable(name = "TS_TAG_TYPE_PROJECT_LINK",
        joinColumns = @JoinColumn(name = "TAG_TYPE_ID"),
        inverseJoinColumns = @JoinColumn(name = "PROJECT_ID"))
    @ManyToMany
    protected Set<Project> projects;

    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "tagType")
    @Composition
    protected Set<Tag> tags;

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<Tag> getTags() {
        return tags;
    }


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

    public String getCaption() {
        return getName();
    }
}