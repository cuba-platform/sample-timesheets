/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.DialogParams;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.timesheets.entity.*;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author gorelov
 * @version $Id$
 */
public class ComponentsHelper {

    protected static ComponentsFactory componentsFactory = AppBeans.get(ComponentsFactory.NAME);

    public static FieldGroup.CustomFieldGenerator getCustomTextArea() {
        return new FieldGroup.CustomFieldGenerator() {
            @Override
            public Component generateField(Datasource datasource, String propertyId) {
                ResizableTextArea textArea = componentsFactory.createComponent(ResizableTextArea.NAME);
                textArea.setDatasource(datasource, propertyId);
                textArea.setHeight("100px");
                textArea.setResizable(true);
                return textArea;
            }
        };
    }

    public static void addRemoveColumn(final Table table, String columnName) {
        table.addGeneratedColumn(columnName, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                LinkButton removeButton = componentsFactory.createComponent(LinkButton.NAME);
                removeButton.setIcon("icons/remove.png");
                removeButton.setAction(new EntityRemoveAction(table, entity));
                return removeButton;
            }
        });
        table.setColumnCaption(columnName, "");
        table.setColumnWidth(columnName, 35);
    }

    public static PickerField.LookupAction createLookupAction(PickerField pickerField) {
        PickerField.LookupAction lookupAction = new PickerField.LookupAction(pickerField);
        lookupAction.setLookupScreenOpenType(WindowManager.OpenType.DIALOG);
        lookupAction.setLookupScreenDialogParams(new DialogParams()
                .setWidth(800)
                .setHeight(500)
                .setResizable(true));
        return lookupAction;
    }

    public static LinkButton createCaptionlessLinkButton(String icon, String description, Action action) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.NAME);
        linkButton.setIcon(icon);
        linkButton.setDescription(description);
        linkButton.setAlignment(Component.Alignment.MIDDLE_CENTER);
        linkButton.setAction(action);
        return linkButton;
    }

    public static class EntityRemoveAction extends CaptionlessRemoveAction {

        private Entity entity;

        public EntityRemoveAction(ListComponent target, Entity entity) {
            super(target);
            this.entity = entity;
        }

        @Override
        public void actionPerform(Component component) {
            if (!isEnabled()) {
                return;
            }

            Set<Entity> selected = new HashSet<>(1);
            selected.add(entity);
            confirmAndRemove(selected);
        }
    }

    public static class TaskStatusTrackingAction extends ItemTrackingAction {

        public TaskStatusTrackingAction(ListComponent target, String id) {
            super(target, id);
        }

        @Override
        public void actionPerform(Component component) {
            Task task = target.getSingleSelected();
            if (task != null) {
                if (task.getStatus() != null) {
                    task.setStatus(task.getStatus().inverted());
                    target.getDatasource().commit();
//                    target.refresh();
                }
            }
        }

        @Override
        public void refreshState() {
            super.refreshState();

            String captionKey = "closeTask";
            Task selected = target.getSingleSelected();
            if (selected != null) {
                TaskStatus status = selected.getStatus();
                if (status != null && TaskStatus.INACTIVE.equals(status)) {
                    captionKey = "openTask";
                }
            }
            setCaption(messages.getMessage(getClass(), captionKey));
        }

        @Override
        public String getIcon() {
            return "font-icon:EXCHANGE";
        }
    }

    public static class CaptionlessRemoveAction extends RemoveAction {

        public CaptionlessRemoveAction(ListComponent target) {
            super(target);
        }

        @Override
        public String getCaption() {
            return null;
        }
    }

    public static String getTaskStatusStyle(Task task) {
        switch (task.getStatus()) {
            case ACTIVE:
                return "task-active";
            case INACTIVE:
                return "task-inactive";
            default:
                return null;
        }
    }

    public static String getProjectStatusStyle(Project project) {
        switch (project.getStatus()) {
            case OPEN:
                return "project-open";
            case CLOSED:
                return "project-closed";
            default:
                return null;
        }
    }

    public static String getTimeEntryStatusStyle(TimeEntry timeEntry) {
        switch (timeEntry.getStatus()) {
            case NEW:
                return "time-entry-new";
            case APPROVED:
                return "time-entry-approved";
            case REJECTED:
                return "time-entry-rejected";
            default:
                return null;
        }
    }

    public static String getTimeEntryStatusStyleBg(TimeEntry timeEntry) {
        String style = getTimeEntryStatusStyle(timeEntry);
        return style != null ? style + "-bg" : null;
    }

    public static class EntityCodeGenerationListener<T extends Entity> extends DsListenerAdapter<T> {
        @Override
        public void valueChanged(T source, String property, Object prevValue, Object value) {
            if ("name".equalsIgnoreCase(property) && source.getMetaClass().getProperty("code") != null) {
                String codeValue = source.getValue("code");
                if (StringUtils.isBlank(codeValue)) {
                    String newName = String.valueOf(value);
                    String newCode = newName.toUpperCase().replaceAll(" ", "_");
                    source.setValue("code", newCode);
                }

            }
        }
    }
}
