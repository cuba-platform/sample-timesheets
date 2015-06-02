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
import com.haulmont.timesheets.global.ResultAndCause;
import com.haulmont.timesheets.global.ValidationTools;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.haulmont.timesheets.gui.SecurityAssistant;
import com.haulmont.timesheets.service.ProjectsService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

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
    @Inject
    protected SecurityAssistant securityAssistant;
    @Inject
    protected ValidationTools validationTools;

    @Named("fieldGroup.task")
    protected LookupPickerField taskField;
    @Named("fieldGroup.status")
    protected LookupField statusField;

    protected Component rejectionReason;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setWidthAuto();

        taskField.addAction(ComponentsHelper.createLookupAction(taskField));
        taskField.addClearAction();
        fieldGroup.addCustomField("description", ComponentsHelper.getCustomTextArea());
        fieldGroup.addCustomField("rejectionReason", ComponentsHelper.getCustomTextArea());

        rejectionReason = fieldGroup.getFieldComponent("rejectionReason");

        timeEntryDs.addListener(new DsListenerAdapter<TimeEntry>() {
            @Override
            public void valueChanged(TimeEntry source, String property, Object prevValue, Object value) {
                if ("task".equals(property)) {
                    tagsDs.clear();
                    if (value != null) {
                        Task task = (Task) value;
                        for (Tag tag : task.getDefaultTags()) {
                            tagsDs.includeItem(tag);
                        }
                        allTagsDs.refresh(ParamsMap.of("project", task.getProject()));
                    }
                    updateStatusField();
                    updateRejectionReasonField();
                    setDefaultStatus(getItem());
                }
                updateStatus();
            }
        });
    }

    @Override
    protected void initNewItem(TimeEntry item) {
        super.initNewItem(item);
        if (item.getStatus() == null) {
            setDefaultStatus(item);
        }
        if (item.getUser() == null) {
            item.setUser(userSession.getUser());
        }
    }

    @Override
    protected void postInit() {
        super.postInit();
        TimeEntry timeEntry = getItem();

        if (TimeEntryStatus.CLOSED.equals(timeEntry.getStatus()) && !securityAssistant.isSuperUser()) {
            setReadOnly();
        }

        if (TimeEntryStatus.APPROVED.equals(timeEntry.getStatus()) && userIsWorker()) {
            setReadOnly();
        }

        updateStatusField();
        updateRejectionReasonField();

        if (timeEntry.getTask() != null) {
            allTagsDs.refresh(ParamsMap.of("project", timeEntry.getTask().getProject()));
        }

        if (!securityAssistant.isSuperUser()) {
            statusField.setOptionsList(Arrays.asList(TimeEntryStatus.NEW, TimeEntryStatus.APPROVED, TimeEntryStatus.REJECTED));
        }
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        ResultAndCause resultAndCause = validationTools.validateTags(getItem());
        if (resultAndCause.isNegative) {
            showNotification(resultAndCause.cause, NotificationType.WARNING_HTML);
        }
    }

    protected boolean userIsWorker() {
        if (securityAssistant.isSuperUser()) {
            return false;
        }

        ProjectRole workerRole = projectsService.getEntityByCode(ProjectRole.class, "worker", null);
        if (workerRole == null) {
            return true;
        }
        Task task = getItem().getTask();
        Project project = task != null ? task.getProject() : null;
        if (project == null) {
            return true;
        }
        ProjectRole userRole = projectsService.getUserProjectRole(project, userSession.getUser());
        return userRole == null || workerRole.equals(userRole);
    }

    protected void setReadOnly() {
        fieldGroup.setEnabled(false);
        tagsTokenList.setEnabled(false);
    }

    protected void updateStatusField() {
        statusField.setEnabled(!userIsWorker());
    }

    protected void updateRejectionReasonField() {
        rejectionReason.setEnabled(!userIsWorker());
    }

    protected void updateStatus() {
        TimeEntry item = getItem();
        if (!TimeEntryStatus.REJECTED.equals(item.getStatus()) && userIsWorker()) {
            setDefaultStatus(item);
        }
    }

    protected void setDefaultStatus(TimeEntry item) {
        item.setStatus(TimeEntryStatus.NEW);
    }
}