
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
