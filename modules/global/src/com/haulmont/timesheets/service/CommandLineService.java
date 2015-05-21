/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.service;

import com.haulmont.timesheets.entity.TimeEntry;

import java.util.List;

/**
 * @author degtyarjov
 * @version $Id$
 */
public interface CommandLineService {
    String NAME = "ts_CommandLineService";

    List<TimeEntry> createTimeEntriesForTheCommandLine(String commandLine);
}
