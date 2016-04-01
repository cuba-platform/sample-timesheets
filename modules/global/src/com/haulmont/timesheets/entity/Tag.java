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

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.text.MessageFormat;

/**
 * @author gorelov
 */
@NamePattern("#getCaption|name,tagType")
@Table(name = "TS_TAG")
@Entity(name = "ts$Tag")
public class Tag extends StandardEntity {

    private static final long serialVersionUID = 7460355223234159296L;

    @Column(name = "NAME", nullable = false, length = 100)
    protected String name;

    @Column(name = "CODE", nullable = false, length = 50)
    protected String code;

    @Column(name = "DESCRIPTION")
    protected String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TAG_TYPE_ID")
    protected TagType tagType;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TagType getTagType() {
        return tagType;
    }

    public void setTagType(TagType tagType) {
        this.tagType = tagType;
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
        String pattern;
        Object[] params;
        if (tagType != null) {
            pattern = "{0} [{1}]";
            params = new Object[]{
                    StringUtils.trimToEmpty(name),
                    StringUtils.trimToEmpty(tagType.getName())
            };
        } else {
            pattern = "{0}";
            params = new Object[]{
                    StringUtils.trimToEmpty(name)
            };
        }
        MessageFormat fmt = new MessageFormat(pattern);
        return StringUtils.trimToEmpty(fmt.format(params));
    }
}