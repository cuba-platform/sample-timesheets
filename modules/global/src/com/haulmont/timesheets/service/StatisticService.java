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

package com.haulmont.timesheets.service;

import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.Task;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author degtyarjov
 */
public interface StatisticService {
    String NAME = "ts_StatisticService";

    Map<Task, BigDecimal> getStatisticsByTasks(Date start, Date end, Project project);

    Map<Integer, Map<String, Object>> getStatisticsByProjects(Date start, Date end);
}