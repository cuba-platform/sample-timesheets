
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
