/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.calendar;

import com.haulmont.timesheets.entity.Holiday;
import com.vaadin.ui.components.calendar.event.BasicEvent;

import java.util.Date;

/**
 * @author gorelov
 * @version $Id$
 */
public class HolidayCalendarEventAdapter extends BasicEvent {

    protected Holiday holiday;

    public HolidayCalendarEventAdapter(Holiday holiday) {
        this.holiday = holiday;
    }

    public Holiday getHoliday() {
        return holiday;
    }

    public void setHoliday(Holiday holiday) {
        this.holiday = holiday;
    }

    @Override
    public String getCaption() {
        return holiday.getName();
    }

    @Override
    public String getDescription() {
        return holiday.getDescription();
    }

    @Override
    public Date getStart() {
        return holiday.getStartDate();
    }

    @Override
    public Date getEnd() {
        return holiday.getEndDate();
    }

    @Override
    public boolean isAllDay() {
        return true;
    }

    @Override
    public String getStyleName() {
        return "holiday";
    }
}
