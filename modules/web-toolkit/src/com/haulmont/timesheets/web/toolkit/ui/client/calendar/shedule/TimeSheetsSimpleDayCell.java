
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

package com.haulmont.timesheets.web.toolkit.ui.client.calendar.shedule;

import com.vaadin.client.ui.VCalendar;
import com.vaadin.client.ui.calendar.schedule.SimpleDayCell;

/**
 * @author gorelov
 */
public class TimeSheetsSimpleDayCell extends SimpleDayCell {

    protected String moreMsgFormat = "+ %s";

    public TimeSheetsSimpleDayCell(VCalendar calendar, int row, int cell) {
        super(calendar, row, cell);
    }

    @Override
    protected int getBottomspacerWidth() {
        return 6;
    }

    @Override
    protected String getMoreMsgFormat(int more) {
        return moreMsgFormat.replaceFirst("%s", more + "");
    }

    public void setMoreMsgFormat(String moreMsgFormat) {
        this.moreMsgFormat = moreMsgFormat;
    }
}
