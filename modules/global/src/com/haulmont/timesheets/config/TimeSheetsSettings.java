
package com.haulmont.timesheets.config;

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
 */
@Source(type = SourceType.DATABASE)
public interface TimeSheetsSettings extends Config {

    @Property("timesheets.closerId")
    @Default("90e4249a-b3b9-d9e2-50ef-6f3aba50665e")
    @Factory(factory = UuidTypeFactory.class)
    UUID getCloserId();
    void setCloserId(UUID uuid);

    @Property("timesheets.defaultGroupId")
    @Default("0fa2b1a5-1d68-4d69-9fbd-dff348347f93")
    @Factory(factory = UuidTypeFactory.class)
    UUID getDefaultGroupId();
    void setDefaultGroupId(UUID id);
}
