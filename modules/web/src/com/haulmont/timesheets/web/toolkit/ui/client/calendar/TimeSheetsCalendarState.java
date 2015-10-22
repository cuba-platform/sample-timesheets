
package com.haulmont.timesheets.web.toolkit.ui.client.calendar;

import com.vaadin.shared.ui.calendar.CalendarState;

import java.util.HashSet;
import java.util.Set;

/**
 * @author gorelov
 */
public class TimeSheetsCalendarState extends CalendarState {
    public Set<Integer> weekends = new HashSet<>();
    public Set<String> holidays = new HashSet<>();
    public String moreMsgFormat = "";
}
