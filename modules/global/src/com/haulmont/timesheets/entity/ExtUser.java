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

package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.security.entity.User;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.text.MessageFormat;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author gorelov
 */
@NamePattern("#getCaption|firstName,lastName,login,name")
@Entity(name = "ts$ExtUser")
@Extends(User.class)
public class ExtUser extends User {

    private static final long serialVersionUID = 4909893210504413352L;

    @Column(name = "WORK_HOURS_FOR_WEEK", nullable = false)
    protected BigDecimal workHoursForWeek;

    @OneToMany(mappedBy = "user")
    private List<TimeEntry> timeEntries;

    public BigDecimal getWorkHoursForWeek() {
        return workHoursForWeek;
    }

    public void setWorkHoursForWeek(BigDecimal workHoursForWeek) {
        this.workHoursForWeek = workHoursForWeek;
    }

    @Override
    public String getCaption() {
        if (StringUtils.isNotEmpty(firstName) || StringUtils.isNotEmpty(lastName)) {
            String pattern = "{0} {1}";
            MessageFormat fmt = new MessageFormat(pattern);
            return StringUtils.trimToEmpty(fmt.format(new Object[]{
                    StringUtils.trimToEmpty(firstName),
                    StringUtils.trimToEmpty(lastName)
            }));
        }
        return super.getCaption();
    }

    public List<TimeEntry> getTimeEntries() {
        return timeEntries;
    }

    public void setTimeEntries(List<TimeEntry> timeEntries) {
        this.timeEntries = timeEntries;
    }
}