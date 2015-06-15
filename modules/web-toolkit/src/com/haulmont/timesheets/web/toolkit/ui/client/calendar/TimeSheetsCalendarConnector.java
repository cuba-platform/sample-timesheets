
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
