/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui.util;

import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.aggregation.AggregationStrategy;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.global.WorkdaysTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ScreensHelper {
    public static final String COMMON_DAY_CAPTION_STYLE = "%s %d";
    public static final String TODAY_CAPTION_STYLE = "<strong><span style=\"text-decoration: underline;\">%s</span></strong>";
    public static final String HOLIDAY_CAPTION_STYLE = "<font color=\"#EF525B\">%s</font>";

    protected static Messages messages = AppBeans.get(Messages.NAME);
    protected static TimeSource timeSource = AppBeans.get(TimeSource.NAME);
    protected static WorkdaysTools workdaysTools = AppBeans.get(WorkdaysTools.NAME);
    protected static UiComponents uiComponents = AppBeans.get(UiComponents.NAME);
    protected static Metadata metadata = AppBeans.get(Metadata.NAME);

    public static class EntityCodeGenerationListener<E extends Entity> implements Consumer<InstanceContainer.ItemPropertyChangeEvent<E>> {
        @Override
        public void accept(InstanceContainer.ItemPropertyChangeEvent<E> e) {
            E source = e.getItem();
            if ("name".equalsIgnoreCase(e.getProperty()) && source.getMetaClass().getProperty("code") != null) {
                String codeValue = source.getValue("code");
                if (StringUtils.isBlank(codeValue)) {
                    String newName = String.valueOf(e.getValue());
                    String newCode = newName.toUpperCase().replaceAll(" ", "_");
                    source.setValue("code", newCode);
                }
            }
        }
    }

    public static AggregationInfo createAggregationInfo(MetaPropertyPath path, AggregationStrategy strategy) {
        AggregationInfo info = new AggregationInfo();
        info.setPropertyPath(path);
        info.setStrategy(strategy);
        return info;
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

    public static String getTimeEntryStatusStyle(TimeEntry timeEntry) {
        switch (timeEntry.getStatus()) {
            case NEW:
                return "time-entry-new";
            case APPROVED:
                return "time-entry-approved";
            case REJECTED:
                return "time-entry-rejected";
            case CLOSED:
                return "time-entry-closed";
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

    public static String createStringFromMultipleEntities(Set<Entity> entities) {
        StringBuilder sb = new StringBuilder();

        boolean first = true;
        for (Entity entity : entities) {
            if (!first) {
                sb.append(", ");
            }

            first = false;

            sb.append(metadata.getTools().getInstanceName(entity));
        }

        return sb.toString();
    }

    public static String getColumnCaption(String columnId, Date date) {
        String caption = messages.getMessage(WeeklyReportEntry.class, "WeeklyReportEntry." + columnId);
        String format = COMMON_DAY_CAPTION_STYLE;

        if (workdaysTools.isHoliday(date) || workdaysTools.isWeekend(date)) {
            format = String.format(HOLIDAY_CAPTION_STYLE, format);
        }
        if (DateUtils.isSameDay(timeSource.currentTimestamp(), date)) {
            format = String.format(TODAY_CAPTION_STYLE, format);
        }
        return String.format(format, caption, DateUtils.toCalendar(date).get(Calendar.DAY_OF_MONTH));
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

    public static LinkButton createCaptionlessLinkButton(String icon, String description, Action action) {
        LinkButton linkButton = uiComponents.create(LinkButton.class);
        linkButton.setIcon(icon);
        linkButton.setDescription(description);
        linkButton.setAlignment(Component.Alignment.MIDDLE_CENTER);
        linkButton.setAction(action);
        return linkButton;
    }

    public static abstract class CustomRemoveAction extends BaseAction {

        protected Dialogs dialogs;
        protected String confirmationMessage;
        protected String confirmationTitle;

        protected CustomRemoveAction(String id, Dialogs dialogs) {
            super(id);
            this.dialogs = dialogs;
        }

        @Override
        public void actionPerform(Component component) {
            String messagesPackage = AppConfig.getMessagesPack();
            dialogs.createOptionDialog(Dialogs.MessageType.CONFIRMATION)
                    .withCaption(getConfirmationTitle(messagesPackage))
                    .withMessage(getConfirmationMessage(messagesPackage))
                    .withActions(
                            new DialogAction(DialogAction.Type.OK) {
                                @Override
                                public void actionPerform(Component component) {
                                    doRemove();
                                }
                            },
                            new DialogAction(DialogAction.Type.CANCEL)
                    )
                    .show();
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
}
