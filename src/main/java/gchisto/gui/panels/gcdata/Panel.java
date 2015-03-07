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
package gchisto.gui.panels.gcdata;

import gchisto.gctrace.GCTrace;
import gchisto.gui.utils.TabbedPane;

/**
 *
 * @author tony
 */
public class Panel extends TabbedPane<DataPanel> {

    protected DataPanel newPanel(GCTrace gcTrace) {
        DataPanel panel = new DataPanel(gcTrace);
        gcTrace.addListener(panel);
        return panel;
    }

    protected void updatePanel(DataPanel panel) {
        panel.possiblyRefresh();
    }

    public String getPanelName() {
        return "GC Data";
    }
    
}
