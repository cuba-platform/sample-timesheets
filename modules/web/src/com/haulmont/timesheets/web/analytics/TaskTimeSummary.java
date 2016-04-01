
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

package com.haulmont.timesheets.web.analytics;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;

import java.math.BigDecimal;

/**
 * @author degtyarjov
 */
@MetaClass(name = "ts$TaskTimeSummary")
public class TaskTimeSummary extends AbstractNotPersistentEntity {
    public TaskTimeSummary(String taskName, BigDecimal hoursSpentForTheTask) {
        this.taskName = taskName;
        this.hoursSpentForTheTask = hoursSpentForTheTask;
    }

    @MetaProperty
    protected String taskName;

    @MetaProperty
    protected BigDecimal hoursSpentForTheTask;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public BigDecimal getHoursSpentForTheTask() {
        return hoursSpentForTheTask;
    }

    public void setHoursSpentForTheTask(BigDecimal hoursSpentForTheTask) {
        this.hoursSpentForTheTask = hoursSpentForTheTask;
    }
}
