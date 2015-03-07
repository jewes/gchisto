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
package gchisto.gui.panels.gctimeline;

import gchisto.jfreechart.extensions.ChartLocker;
import gchisto.gcactivity.GCActivity;
import gchisto.gcactivity.GCActivitySet;
import gchisto.gctrace.GCTrace;
import gchisto.gctrace.GCTraceCheckpoint;
import gchisto.gctrace.GCTraceListener;
import gchisto.gctrace.RCWithGCTraceCheckpoint;
import gchisto.gctrace.RCWithGCTraceCheckpointCallback;
import gchisto.gui.utils.AbstractChartPanel;
import gchisto.gui.utils.GroupActivatingPanel;
import gchisto.utils.Refresher;
import gchisto.utils.WorkerThread;
import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 * A panel that contains a bar chart that shows the values of a metric over
 * application elapsed time. Each such panel will be added to the tabbed pane
 * of the main GC timeline panel.
 *
 * @author Tony Printezis
 */
public class ChartPanel extends AbstractChartPanel
        implements GCTraceListener, RCWithGCTraceCheckpointCallback {

    final private Dataset dataset;
    final private Refresher refresher;
    final private ChartLocker locker = new ChartLocker();
    final private GCTraceCheckpoint checkpoint;
    private GroupActivatingPanel groupActivatingTable;

    /**
     * It creates a chart for the given dataset and adds the chart to the panel.
     *
     * @param dataset The dataset that will provide the values for the chart.
     */
    private void addChart() {
        JFreeChart chart = ChartFactory.createXYBarChart(getTitle(),
                "Elapsed Time (sec)", false, "Time" + unitSuffix(),
                dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.addProgressListener(locker);
        XYPlot plot = (XYPlot) chart.getPlot();
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setToolTipGenerator(dataset);

        groupActivatingTable = new GroupActivatingPanel(dataset, locker);

        org.jfree.chart.ChartPanel chartPanel =
                new org.jfree.chart.ChartPanel(chart);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                groupActivatingTable, chartPanel);
        splitPane.setDividerLocation(200);
        mainPanel().add(BorderLayout.CENTER, splitPane);
    }

    public void refresh(final GCTraceCheckpoint checkpoint) {
        locker.doWhileLocked(new Runnable() {

            public void run() {
                dataset.datasetChanged();
            }
        });
    }

    public void possiblyRefresh() {
        refresher.possiblyRefresh();
    }

    public void gcActivityAdded(
            GCTrace gcTrace,
            GCActivitySet gcActivitySet,
            GCActivity gcActivity) {
        possiblyRefresh();
    }

    public void gcActivityNameAdded(
            final GCTrace gcTrace,
            final int id,
            final String gcActivityName) {
        locker.doWhileLocked(new Runnable() {

            public void run() {
                dataset.addGroup(id, gcActivityName);
                groupActivatingTable.groupAdded();
                checkpoint.extend(id);
            }
        });
        possiblyRefresh();
    }

    /**
     * It creates a new instance of this panel and adds a chart into it.
     *
     * @param title The name of the metric.
     * @param unitName The name of the unit of the metric.
     * @param dataset The dataset that will provide the values for the chart.
     */
    public ChartPanel(
            String title, String unitName,
            Dataset dataset,
            GCTraceCheckpoint checkpoint) {
        super(title, unitName);

        this.dataset = dataset;
        this.refresher = new Refresher(
                WorkerThread.instance(),
                new RCWithGCTraceCheckpoint(checkpoint, locker, this));
        this.checkpoint = checkpoint;

        addChart();
    }
}
