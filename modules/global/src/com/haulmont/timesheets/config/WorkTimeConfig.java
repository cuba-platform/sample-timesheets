
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
import com.haulmont.cuba.core.config.type.*;
import com.haulmont.timesheets.core.BigDecimalTypeFactory;
import com.haulmont.timesheets.core.StringListTypeStringify;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author gorelov
 */
@Source(type = SourceType.DATABASE)
public interface WorkTimeConfig extends Config {

    @Property("timesheets.workHourForWeek")
    @Default("40")
    @Factory(factory = BigDecimalTypeFactory.class)
    BigDecimal getWorkHourForWeek();

    void setWorkHourForWeek(BigDecimal hours);

    @Property("timesheets.workDays")
    @Default("Mon|Tue|Wed|Thu|Fri")
    @Factory(factory = StringListTypeFactory.class)
    @Stringify(stringify = StringListTypeStringify.class)
    List<String> getWorkDays();

    void setWorkDays(List<String> workDays);

    @Property("timesheets.openPeriodStart")
    @Factory(factory = DateFactory.class)
    @Stringify(stringify = DateStringify.class)
    Date getOpenPeriodStart();
    void setOpenPeriodStart(Date date);
}
