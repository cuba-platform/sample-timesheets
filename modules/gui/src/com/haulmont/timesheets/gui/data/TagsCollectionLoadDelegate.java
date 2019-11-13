/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui.data;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.Tag;
import com.haulmont.timesheets.entity.TagType;
import com.haulmont.timesheets.service.ProjectsService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class TagsCollectionLoadDelegate implements Function<LoadContext<Tag>, List<Tag>> {
    protected TagType requiredTagType;
    protected Set<TagType> excludeTagTypes;
    protected Project project;

    @Override
    public List<Tag> apply(LoadContext<Tag> tagLoadContext) {
        ProjectsService projectsService = AppBeans.get(ProjectsService.NAME);
        List<Tag> loaded;
        if (requiredTagType != null) {
            loaded = projectsService.getTagsWithTheTagType(requiredTagType, "tag-with-type");
        } else {
            loaded = projectsService.getTagsForTheProject(project, "tag-with-type");
        }
        List<Tag> data = new ArrayList<>();
        for (Tag tag : loaded) {
            if (excludeTagTypes == null || !excludeTagTypes.contains(tag.getTagType())) {
                data.add(tag);
            }
        }
        return data;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setRequiredTagType(TagType requiredTagType) {
        this.requiredTagType = requiredTagType;
    }

    public void addExcludeTagType(TagType type) {
        if (excludeTagTypes == null) {
            excludeTagTypes = new HashSet<>();
        }
        excludeTagTypes.add(type);
    }

    public void setExcludeTagTypes(Set<TagType> excludeTagTypes) {
        this.excludeTagTypes = excludeTagTypes;
    }
}
