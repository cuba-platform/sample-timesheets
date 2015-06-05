/*
 * Copyright (c) 2015 com.haulmont.ts.entity
 */
package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.security.entity.User;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.text.MessageFormat;
import java.math.BigDecimal;

/**
 * @author gorelov
 */
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("Ext")
@NamePattern("#getCaption|firstName,lastName,login,name")
@Entity(name = "ts$ExtUser")
@Extends(User.class)
public class ExtUser extends User {

    private static final long serialVersionUID = 4909893210504413352L;

    @Column(name = "WORK_HOURS_FOR_WEEK", nullable = false)
    protected BigDecimal workHoursForWeek;
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
}