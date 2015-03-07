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

import gchisto.gui.utils.AbstractChartPanel;
import gchisto.utils.Refresher;
import gchisto.utils.WorkerThread;

/**
 *
 * @author tony
 */
abstract public class GCStatsChartPanel extends AbstractChartPanel {

    private class RefreshCallback extends AbstractRefresherCallback {

        public void refresh() {
            refreshDataset();
        }
    }

    private class UpdateCallback extends AbstractRefresherCallback {

        public void refresh() {
            updateDataset();
        }
    }
    final private Refresher refreshRefresher;
    final private Refresher updateRefresher;

    public void refresh() {
        refreshRefresher.possiblyRefresh();
    }

    public void update() {
        updateRefresher.possiblyRefresh();
    }

    abstract public void refreshDataset();

    abstract public void updateDataset();

    public GCStatsChartPanel(String title) {
        this(title, null);
    }

    public GCStatsChartPanel(String title, String unitName) {
        super(title, unitName);

        this.refreshRefresher = new Refresher(WorkerThread.instance(), new RefreshCallback());
        this.updateRefresher = new Refresher(WorkerThread.instance(), new UpdateCallback());
    }
}
