
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

package com.haulmont.timesheets.web.toolkit.ui.client.calendar;

import com.haulmont.timesheets.web.toolkit.ui.TimeSheetsCalendar;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.calendar.CalendarConnector;
import com.vaadin.shared.ui.Connect;

/**
 * @author gorelov
 */
@Connect(value = TimeSheetsCalendar.class, loadStyle = Connect.LoadStyle.LAZY)
public class TimeSheetsCalendarConnector extends CalendarConnector {

    @Override
    public TimeSheetsCalendarWidget getWidget() {
        return (TimeSheetsCalendarWidget) super.getWidget();
    }

    @Override
    public TimeSheetsCalendarState getState() {
        return (TimeSheetsCalendarState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        getWidget().setWeekends(getState().weekends);
        getWidget().setHolidays(getState().holidays);
        getWidget().setMoreMsgFormat(getState().moreMsgFormat);
        super.onStateChanged(stateChangeEvent);
    }
}
