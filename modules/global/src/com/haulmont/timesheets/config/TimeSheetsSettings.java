
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
