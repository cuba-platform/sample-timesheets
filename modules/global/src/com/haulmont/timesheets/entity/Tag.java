/*
 * Copyright (c) 2015 com.haulmont.ts.entity
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

    @Column(name = "CODE", nullable = false, unique = true, length = 50)
    protected String code;

    @Column(name = "DESCRIPTION")
    protected String description;

    @ManyToOne(fetch = FetchType.LAZY)
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