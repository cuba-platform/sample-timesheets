
package com.haulmont.timesheets.core;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;
import com.haulmont.cuba.core.config.type.Factory;
import com.haulmont.cuba.core.config.type.StringListTypeFactory;
import com.haulmont.cuba.core.config.type.Stringify;

import java.math.BigDecimal;
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
}
