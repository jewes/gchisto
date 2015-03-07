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

import gchisto.utils.Locker;
import java.awt.BorderLayout;

/**
 *
 * @author tony
 */
public class AllStatsTablePanelMulti extends GCStatsChartPanel {

    private AllStatsTableMulti panel;

    /**
     * It adds the appropriate table to this panel. The table is implemented
     * differently, depending whether there is a a single loaded GC trace, or
     * multiple ones. In the former case the table only contains the values
     * in the latter case the table contains the values, as well as comparative
     * stats between the GC traces.
     *
     * @param datasetGenerator The datasetGenerator generator of this package which
     * will provide all the values and metadata for the table.
     * @param single It determines whether there is one GC trace loaded,
     * or multiple ones.
     */
    private void addTable(DatasetGenerator datasetGenerator,
            Locker locker) {
        panel = new AllStatsTableMulti(datasetGenerator, locker);
        mainPanel().add(BorderLayout.CENTER, panel);
    }

    public void refreshDataset() {
        panel.refresh();
    }

    public void updateDataset() {
        panel.update();
    }

    /**
     * It creates a new instance of this panel and adds the table into it.
     *
     * @param datasetGenerator The datasetGenerator generator of this package which
     * will provide all the values and metadata for the table.
     * @param single It determines whether there is one GC trace loaded,
     * or multiple ones.
     */
    public AllStatsTablePanelMulti(
            DatasetGenerator datasetGenerator,
            Locker locker) {
        super("All GC Stats");

        addTable(datasetGenerator, locker);
    }
}
