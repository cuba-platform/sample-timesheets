
package com.haulmont.timesheets.gui.util;

import com.haulmont.cuba.gui.components.Formatter;
import com.haulmont.timesheets.global.HoursAndMinutes;

/**
 * @author degtyarjov
 */
public class HoursAndMinutesFormatter implements Formatter<Integer> {
    @Override
    public String format(Integer value) {
        return new HoursAndMinutes(0, value != null ? value : 0).getFormattedCaption();
    }
}
