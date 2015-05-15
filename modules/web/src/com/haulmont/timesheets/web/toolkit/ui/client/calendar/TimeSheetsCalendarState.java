/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.toolkit.ui.client.calendar;

import com.vaadin.shared.ui.calendar.CalendarState;

import java.util.*;

/**
 * @author gorelov
 * @version $Id$
 */
public class TimeSheetsCalendarState extends CalendarState {

    public List<Integer> weekends = new ArrayList<>();
    public Set<String> holidays = new HashSet<>();

}
