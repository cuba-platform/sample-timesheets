
package com.haulmont.timesheets.service;

import com.haulmont.timesheets.entity.TimeEntry;

import java.util.List;

/**
 * @author degtyarjov
 */
public interface CommandLineService {
    String NAME = "ts_CommandLineService";

    List<TimeEntry> createTimeEntriesForTheCommandLine(String commandLine);
}