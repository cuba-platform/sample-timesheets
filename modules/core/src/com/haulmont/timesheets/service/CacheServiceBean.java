/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.service;

import com.haulmont.timesheets.core.HolidaysCacheAPI;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author degtyarjov
 * @version $Id$
 */
@Service(CacheService.NAME)
public class CacheServiceBean implements CacheService {
    @Inject
    protected HolidaysCacheAPI holidaysCacheAPI;

    @Override
    public void updateHolidaysCache() {
        holidaysCacheAPI.updateCache();
    }
}
