/*
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 *
 */
package gchisto.gui.panels.gcstats;

import gchisto.jfreechart.extensions.ChangingCategoryDatasetWithTTG;
import gchisto.jfreechart.extensions.ChartLocker;
import java.awt.BorderLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;

/**
 * A panel that contains a bar chart that shows the values of a metric over
 * all loaded GC traces and al GC activities. Each such panel will be added
 * to the tabbed pane of the main GC stats panel.
 *
 * @author Tony Printezis
 */
public class MetricChartPanel extends GCStatsChartPanel {

    final private ChangingCategoryDatasetWithTTG dataset;
    final private ChartLocker locker;

    /**
     * It creates a chart for the given dataset and adds the chart to the panel.
     *
     * @param dataset The dataset that will provide the values for the chart.
     */
    private void addChart() {
        JFreeChart chart = ChartFactory.createBarChart3D(getTitle(),
                null, "Time" + unitSuffix(),
                dataset, PlotOrientation.VERTICAL,
                true, true, false);
        chart.addProgressListener(locker);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setToolTipGenerator(dataset);
        
        mainPanel().add(BorderLayout.CENTER, new ChartPanel(chart));
    }

    public void refreshDataset() {
        updateDataset();
    }

    public void updateDataset() {
        locker.doWhileLocked(new Runnable() {

            public void run() {
                dataset.datasetChanged();
            }
        });
    }

    /**
     * It creates a new instance of this panel and adds a chart into it.
     *
     * @param title The name of the metric.
     * @param unitName The name of the unit of the metric.
     * @param dataset The dataset that will provide the values for the chart.
     */
    public MetricChartPanel(
            String title, String unitName,
            ChangingCategoryDatasetWithTTG dataset,
            ChartLocker locker) {
        super(title, unitName);

        this.dataset = dataset;
        this.locker = locker;

        addChart();
    }
}
