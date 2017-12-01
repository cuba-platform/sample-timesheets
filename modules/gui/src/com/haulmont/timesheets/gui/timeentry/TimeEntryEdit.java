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

package com.haulmont.timesheets.gui.timeentry;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.Security;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.global.HoursAndMinutes;
import com.haulmont.timesheets.global.ResultAndCause;
import com.haulmont.timesheets.global.TimeParser;
import com.haulmont.timesheets.global.ValidationTools;
import com.haulmont.timesheets.gui.data.TagsCollectionDatasource;
import com.haulmont.timesheets.gui.util.ComponentsHelper;
import com.haulmont.timesheets.gui.util.SecurityAssistant;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * @author gorelov
 */
@SuppressWarnings("WeakerAccess")
public class TimeEntryEdit extends AbstractEditor<TimeEntry> {
    @Inject
    private CollectionDatasource<ActivityType, UUID> activityTypesDs;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected TokenList otherTagsTokenList;
    @Inject
    protected UserSession userSession;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected Datasource<TimeEntry> timeEntryDs;
    @Inject
    protected CollectionDatasource<Tag, UUID> tagsDs;
    @Inject
    protected CollectionDatasource<Tag, UUID> otherTagsDs;
    @Inject
    protected TagsCollectionDatasource optionOtherTagsDs;
    @Inject
    protected SecurityAssistant securityAssistant;
    @Inject
    protected ValidationTools validationTools;
    @Inject
    protected BoxLayout tagsTokenListsBox;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected TimeParser timeParser;
    @Inject
    private Security security;
    @Named("fieldGroup.task")
    protected LookupPickerField taskField;
    @Named("fieldGroup.status")
    protected LookupField statusField;
    @Named("fieldGroup.user")
    protected Field userField;
    @Named("fieldGroup.activityType")
    protected Field activityType;

    protected Component rejectionReason;
    protected TextField time;

    @Override
    public void init(Map<String, Object> params) {
        taskField.addAction(ComponentsHelper.createLookupAction(taskField));
        taskField.addClearAction();
        fieldGroup.addCustomField("description", ComponentsHelper.getCustomTextArea());
        fieldGroup.addCustomField("rejectionReason", ComponentsHelper.getCustomTextArea());
        fieldGroup.addCustomField("timeInMinutes", (datasource, propertyId) ->
                componentsFactory.<TextField>createComponent(TextField.NAME));

        rejectionReason = fieldGroup.getFieldComponent("rejectionReason");
        time = (TextField) fieldGroup.getFieldComponent("timeInMinutes");

        timeEntryDs.addItemPropertyChangeListener(e -> {
            if ("task".equals(e.getProperty())) {
                tagsDs.clear();
                if (e.getValue() != null) {
                    Task task = (Task) e.getValue();
                    for (Tag tag : task.getDefaultTags()) {
                        tagsDs.includeItem(tag);
                    }
                    updateOtherTagsDs(task.getProject(), task.getRequiredTagTypes());
                }
                updateStatusField();
                updateRejectionReasonField();
                updateTagsLists();
                setDefaultStatus(getItem());
                updateActivityType();
            }
            updateStatus();
        });
    }

    protected void updateActivityType() {
        activityTypesDs.refresh();
        activityType.setVisible(activityTypesDs.getItemIds().size() > 0);
    }

    protected void updateTagsLists() {
        if (security.isSpecificPermitted("app.canEditTags")) {
            tagsTokenListsBox.removeAll();
            Task task = getItem().getTask();
            if (task != null && CollectionUtils.isNotEmpty(task.getRequiredTagTypes())) {
                for (TagType type : task.getRequiredTagTypes()) {
                    CollectionDatasource<Tag, UUID> ds = createTagsDs(null);
                    CollectionDatasource optionDs = createTagsDs(type);
                    TokenList tokenList = createTokenList(ds, optionDs, getListCaption(type));
                    for (Tag tag : getAssignedTags(type, null)) {
                        ds.addItem(tag);
                    }
                    ds.addCollectionChangeListener(new TagDsListener());
                    tagsTokenListsBox.add(tokenList);
                }
            }
            otherTagsDs.clear();
            for (Tag tag : getAssignedTags(null, task != null ? task.getRequiredTagTypes() : null)) {
                otherTagsDs.addItem(tag);
            }

            otherTagsTokenList.setVisible(!CollectionUtils.isEmpty(optionOtherTagsDs.getItemIds()));
        } else {
            otherTagsTokenList.setVisible(false);
        }
    }

    protected Set<Tag> getAssignedTags(TagType required, Set<TagType> exclude) {
        TimeEntry timeEntry = getItem();
        if (timeEntry == null || CollectionUtils.isEmpty(timeEntry.getTags())) {
            return Collections.emptySet();
        }
        if (required == null && CollectionUtils.isEmpty(exclude)) {
            return getItem().getTags();
        }
        Set<Tag> assigned = new HashSet<>();
        for (Tag tag : timeEntry.getTags()) {
            if (required != null && required.equals(tag.getTagType())
                    || required == null && !exclude.contains(tag.getTagType())) {
                assigned.add(tag);
            }
        }
        return assigned;
    }

    protected String getListCaption(TagType type) {
        return String.format("%s %s*", type.getName(), otherTagsTokenList.getCaption());
    }

    protected void updateOtherTagsDs(Project project, Set<TagType> types) {
        optionOtherTagsDs.setExcludeTagTypes(types);
        optionOtherTagsDs.refresh(ParamsMap.of("project", project));
    }

    @SuppressWarnings("unchecked")
    protected CollectionDatasource<Tag, UUID> createTagsDs(TagType required) {
        DsBuilder builder;
        if (required == null) {
            builder = new DsBuilder(getDsContext())
                    .setViewName("tag-with-type")
                    .setAllowCommit(false)
                    .setRefreshMode(CollectionDatasource.RefreshMode.NEVER);
        } else {
            builder = new DsBuilder()
                    .setDsClass(TagsCollectionDatasource.class);
        }
        builder.setJavaClass(Tag.class);
        if (required == null) {
            return builder.buildCollectionDatasource();
        } else {
            TagsCollectionDatasource ds = (TagsCollectionDatasource) builder.buildCollectionDatasource();
            ds.setRequiredTagType(required);
            ds.refresh();
            return ds;
        }
    }

    protected TokenList createTokenList(CollectionDatasource ds, CollectionDatasource optionDs, String caption) {
        TokenList tokenList = componentsFactory.createComponent(TokenList.class);
        tokenList.setCaption(caption);
        tokenList.setWidth("500px");
        tokenList.setInline(true);
        tokenList.setAddButtonIcon("icons/plus-btn.png");
        tokenList.setDatasource(ds);
        tokenList.setOptionsDatasource(optionDs);
        return tokenList;
    }

    @Override
    protected void initNewItem(TimeEntry item) {
        super.initNewItem(item);
        if (item.getStatus() == null) {
            setDefaultStatus(item);
        }
        if (item.getUser() == null) {
            item.setUser(userSession.getCurrentOrSubstitutedUser());
        }
        if (item.getDate() == null) {
            item.setDate(timeSource.currentTimestamp());
        }
    }

    @Override
    protected void postInit() {
        super.postInit();
        TimeEntry timeEntry = getItem();

        HoursAndMinutes hoursAndMinutes = HoursAndMinutes.fromTimeEntry(timeEntry);
        if (hoursAndMinutes.toMinutes() > 0) {
            time.setValue(hoursAndMinutes);
        } else {
            time.setValue(null);
        }

        if (TimeEntryStatus.CLOSED.equals(timeEntry.getStatus()) && !securityAssistant.isSuperUser()) {
            setReadOnly();
        }

        if (TimeEntryStatus.APPROVED.equals(timeEntry.getStatus()) && userIsWorker()) {
            setReadOnly();
        }

        updateStatusField();
        updateRejectionReasonField();

        if (timeEntry.getTask() != null) {
            Task task = timeEntry.getTask();
            updateOtherTagsDs(task.getProject(), task.getRequiredTagTypes());
        }
        updateTagsLists();
        otherTagsDs.addCollectionChangeListener(new TagDsListener());

        if (!securityAssistant.isSuperUser()) {
            statusField.setOptionsList(Arrays.asList(TimeEntryStatus.NEW, TimeEntryStatus.APPROVED, TimeEntryStatus.REJECTED));
        }

        if (userSession.getCurrentOrSubstitutedUser().equals(timeEntry.getUser())) {
            userField.setVisible(false);
        }

        updateActivityType();

        time.addValueChangeListener(e -> {
            HoursAndMinutes hoursAndMin = timeParser.parseToHoursAndMinutes(String.valueOf(e.getValue()));
            if (hoursAndMin.toMinutes() > 0) {
                getItem().setTimeInMinutes(hoursAndMin.toMinutes());
                time.setValue(hoursAndMin);
            } else {
                getItem().setTimeInMinutes(null);
                time.setValue(null);
            }
        });
    }

    @Override
    public void commitAndClose() {
        if (validateAll()) {
            ResultAndCause validationResult = validationTools.validateTags(getItem());
            if (validationResult.isNegative) {
                showOptionDialog(getMessage("caption.attention"),
                        validationResult.cause + getMessage("confirmation.manuallyTagSetting"),
                        MessageType.CONFIRMATION_HTML,
                        Arrays.asList(
                                new DialogAction(DialogAction.Type.YES) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        TimeEntryEdit.super.commitAndClose();
                                    }
                                },
                                new DialogAction(DialogAction.Type.NO)));

            } else {
                super.commitAndClose();
            }
        }
    }

    protected boolean userIsWorker() {
        if (securityAssistant.isSuperUser()) {
            return false;
        }

        ProjectRole workerRole = projectsService.getEntityByCode(ProjectRole.class, ProjectRoleCode.WORKER.getId(), null);
        if (workerRole == null) {
            return true;
        }
        Task task = getItem().getTask();
        Project project = task != null ? task.getProject() : null;
        if (project == null) {
            return true;
        }
        ProjectRole userRole = projectsService.getUserProjectRole(project, userSession.getCurrentOrSubstitutedUser());
        return userRole == null || workerRole.equals(userRole);
    }

    protected void setReadOnly() {
        fieldGroup.setEnabled(false);
        otherTagsTokenList.setEnabled(false);
    }

    protected void updateStatusField() {
        statusField.setEnabled(!userIsWorker());
        if (PersistenceHelper.isNew(getItem())) {
            statusField.setVisible(false);
        }
    }

    protected void updateRejectionReasonField() {
        rejectionReason.setEnabled(!userIsWorker());
        if (PersistenceHelper.isNew(getItem())
                || (userIsWorker() && getItem().getRejectionReason() == null)) {
            rejectionReason.setVisible(false);
        }
    }

    protected void updateStatus() {
        TimeEntry item = getItem();
        if (TimeEntryStatus.REJECTED.equals(item.getStatus()) && userIsWorker()) {
            setDefaultStatus(item);
        }
    }

    protected void setDefaultStatus(TimeEntry item) {
        item.setStatus(TimeEntryStatus.NEW);
    }

    protected class TagDsListener implements CollectionDatasource.CollectionChangeListener<Tag, UUID> {

        @Override
        public void collectionChanged(CollectionDatasource.CollectionChangeEvent<Tag, UUID> e) {
            switch (e.getOperation()) {
                case ADD:
                    for (Tag tag : e.getItems()) {
                        tagsDs.addItem(tag);
                    }
                    break;
                case REMOVE:
                    for (Tag tag : e.getItems()) {
                        tagsDs.removeItem(tag);
                    }
                    break;
            }
        }
    }
}