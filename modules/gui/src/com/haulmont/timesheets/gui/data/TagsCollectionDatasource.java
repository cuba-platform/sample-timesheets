
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

package com.haulmont.timesheets.gui.data;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CollectionDatasourceImpl;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.Tag;
import com.haulmont.timesheets.entity.TagType;
import com.haulmont.timesheets.service.ProjectsService;

import java.util.*;

/**
 * @author gorelov
 */
public class TagsCollectionDatasource extends CollectionDatasourceImpl<Tag, UUID> {

    protected TagType requiredTagType;
    protected Set<TagType> excludeTagTypes;

    @Override
    protected void loadData(Map<String, Object> params) {
        detachListener(data.values());
        data.clear();

        ProjectsService projectsService = AppBeans.get(ProjectsService.NAME);
        List<Tag> loaded;
        if (requiredTagType != null) {
            loaded = projectsService.getTagsWithTheTagType(requiredTagType, "tag-with-type");
        } else {
            Project project = (Project) params.get("project");
            loaded = projectsService.getTagsForTheProject(project, "tag-with-type");
        }
        for (Tag tag : loaded) {
            if (excludeTagTypes == null || !excludeTagTypes.contains(tag.getTagType())) {
                data.put(tag.getId(), tag);
                attachListener(tag);
            }
        }
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
