/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.toolkit.ui.client.calendar;

import com.vaadin.shared.ui.calendar.CalendarState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gorelov
 * @version $Id$
 */
public class TimeSheetsCalendarState extends CalendarState {

    public List<Integer> weekends = new ArrayList<>();
    public List<Date> holidays = new ArrayList<>();

}
