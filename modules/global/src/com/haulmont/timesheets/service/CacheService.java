/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.service;

/**
 * @author degtyarjov
 * @version $Id$
 */
public interface CacheService {
    String NAME = "ts_CacheService";

    void updateHolidaysCache();
}
