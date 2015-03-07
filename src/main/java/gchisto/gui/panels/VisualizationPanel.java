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
package gchisto.gui.panels;

import gchisto.gctrace.GCTraceSet;
import gchisto.gctrace.GCTraceSetListener;
import javax.swing.JPanel;

/**
 * This is the interface that all panels that can be added to the tabbed 
 * pane of the main frame need to implement.
 *
 * @author Tony Printezis
 * @see    gchisto.gui.MainFrame
 */
public interface VisualizationPanel {
    
    /**
     * It returns the panel that will be added to the tabbed pane of
     * the main frame.
     *
     * @return The panel that will be added to the tabbed pane of
     * the main frame.
     */
    public JPanel getPanel();
    
    /**
     * It returns the name of this panel.
     *
     * @return The name of this panel.
     */
    public String getPanelName();
    
    /**
     * It returns the GC trace set listener associated with this panel.
     *
     * @return The GC trace set listener associated with this panel.
     */
    public GCTraceSetListener getListener();
    
    /**
     * It installs the GC trace set of the application in the panel.
     *
     * @param gcTraceSet The GC trace set of the application.
     */
    public void setGCTraceSet(GCTraceSet gcTraceSet);
    
}
