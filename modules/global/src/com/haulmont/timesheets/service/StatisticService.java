/*
 * Copyright (c) 2015 com.haulmont.timesheets.service
 */
package com.haulmont.timesheets.service;

import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.Task;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author degtyarjov
 */
public interface StatisticService {
    String NAME = "ts_StatisticService";

    public Map<Task, BigDecimal> getStatisticsByTasks(Date start, Date end, Project project);

    public Map<Integer, Map<String, Object>> getStatisticsByProjects(Date start, Date end);
}