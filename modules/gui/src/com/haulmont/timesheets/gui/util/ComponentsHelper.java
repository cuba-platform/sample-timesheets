
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

package com.haulmont.timesheets.gui.util;

import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.aggregation.AggregationStrategy;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.global.WorkdaysTools;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author gorelov
 */
public class ComponentsHelper {

    public static final String COMMON_DAY_CAPTION_STYLE = "%s %d";
    public static final String TODAY_CAPTION_STYLE = "<strong><span style=\"text-decoration: underline;\">%s</span></strong>";
    public static final String HOLIDAY_CAPTION_STYLE = "<font color=\"#EF525B\">%s</font>";

    protected static ComponentsFactory componentsFactory = AppBeans.get(ComponentsFactory.NAME);
    protected static Messages messages = AppBeans.get(Messages.NAME);
    protected static TimeSource timeSource = AppBeans.get(TimeSource.NAME);
    protected static WorkdaysTools workdaysTools = AppBeans.get(WorkdaysTools.NAME);

    public static FieldGroup.CustomFieldGenerator getCustomTextArea() {
        return (datasource, propertyId) -> {
            ResizableTextArea textArea = componentsFactory.createComponent(ResizableTextArea.class);
            textArea.setDatasource(datasource, propertyId);
            textArea.setHeight("100px");
            textArea.setResizable(true);
            return textArea;
        };
    }

    public static PickerField.LookupAction createLookupAction(PickerField pickerField) {
        PickerField.LookupAction lookupAction = new PickerField.LookupAction(pickerField);
        lookupAction.setLookupScreenOpenType(WindowManager.OpenType.DIALOG
                .width(800)
                .height(500)
                .resizable(true)
        );
        return lookupAction;
    }

    public static LinkButton createCaptionlessLinkButton(String icon, String description, Action action) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setIcon(icon);
        linkButton.setDescription(description);
        linkButton.setAlignment(Component.Alignment.MIDDLE_CENTER);
        linkButton.setAction(action);
        return linkButton;
    }

    public static AggregationInfo createAggregationInfo(MetaPropertyPath path, AggregationStrategy strategy) {
        AggregationInfo info = new AggregationInfo();
        info.setPropertyPath(path);
        info.setStrategy(strategy);
        return info;
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

    public static abstract class CustomRemoveAction extends AbstractAction {

        protected Frame frame;
        protected String confirmationMessage;
        protected String confirmationTitle;

        protected CustomRemoveAction(String id, Frame frame) {
            super(id);
            this.frame = frame;
        }

        @Override
        public void actionPerform(Component component) {
            final String messagesPackage = AppConfig.getMessagesPack();
            frame.showOptionDialog(
                    getConfirmationTitle(messagesPackage),
                    getConfirmationMessage(messagesPackage),
                    Frame.MessageType.CONFIRMATION,
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
            Task task = (Task) target.getSingleSelected();
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
            Task selected = (Task) target.getSingleSelected();
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
            case CLOSED:
                return "time-entry-closed";
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

    public static class EntityCodeGenerationListener<T extends Entity> implements Datasource.ItemPropertyChangeListener<T> {
        @Override
        public void itemPropertyChanged(Datasource.ItemPropertyChangeEvent<T> e) {
            if ("name".equalsIgnoreCase(e.getProperty()) && e.getItem().getMetaClass().getProperty("code") != null) {
                String codeValue = e.getItem().getValue("code");
                if (StringUtils.isBlank(codeValue)) {
                    String newName = String.valueOf(e.getValue());
                    String newCode = newName.toUpperCase().replaceAll(" ", "_");
                    e.getItem().setValue("code", newCode);
                }
            }
        }
    }
}
