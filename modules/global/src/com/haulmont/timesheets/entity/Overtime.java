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

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;
import com.haulmont.cuba.security.entity.User;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author degtyarjov
 * @version $Id$
 */
@MetaClass(name = "ts$Overtime")
public class Overtime extends AbstractNotPersistentEntity {
    @MetaProperty
    protected User user;
    @MetaProperty
    protected Date date;
    @MetaProperty
    protected BigDecimal overtimeInHours;

    public Overtime(User user, Date date, BigDecimal overtimeInHours) {
        this.user = user;
        this.date = date;
        this.overtimeInHours = overtimeInHours;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getOvertimeInHours() {
        return overtimeInHours;
    }

    public void setOvertimeInHours(BigDecimal overtimeInHours) {
        this.overtimeInHours = overtimeInHours;
    }
}
