/*
 * Copyright (c) 2015 com.haulmont.ts.gui.timeentry
 */
package com.haulmont.timesheets.gui.timeentry;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.haulmont.timesheets.service.ProjectsService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author gorelov
 */
public class TimeEntryEdit extends AbstractEditor<TimeEntry> {

    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected TokenList tagsTokenList;
    @Inject
    protected UserSession userSession;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected Datasource<TimeEntry> timeEntryDs;
    @Inject
    protected CollectionDatasource<Tag, UUID> tagsDs;
    @Inject
    protected CollectionDatasource<Tag, UUID> allTagsDs;

    @Named("fieldGroup.task")
    protected LookupPickerField taskField;
    @Named("fieldGroup.status")
    protected LookupField statusField;

    @Override
    public void init(Map<String, Object> params) {
        taskField.addAction(ComponentsHelper.createLookupAction(taskField));
        taskField.addClearAction();
        fieldGroup.addCustomField("description", ComponentsHelper.getCustomTextArea());

        timeEntryDs.addListener(new DsListenerAdapter<TimeEntry>() {
            @Override
            public void valueChanged(TimeEntry source, String property, Object prevValue, Object value) {
                if ("task".equals(property)) {
                    // #PL-5355
                    tagsDs.clear();
                    if (value != null) {
                        Task task = (Task) value;
                        for (Tag tag : task.getDefaultTags()) {
                            tagsDs.includeItem(tag);
                        }

                        List<UUID> ids = null;
                        if (!task.getRequiredTagTypes().isEmpty()) {
                            ids = new ArrayList<>();
                            for (TagType type : task.getRequiredTagTypes()) {
                                ids.add(type.getId());
                            }
                        }
                        allTagsDs.refresh(ParamsMap.of("requiredTagTypes", ids));
                    }
                    updateStatusField();
                    setDefaultStatus();
                }
                updateStatus();
            }
        });
    }

    @Override
    protected void postInit() {
        super.postInit();
        TimeEntry timeEntry = getItem();
        if (timeEntry.getStatus() == null) {
            setDefaultStatus();
        } else if (TimeEntryStatus.APPROVED.equals(timeEntry.getStatus()) && userIsWorker()) {
            setReadOnly();
        }
        if (timeEntry.getUser() == null) {
            timeEntry.setUser(userSession.getUser());
        }
        updateStatusField();
    }

    protected boolean userIsWorker() {
        ProjectRole workerRole = projectsService.getRoleByCode("worker");
        Task task = getItem().getTask();
        Project project = task != null ? task.getProject() : null;
        ProjectRole userRole = projectsService.getUserProjectRole(project, userSession.getUser());
        return workerRole == null || userRole == null || workerRole.equals(userRole);
    }

    protected void setReadOnly() {
        fieldGroup.setEnabled(false);
        tagsTokenList.setEnabled(false);
    }

    protected void updateStatusField() {
        statusField.setEnabled(!userIsWorker());
    }

    protected void updateStatus() {
        if (!TimeEntryStatus.REJECTED.equals(getItem().getStatus()) && userIsWorker()) {
            setDefaultStatus();
        }
    }

    protected void setDefaultStatus() {
        getItem().setStatus(TimeEntryStatus.NEW);
    }
}