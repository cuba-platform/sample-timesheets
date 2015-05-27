/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.DialogParams;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.global.DateTools;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author gorelov
 * @version $Id$
 */
public class ComponentsHelper {

    public static final String COMMON_DAY_CAPTION_STYLE = "%s %d";
    public static final String TODAY_CAPTION_STYLE = "<strong><span style=\"text-decoration: underline;\">%s</span></strong>";
    public static final String HOLIDAY_CAPTION_STYLE = "<font color=\"#EF525B\">%s</font>";

    protected static ComponentsFactory componentsFactory = AppBeans.get(ComponentsFactory.NAME);
    protected static Messages messages = AppBeans.get(Messages.NAME);
    protected static TimeSource timeSource = AppBeans.get(TimeSource.NAME);
    protected static DateTools dateTools = AppBeans.get(DateTools.NAME);

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

    public static String getColumnCaption(String columnId, Date date) {
        String caption = messages.getMessage(WeeklyReportEntry.class, "WeeklyReportEntry." + columnId);
        String format = COMMON_DAY_CAPTION_STYLE;

        if (dateTools.isHoliday(date) || dateTools.isWeekend(date)) {
            format = String.format(HOLIDAY_CAPTION_STYLE, format);
        }
        if (DateUtils.isSameDay(timeSource.currentTimestamp(), date)) {
            format = String.format(TODAY_CAPTION_STYLE, format);
        }
        return String.format(format, caption, DateUtils.toCalendar(date).get(Calendar.DAY_OF_MONTH));
    }

    public static abstract class CustomRemoveAction extends AbstractAction {

        protected IFrame frame;
        protected String confirmationMessage;
        protected String confirmationTitle;

        protected CustomRemoveAction(String id, IFrame frame) {
            super(id);
            this.frame = frame;
        }

        @Override
        public void actionPerform(Component component) {
            final String messagesPackage = AppConfig.getMessagesPack();
            frame.showOptionDialog(
                    getConfirmationTitle(messagesPackage),
                    getConfirmationMessage(messagesPackage),
                    IFrame.MessageType.CONFIRMATION,
                    new com.haulmont.cuba.gui.components.Action[]{
                            new DialogAction(DialogAction.Type.OK) {
                                @Override
                                public void actionPerform(Component component) {
                                    doRemove();
                                }
                            },
                            new DialogAction(DialogAction.Type.CANCEL)
                    }
            );
        }

        @Override
        public String getCaption() {
            return null;
        }

        protected abstract void doRemove();

        protected String getConfirmationMessage(String messagesPackage) {
            if (confirmationMessage != null)
                return confirmationMessage;
            else
                return messages.getMessage(messagesPackage, "dialogs.Confirmation.Remove");
        }

        protected String getConfirmationTitle(String messagesPackage) {
            if (confirmationTitle != null)
                return confirmationTitle;
            else
                return messages.getMessage(messagesPackage, "dialogs.Confirmation");
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

    public static String getTimeEntryStatusStyleBg(List<TimeEntry> timeEntries) {
        if (timeEntries.isEmpty()) {
            return null;
        }
        TimeEntryStatus status = timeEntries.get(0).getStatus();
        for (TimeEntry timeEntry : timeEntries) {
            if (!status.equals(timeEntry.getStatus())) {
                return null;
            }
        }
        String style = getTimeEntryStatusStyle(timeEntries.get(0));
        return style != null ? style + "-bg" : null;
    }

    public static String getCacheKeyForEntity(Entity entity, String column) {
        return String.format("%s.%s", entity.getId(), column);
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
