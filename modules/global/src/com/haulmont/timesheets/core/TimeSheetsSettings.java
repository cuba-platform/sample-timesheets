/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.core;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;
import com.haulmont.cuba.core.config.type.Factory;
import com.haulmont.cuba.core.config.type.UuidTypeFactory;

import java.util.UUID;

/**
 * @author gorelov
 * @version $Id$
 */
@Source(type = SourceType.APP)
public interface TimeSheetsSettings extends Config {

    @Property("timesheets.closerId")
    @Default("90e4249a-b3b9-d9e2-50ef-6f3aba50665e")
    @Factory(factory = UuidTypeFactory.class)
    UUID getCloserId();

    void setCloserId(UUID uuid);
}
