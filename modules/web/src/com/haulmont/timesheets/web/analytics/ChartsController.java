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

import com.haulmont.charts.gui.amcharts.model.Graph;
import com.haulmont.charts.gui.amcharts.model.GraphType;
import com.haulmont.charts.gui.amcharts.model.charts.SerialChart;
import com.haulmont.charts.gui.amcharts.model.data.ListDataProvider;
import com.haulmont.charts.gui.amcharts.model.data.MapDataItem;
import com.haulmont.charts.gui.components.charts.Chart;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.service.StatisticService;
import org.apache.commons.lang.time.DateUtils;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author degtyarjov
 */
public class ChartsController extends AbstractWindow {
    @Inject
    protected CollectionDatasource<TaskTimeSummary, UUID> pieDs;
    @Inject
    protected DateField taskStart;
    @Inject
    protected DateField taskEnd;
    @Inject
    protected LookupField taskProject;
    @Inject
    protected Chart tasksChart;
    @Inject
    private DateField projectsStart;
    @Inject
    private DateField projectsEnd;
    @Inject
    protected Chart projectsChart;
    @Inject
    protected StatisticService statisticService;
    @Inject
    private TimeSource timeSource;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        pieDs.refresh();
        Date currentDate = timeSource.currentTimestamp();
        taskStart.setValue(DateUtils.addDays(currentDate, -30));
        taskEnd.setValue(currentDate);
        projectsStart.setValue(DateUtils.addDays(currentDate, -30));
        projectsEnd.setValue(currentDate);
        refreshTasks();
        refreshProjects();
    }

    public void refreshTasks() {
        pieDs.clear();
        Map<Task, BigDecimal> statistics = statisticService.getStatisticsByTasks(
                taskStart.getValue(), taskEnd.getValue(), taskProject.getValue());
        for (Map.Entry<Task, BigDecimal> entry : statistics.entrySet()) {
            Task task = entry.getKey();
            pieDs.addItem(new TaskTimeSummary("[" + task.getProject().getInstanceName() + "] " + task.getName(), entry.getValue()));
        }
        tasksChart.getConfiguration().getLegend().setEnabled(statistics.size() > 0);
        tasksChart.repaint();
    }

    public void refreshProjects() {
        Map<Integer, Map<String, Object>> statisticsByProjects = statisticService.getStatisticsByProjects(
                projectsStart.getValue(), projectsEnd.getValue());
        ListDataProvider dataProvider = new ListDataProvider();
        Set<String> allProjects = new LinkedHashSet<>();
        for (Map.Entry<Integer, Map<String, Object>> entry : statisticsByProjects.entrySet()) {
            MapDataItem mapDataItem = new MapDataItem();
            for (Map.Entry<String, Object> projectsWithSpentTime : entry.getValue().entrySet()) {
                mapDataItem.add(projectsWithSpentTime.getKey(), projectsWithSpentTime.getValue());

                if (!"week".equals(projectsWithSpentTime.getKey())) {
                    allProjects.add(projectsWithSpentTime.getKey());
                }
            }
            dataProvider.addItem(mapDataItem);
        }
        SerialChart projectsChartConfiguration = (SerialChart) projectsChart.getConfiguration();
        projectsChartConfiguration.setDataProvider(dataProvider);

        if (projectsChartConfiguration.getGraphs() != null) {
            projectsChartConfiguration.getGraphs().clear();
        }
        Graph[] graphs = new Graph[allProjects.size()];
        int i = 0;
        for (String project : allProjects) {
            Graph projectGraph = new Graph();
            projectGraph.setFillAlphas(0.5);
            projectGraph.setColumnWidth(0.4);
            projectGraph.setLineAlpha(0.7);
            projectGraph.setTitle(project);
            projectGraph.setType(GraphType.COLUMN);
            projectGraph.setValueField(project);
            projectGraph.setBalloonText(project + ": time spent [[" + project + "]] hrs");

            graphs[i] = projectGraph;
            i++;
        }
        projectsChartConfiguration.addGraphs(graphs);
        projectsChart.repaint();
    }
}