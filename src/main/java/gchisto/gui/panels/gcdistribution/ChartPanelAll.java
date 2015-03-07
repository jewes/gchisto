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
package gchisto.gui.panels.gcdistribution;

import gchisto.gui.utils.AbstractChartPanel;
import gchisto.gui.utils.GroupActivatingPanel;
import gchisto.jfreechart.extensions.XYDatasetWithGroups;
import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;

/**
 * A panel that contains a bar chart that compares the GC distribution 
 * of all GC traces. This panel will be added to the tabbed pane of the main
 * GC distribution panel.
 *
 * @author Tony Printezis
 */
public class ChartPanelAll extends AbstractChartPanel {

    /**
     * It creates a chart for the given dataset and adds the chart to the panel.
     *
     * @param dataset The dataset that will provide the values for the chart.
     */
    private void addChart(XYDatasetWithGroups dataset) {
        assert dataset != null;

        JFreeChart chart = ChartFactory.createXYAreaChart(getTitle(),
                "Buckets (sec)", "Count", dataset, PlotOrientation.VERTICAL,
                true, true, false);

        // null so that we can get it compiled; we'll get back to this...
        GroupActivatingPanel table = new GroupActivatingPanel(dataset, null);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                table, new org.jfree.chart.ChartPanel(chart));
        splitPane.setDividerLocation(200);
        mainPanel().add(BorderLayout.CENTER, splitPane);
    }

    /**
     * It creates a new instance of this panel and adds a chart into it.
     *
     * @param title The name of the metric.
     * @param unitName The name of the unit of the metric.
     * @param dataset The dataset that will provide the values for the chart.
     */
    public ChartPanelAll(
            String title, String unitName,
            XYDatasetWithGroups dataset) {
        super(title, unitName);
        addChart(dataset);
    }
}
