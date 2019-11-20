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

import com.haulmont.bali.events.Subscription;
import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.Security;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.data.options.ContainerOptions;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.util.OperationResult;
import com.haulmont.cuba.gui.util.UnknownOperationResult;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.global.HoursAndMinutes;
import com.haulmont.timesheets.global.ResultAndCause;
import com.haulmont.timesheets.global.TimeParser;
import com.haulmont.timesheets.global.ValidationTools;
import com.haulmont.timesheets.gui.data.TagsCollectionLoadDelegate;
import com.haulmont.timesheets.gui.data.TasksCollectionLoadDelegate;
import com.haulmont.timesheets.gui.util.SecurityAssistant;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import java.util.*;

/**
 * @author gorelov
 */
@UiController("ts$TimeEntry.edit")
@UiDescriptor("timeentry-edit.xml")
@EditedEntityContainer("timeEntryDc")
public class TimeEntryEdit extends StandardEditor<TimeEntry> {

    @Inject
    protected TimeParser timeParser;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected Security security;
    @Inject
    protected DataComponents dataComponents;
    @Inject
    protected UiComponents uiComponents;
    @Inject
    protected UserSession userSession;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected CollectionContainer<ActivityType> activityTypesDc;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected SecurityAssistant securityAssistant;
    @Inject
    protected ScreenValidation screenValidation;
    @Inject
    protected ValidationTools validationTools;
    @Inject
    protected Notifications notifications;
    @Inject
    protected Dialogs dialogs;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected MessageTools messageTools;

    @Inject
    protected Form form;
    @Inject
    protected TextField<String> timeInMinutes;
    @Inject
    protected TextArea<String> description;
    @Inject
    protected TextArea<String> rejectionReason;
    @Inject
    protected LookupField<TimeEntryStatus> status;
    @Inject
    protected VBoxLayout tagsTokenListsBox;
    @Inject
    protected TokenList<Tag> otherTagsTokenList;
    @Inject
    protected LookupField<ActivityType> activityType;
    @Inject
    protected InstanceLoader<TimeEntry> timeEntryDl;
    @Inject
    protected CollectionContainer<Tag> tagsDc;
    @Inject
    protected CollectionLoader<Tag> optionOtherTagsDl;
    @Inject
    protected CollectionLoader<Task> activeTasksDl;
    @Inject
    protected CollectionContainer<Tag> optionOtherTagsDc;
    @Inject
    protected CollectionLoader<ActivityType> activityTypesDl;
    @Inject
    protected InstanceContainer<TimeEntry> timeEntryDc;
    @Inject
    protected LookupField<ExtUser> user;
    @Inject
    protected LookupPickerField<Task> taskField;

    protected boolean readOnly = false;
    protected Subscription otherTagsValueListener;

    @Subscribe(id = "timeEntryDc", target = Target.DATA_CONTAINER)
    protected void onTimeEntryDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<TimeEntry> e) {
        if ("task".equals(e.getProperty())) {
            List<Tag> tags = tagsDc.getMutableItems();
            tags.clear();
            Task task = (Task) e.getValue();
            if (task != null) {
                tags.addAll(task.getDefaultTags());
                updateOtherTagsDc(task.getProject(), task.getRequiredTagTypes());
            }
            updateStatusField();
            updateRejectionReasonField();
            updateTagsLists();
            setDefaultStatus(getEditedEntity());
            updateActivityType();
        }
        updateStatus();
    }

    @Subscribe
    protected void onInit(InitEvent event) {
        optionOtherTagsDl.setLoadDelegate(new TagsCollectionLoadDelegate());
        activeTasksDl.setLoadDelegate(new TasksCollectionLoadDelegate());
    }

    protected void updateActivityType() {
        if (getEditedEntity().getTask() != null) {
            activityTypesDl.setParameter("project", getEditedEntity().getTask().getProject());
        } else {
            activityTypesDl.setParameter("project", null);
        }
        activityTypesDl.load();
        activityType.setVisible(activityTypesDc.getItems().size() > 0);
    }

    protected void updateTagsLists() {
        if (security.isSpecificPermitted("app.canEditTags")) {

            Task task = getEditedEntity().getTask();
            if (otherTagsValueListener != null)
                otherTagsValueListener.remove();
            otherTagsTokenList.setValue(getAssignedTags(null, task != null ? task.getRequiredTagTypes() : null));
            otherTagsValueListener = otherTagsTokenList.addValueChangeListener(e -> {
                Collection<Tag> value = e.getValue();
                Collection<Tag> prevValue = e.getPrevValue();
                //noinspection ConstantConditions
                value.stream()
                        .filter(tag -> !prevValue.contains(tag))
                        .forEach(tag -> tagsDc.getMutableItems().add(tag));
                //noinspection ConstantConditions
                prevValue.stream()
                        .filter(tag -> !value.contains(tag))
                        .forEach(tag -> tagsDc.getMutableItems().remove(tag));
            });

            tagsTokenListsBox.removeAll();
            if (task != null && CollectionUtils.isNotEmpty(task.getRequiredTagTypes())) {
                otherTagsTokenList.setCaption(null);
                for (TagType type : task.getRequiredTagTypes()) {
                    CollectionContainer<Tag> optionDc = createTagsDataContainer(type);
                    TokenList tokenList = createTokenList(optionDc, getListCaption(type), type);
                    tagsTokenListsBox.add(tokenList);
                }
            } else {
                otherTagsTokenList.setCaption(messageTools.loadString("msg://com.haulmont.timesheets.entity/TimeEntry.tags"));
            }
            otherTagsTokenList.setVisible(!CollectionUtils.isEmpty(optionOtherTagsDc.getItems()));
        } else {
            otherTagsTokenList.setVisible(false);
        }
    }

    protected Set<Tag> getAssignedTags(TagType required, Set<TagType> exclude) {
        TimeEntry timeEntry = getEditedEntity();
        if (timeEntry == null || CollectionUtils.isEmpty(timeEntry.getTags())) {
            return new HashSet<>();
        }
        if (required == null && CollectionUtils.isEmpty(exclude)) {
            return timeEntry.getTags();
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
        return String.format("%s %s*", type.getName(), messageTools.loadString("msg://com.haulmont.timesheets.entity/TimeEntry.tags"));
    }

    protected void updateOtherTagsDc(Project project, Set<TagType> types) {
        TagsCollectionLoadDelegate delegate = (TagsCollectionLoadDelegate) optionOtherTagsDl.getLoadDelegate();
        delegate.setExcludeTagTypes(types);
        delegate.setProject(project);
        optionOtherTagsDl.load();
    }

    protected CollectionContainer<Tag> createTagsDataContainer(TagType required) {
        CollectionContainer<Tag> container = dataComponents.createCollectionContainer(Tag.class);
        CollectionLoader<Tag> loader = dataComponents.createCollectionLoader();
        loader.setContainer(container);
        loader.setDataContext(getScreenData().getDataContext());
        loader.setView("tag-with-type");
        TagsCollectionLoadDelegate delegate = new TagsCollectionLoadDelegate();
        delegate.setRequiredTagType(required);
        loader.setLoadDelegate(delegate);
        loader.load();
        return container;
    }

    protected TokenList createTokenList(CollectionContainer<Tag> optionDc, String caption, TagType type) {
        TokenList<Tag> tokenList = uiComponents.create(TokenList.class);
        tokenList.setCaption(caption);
        tokenList.setWidth("500px");
        tokenList.setInline(true);
        tokenList.setEditable(!readOnly);
        tokenList.setAddButtonIcon("icons/plus-btn.png");
        tokenList.setOptions(new ContainerOptions<>(optionDc));
        tokenList.setValue(getAssignedTags(type, null));
        tokenList.addValueChangeListener(e -> {
            Collection<Tag> value = e.getValue();
            Collection<Tag> prevValue = e.getPrevValue();
            //noinspection ConstantConditions
            value.stream()
                    .filter(tag -> !prevValue.contains(tag))
                    .forEach(tag -> tagsDc.getMutableItems().add(tag));
            //noinspection ConstantConditions
            prevValue.stream()
                    .filter(tag -> !value.contains(tag))
                    .forEach(tag -> tagsDc.getMutableItems().remove(tag));
        });
        return tokenList;
    }

    @Subscribe
    protected void onInitEntity(InitEntityEvent<TimeEntry> e) {
        TimeEntry item = e.getEntity();
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

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        timeEntryDl.load();
        if (getEditedEntity().getTask() != null) {
            activityTypesDl.setParameter("project", getEditedEntity().getTask().getProject());
        } else {
            activityTypesDl.setParameter("project", null);
        }
        activeTasksDl.load();
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        TimeEntry timeEntry = getEditedEntity();

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
            updateOtherTagsDc(task.getProject(), task.getRequiredTagTypes());
        }
        updateTagsLists();
        if (!securityAssistant.isSuperUser()) {
            status.setOptionsList(Arrays.asList(TimeEntryStatus.NEW, TimeEntryStatus.APPROVED, TimeEntryStatus.REJECTED));
        }

        status.addValueChangeListener(e -> {
            if (TimeEntryStatus.REJECTED.equals(e.getValue())) {
                rejectionReason.setRequired(true);
            } else {
                rejectionReason.setRequired(false);
            }
        });

        if (userSession.getCurrentOrSubstitutedUser().equals(timeEntry.getUser())) {
            user.setVisible(false);
        }

        HoursAndMinutes hoursAndMinutes = HoursAndMinutes.fromTimeEntry(timeEntry);
        if (hoursAndMinutes.toMinutes() > 0) {
            timeInMinutes.setValue(hoursAndMinutes.toString());
        } else {
            timeInMinutes.setValue(null);
        }

        timeInMinutes.addValueChangeListener(e -> {
            TimeEntry editedEntity = getEditedEntity();
            HoursAndMinutes ham = timeParser.parseToHoursAndMinutes(String.valueOf(e.getValue()));
            if (ham.toMinutes() > 0) {
                editedEntity.setTimeInMinutes(ham.toMinutes());
                e.getComponent().setValue(ham.toString());
            } else {
                editedEntity.setTimeInMinutes(null);
                e.getComponent().setValue(null);
            }
        });

        updateActivityType();
    }

    @Override
    protected OperationResult commitChanges() {
        ValidationErrors validationErrors = screenValidation.validateUiComponents(form);
        if (!validationErrors.isEmpty()) {
            screenValidation.showValidationErrors(this, validationErrors);
            return OperationResult.fail();
        }

        ResultAndCause resultAndCause = validationTools.validateTimeEntry(getEditedEntity());
        if (resultAndCause.isNegative) {
            notifications.create()
                    .withCaption(resultAndCause.cause)
                    .withContentMode(ContentMode.HTML)
                    .withType(Notifications.NotificationType.WARNING)
                    .show();
            return OperationResult.fail();
        }

        ResultAndCause validationResult = validationTools.validateTags(getEditedEntity());
        if (!validationResult.isNegative) {
            getScreenData().getDataContext().commit();

            return OperationResult.success();
        }

        UnknownOperationResult result = new UnknownOperationResult();
        dialogs.createOptionDialog(Dialogs.MessageType.CONFIRMATION)
                .withCaption(messageBundle.getMessage("caption.attention"))
                .withContentMode(ContentMode.HTML)
                .withMessage(validationResult.cause + messageBundle.getMessage("confirmation.manuallyTagSetting"))
                .withActions(
                        new DialogAction(DialogAction.Type.YES)
                                .withHandler(event -> {
                                    getScreenData().getDataContext().commit();

                                    result.success();
                                }),
                        new DialogAction(DialogAction.Type.NO)
                                .withHandler(event ->
                                        result.fail()
                                ))
                .show();

        return result;
    }

    protected boolean userIsWorker() {
        if (securityAssistant.isSuperUser()) {
            return false;
        }

        ProjectRole workerRole = projectsService.getEntityByCode(ProjectRole.class, ProjectRoleCode.WORKER.getId(), null);
        if (workerRole == null) {
            return true;
        }
        Task task = getEditedEntity().getTask();
        Project project = task != null ? task.getProject() : null;
        if (project == null) {
            return true;
        }
        ProjectRole userRole = projectsService.getUserProjectRole(project, userSession.getCurrentOrSubstitutedUser());
        return userRole == null || workerRole.equals(userRole);
    }

    protected void setReadOnly() {
        readOnly = true;
        form.setEditable(false);
        otherTagsTokenList.setEditable(false);
        setTokenListBoxReadOnly();
    }

    private void setTokenListBoxReadOnly() {
        for (Component component : tagsTokenListsBox.getComponents()) {
            if (component instanceof Component.Editable) {
                ((Component.Editable) component).setEditable(false);
            } else {
                component.setEnabled(false);
            }
        }
    }

    protected void updateStatusField() {
        status.setEditable(!userIsWorker());
        if (PersistenceHelper.isNew(getEditedEntity())) {
            status.setVisible(false);
        }
    }

    protected void updateRejectionReasonField() {
        rejectionReason.setEnabled(!userIsWorker());
        if (PersistenceHelper.isNew(getEditedEntity())
                || (userIsWorker() && getEditedEntity().getRejectionReason() == null)) {
            rejectionReason.setVisible(false);
        }
    }

    protected void updateStatus() {
        TimeEntry item = getEditedEntity();
        if (TimeEntryStatus.REJECTED.equals(item.getStatus()) && userIsWorker()) {
            setDefaultStatus(item);
        }
    }

    protected void setDefaultStatus(TimeEntry item) {
        item.setStatus(TimeEntryStatus.NEW);
    }
}