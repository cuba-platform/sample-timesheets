/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui.projectparticipant
 */
package com.haulmont.timesheets.gui.projectparticipant;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.ProjectParticipant;
import org.apache.commons.lang.BooleanUtils;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

/**
 * @author gorelov
 */
public class ProjectparticipantLookup extends AbstractLookup {

    @Inject
    protected Table projectParticipantsTable;
    @Inject
    protected CollectionDatasource<ProjectParticipant, UUID> projectParticipantsDs;

    @Override
    public void init(Map<String, Object> params) {
        if (BooleanUtils.isTrue((Boolean) params.get("multiselect"))) {
            projectParticipantsTable.setMultiSelect(true);
        }
        Project project = (Project) params.get("project");
        projectParticipantsDs.refresh(ParamsMap.of("project", project));
    }
}