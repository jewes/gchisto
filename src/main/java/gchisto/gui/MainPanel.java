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
package gchisto.gui;

import gchisto.gctrace.GCTraceSet;
import gchisto.gui.panels.TraceManagementPanel;
import gchisto.gui.panels.VisualizationPanel;
import gchisto.gui.utils.StatusLabelManager;
import gchisto.utils.MessageReporter;
import gchisto.utils.errorchecking.ArgumentChecking;
import gchisto.utils.errorchecking.ErrorReporting;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class MainPanel extends javax.swing.JPanel {

    /**
     * The index of the trace management panel in the <tt>panels</tt> list.
     *
     * @see #panels
     */
    static private final int TRACE_MANAGEMENT_PANEL_INDEX = 0;
    /**
     * An array that contains the full names of the classes that represent
     * the panels that will be added to the main frame. The only restriction
     * is that these classes should implement the <tt>VisualizationPanel</tt>
     * interface and the class at index <tt>TRACE_MANAGEMENT_PANEL_INDEX</tt>
     * should implement the <tt>TraceManagementPanel</tt> interface.
     *
     * @see gchisto.gui.panels.VisualizationPanel
     * @see gchisto.gui.panels.TraceManagementPanel
     */
    static private final String[] PANEL_CLASS_NAMES = {
        "gchisto.gui.panels.tracemanagement.Panel",

        "gchisto.gui.panels.gcstats.Panel",
        "gchisto.gui.panels.gcdistribution.Panel",
        "gchisto.gui.panels.gctimeline.Panel"

//        "gchisto.gui.panels.gcdata.Panel",
    };
    /**
     * The main GC trace set of the application.
     */
    final private GCTraceSet gcTraceSet = new GCTraceSet();
    /**
     * A linked list that contains the panels of the main frame.
     */
    final private List<VisualizationPanel> panels = new LinkedList<VisualizationPanel>();
    /**
     * The trace management panel.
     */
    private TraceManagementPanel traceManagementPanel;

    /**
     * It attempts to load a GC trace with the given file name.
     *
     * @param fileName The file name of the GC trace to be loaded.
     *
     * TODO
     */
    public void loadGCTraces(String[] fileNames) {
        ArgumentChecking.notNull(fileNames, "fileNames");

        for (int i = 0; i < fileNames.length; ++i) {
            loadGCTrace(fileNames[i]);
        }
    }

    /**
     * TODO
     */
    public void loadGCTrace(String fileName) {
        ArgumentChecking.notNull(fileName, "fileName");

        traceManagementPanel.addGCTrace(new File(fileName));
    }

    /**
     * TODO
     */
    public void loadGCTrace() {
        traceManagementPanel.addGCTrace();
    }

    /**
     * It instantiates and sets up the visualization panels and adds them
     * to the tabbed pane of the frame.
     *
     * @see #PANEL_CLASS_NAMES
     */
    private void setupPanels() {
        for (int i = 0; i < PANEL_CLASS_NAMES.length; ++i) {
            String className = PANEL_CLASS_NAMES[i];
            try {
                Class c = Class.forName(className);
                Object p = c.newInstance();
                VisualizationPanel panel = (VisualizationPanel) p;
                panel.setGCTraceSet(gcTraceSet);
                gcTraceSet.addListener(panel.getListener());
                panels.add(panel);
                tabbedPane.addTab(panel.getPanelName(), panel.getPanel());
            } catch (ClassNotFoundException e) {
                ErrorReporting.warning(className + " not found");
            } catch (InstantiationException e) {
                ErrorReporting.warning("could not instantiate " + className);
            } catch (IllegalAccessException e) {
                ErrorReporting.warning("could not access constructor of " + className);
            } catch (ClassCastException e) {
                ErrorReporting.warning("could not cast " + className + " to VisualizationPanel");
            }
        }
        ErrorReporting.fatalError(panels.size() > TRACE_MANAGEMENT_PANEL_INDEX,
                "There must be at least one panel set up, " +
                "the trace management panel.");
        try {
            traceManagementPanel =
                    (TraceManagementPanel) panels.get(TRACE_MANAGEMENT_PANEL_INDEX);
        } catch (ClassCastException e) {
            ErrorReporting.fatalError("could not cast panel with index " +
                    TRACE_MANAGEMENT_PANEL_INDEX + " to TraceManagementPanel");
        }
        ErrorReporting.fatalError(traceManagementPanel != null,
                "The trace management panel should not be null");
    }

    public MainPanel() {
        initComponents();
        setupPanels();

        MessageReporter.setStatusLabel(new StatusLabelManager(statusLabel));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        tabbedPane = new javax.swing.JTabbedPane();
        statusLabel = new javax.swing.JLabel();

        statusLabel.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusLabel))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel statusLabel;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
