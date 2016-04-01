
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

package com.haulmont.timesheets.web.calendar;

import com.haulmont.timesheets.entity.Holiday;
import com.vaadin.ui.components.calendar.event.BasicEvent;

import java.util.Date;

/**
 * @author gorelov
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
