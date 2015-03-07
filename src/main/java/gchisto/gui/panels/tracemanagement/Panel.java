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
package gchisto.gui.panels.tracemanagement;

import gchisto.gctrace.GCTrace;
import gchisto.gctracegenerator.GCTraceGenerator;
import gchisto.gctracegenerator.GCTraceGeneratorForFiles;
import gchisto.gctracegenerator.GCTraceGeneratorListener;
import gchisto.gctracegenerator.GCTraceGeneratorSet;
import gchisto.gctrace.GCTraceSet;
import gchisto.gctrace.GCTraceSetListener;
import gchisto.gui.panels.TraceManagementPanel;
import gchisto.gui.utils.GUIUtilities;
import gchisto.utils.errorchecking.ArgumentChecking;
import gchisto.utils.errorchecking.ShouldNotReachHereException;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;

/**
 * A panel that allows the user to manage the GC traces loaded in the
 * system (i.e., load a new GC trace, rename, reload, delete, or change
 * the order of loaded traces).
 *
 * @author Tony Printezis
 */
public class Panel extends javax.swing.JPanel
        implements TraceManagementPanel {
    
    /**
     * The column header names
     *
     * @see Model
     */
    static private String[] COLUMN_NAMES = {
        "Name", "Time Added"
    };
    
    /**
     * The classes of the cells in each column.
     *
     * @see Model
     */
    static private Class[] COLUMN_CLASSES = {
        String.class, String.class
    };
    
    /**
     * Whether the cells in each column are editable.
     *
     * @see Model
     */
    static private boolean[] COLUMN_EDITABLE = {
        true, false
    };
    
    /**
     * The GC trace set that this panel will work on.
     */
    private GCTraceSet gcTraceSet;
    
    /**
     * The trace reader that will be used to read GC trace files. We should
     * really not hardcode this.
     *
     * TODO
     */
    final private GCTraceGeneratorSet gcTraceGeneratorSet = new GCTraceGeneratorSet();
    
    /**
     * The model for this table.
     */
    final private Model model = new Model();
    
    /**
     * The GC trace set setListener of this panel.
     */
    final private GCTraceSetListener setListener = new SetListener();
    
    /**
     * The GC trace set listener for this panel.
     */
    private class SetListener implements GCTraceSetListener {
        public void gcTraceAdded(GCTrace gcTrace)     {
            callTableChanged();
            setSelectedTrace(gcTrace);
        }
        public void gcTraceRenamed(GCTrace gcTrace)   {
            callTableChanged();
            setSelectedTrace(gcTrace);
        }
        public void gcTraceRemoved(GCTrace gcTrace)   {
            callTableChanged();
        }
        public void gcTraceMovedUp(GCTrace gcTrace)   {
            callTableChanged();
            setSelectedTrace(gcTrace);
        }
        public void gcTraceMovedDown(GCTrace gcTrace) {
            callTableChanged();
            setSelectedTrace(gcTrace);
        }
    }
    
    /**
     * The setListener which will be notified when the table selection changes.
     */
    private class SelectionListener implements ListSelectionListener {
        /**
         * It is called when the table selection changes.
         *
         * @param e The liste selection event associated with this
         * selection change.
         */
        public void valueChanged(ListSelectionEvent e) {
            selectionChanged();
        }
    }
    
    /**
     * The model for the table. The values for the cells it returns are the
     * following:
     *
     * <ul>
     * <li>column 0 (<tt>String</tt>): the name of the trace
     * <li>column 1 (<tt>String</tt>): the date/time the trace was read
     *</ul>
     */
    private class Model extends AbstractTableModel {
        
        /**
         * It returns the number of rows in the table.
         *
         * @return The number of rows in the table.
         */
        public int getRowCount() {
            return gcTraceSet.size();
        }
        
        /**
         * It returns the number of columns in the table.
         *
         * @return The number of columns in the table.
         */
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }
        
        /**
         * It returns the column name for the specified column.
         *
         * @param columnIndex The index of the column whose header will
         * be returned.
         * @return The column name for the specified column.
         */
        public String getColumnName(int columnIndex) {
            return COLUMN_NAMES[columnIndex];
        }
        
        /**
         * It returns the class of the table cells in the specified column.
         *
         * @param columnIndex The index of the column whose content class
         * will be returned.
         * @return The class of the table cells in the specified column.
         */
        public Class<?> getColumnClass(int columnIndex) {
            return COLUMN_CLASSES[columnIndex];
        }
        
        /**
         * It determines whether the specified table cell is editable.
         *
         * @param rowIndex The row index of the specified table cell.
         * @param columnIndex The column index of the specified table cell.
         * @return Whether the specified table cell is editable.
         */
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return COLUMN_EDITABLE[columnIndex];
        }
        
        /**
         * It returns the value of the specified table cell.
         *
         * @param rowIndex The row index of the specified table cell.
         * @param columnIndex The column index of the specified table cell.
         * @return The value of the specified table cell.
         */
        public Object getValueAt(int rowIndex, int columnIndex) {
            GCTrace gcTrace = gcTraceSet.findGCTrace(rowIndex);
            switch (columnIndex) {
                case 0:
                    return gcTrace.getName();
                case 1:
                    return gcTrace.getAddedDate().toString();
                default:
                    throw new ShouldNotReachHereException();
            }
        }
        
        /**
         * It sets a new value for a particular table cell.
         *
         * @param value The new value for the specified table cell.
         * @param rowIndex The row index of the specified table cell.
         * @param columnIndex The column index of the specified table cell.
         */
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            assert columnIndex == 0;
            assert COLUMN_EDITABLE[columnIndex];
            assert value instanceof String;
            
            String newName = (String) value;
            renameTrace(rowIndex, newName);
        }
        
    }
    
    /**
     * It calls the table changed callback on all the table model listeners.
     */
    private void callTableChanged() {
        model.fireTableDataChanged();
        table.clearSelection();
    }
    
    /**
     * It returns the table row that is currently selected.
     *
     * @return The table row that is currently selected.
     */
    private int getSelectedRow() {
        return table.getSelectedRow();
    }
    
    /**
     * It selects the given table row.
     *
     * @param index The index of the table row that should be selected.
     */
    private void setSelectedRow(int index) {
        assert 0 <= index && index < gcTraceSet.size();
        
        table.getSelectionModel().setSelectionInterval(index, index);
    }
    
    /**
     * It selects the table row that corresponds to the given GC trace.
     *
     * @param gcTrace The GC trace whose corresponding row will
     * be selected.
     */
    private void setSelectedTrace(GCTrace gcTrace) {
        int index = gcTraceSet.findGCTraceIndex(gcTrace.getName());
        assert 0 <= index && index < gcTraceSet.size();
        
        setSelectedRow(index);
    }
    
    /**
     * It is called when the table selection changes.
     */
    private void selectionChanged() {
        int selectedRowIndex = getSelectedRow();
        if (selectedRowIndex != -1) {
            assert 0 <= selectedRowIndex && selectedRowIndex < gcTraceSet.size();
            
            GCTrace gcTrace = gcTraceSet.findGCTrace(selectedRowIndex);
            updateTraceInfo(gcTrace);
        } else {
            updateTraceInfo(null);
        }
    }
    
    /**
     * It forces the selected table row to be edited.
     */
    private void forceSelectedTraceEdit() {
        int selectedRowIndex = getSelectedRow();
        if (selectedRowIndex > -1) {
            assert 0 <= selectedRowIndex && selectedRowIndex < gcTraceSet.size();
            
            table.editCellAt(selectedRowIndex, 0);
        }
    }
    
    /**
     * It updates the text area that contains information about the
     * given GC trace.
     *
     * @param gcTrace The GC trace for which the trace information
     * text area will be updated.
     */
    private void updateTraceInfo(GCTrace gcTrace) {
        String str;
        if (gcTrace != null) {
            str = gcTrace.getInfoString();
        } else {
            str = "";
        }
        traceInfoTextArea.setText(str);
    }
    
    private class AddGCTraceListener implements GCTraceGeneratorListener {
        public void started() {
        }

        public void finished(GCTrace gcTrace) {
            assert gcTrace != null;
            gcTraceSet.addGCTrace(gcTrace);
        }

        public void failed() {
        }
    }
    
    /**
     * TODO
     */
    public void addGCTrace() {
        int index = generatorMenu.getSelectedIndex();
        assert 0 <= index && index < gcTraceGeneratorSet.size();
        GCTraceGenerator gcTraceGenerator = gcTraceGeneratorSet.get(index);
        
        gcTraceGenerator.createNewGCTrace(this, new AddGCTraceListener());
    }
    
    /**
     * TODO
     */
    public void addGCTrace(File file) {
        GCTraceGeneratorForFiles gcTraceGeneratorForFiles = 
                gcTraceGeneratorSet.gcTraceGeneratorForFiles();
        
        gcTraceGeneratorForFiles.createNewGCTrace(file, new AddGCTraceListener());
    }
    
    /**
     * It renames the given GC trace.
     *
     * @param index The index of the GC trace to be renamed.
     * @param newName The new name of the GC trace.
     */
    private void renameTrace(int index, String newName) {
        ArgumentChecking.withinBounds(index, 0, gcTraceSet.size() - 1, "index");
        ArgumentChecking.notNull(newName, "newName");
        
        String traceName = gcTraceSet.findGCTrace(index).getName();
        gcTraceSet.rename(traceName, newName);
    }
    
    /**
     * It removes the currently selected GC trace.
     */
    private void removeGCTrace() {
        int index = getSelectedRow();
        if (index > -1) {
            assert 0 <= index && index < gcTraceSet.size();
            
            GCTrace gcTrace = gcTraceSet.findGCTrace(index);
            gcTraceSet.remove(gcTrace.getName());
            if (gcTraceSet.size() > 0) {
                if (index < gcTraceSet.size()) {
                    setSelectedRow(index);
                } else {
                    assert index == gcTraceSet.size();
                    setSelectedRow(index - 1);
                }
            }
        }
    }
    
    /**
     * It moves the currently selected GC trace up in the GC trace order.
     */
    private void moveUpTrace() {
        int index = getSelectedRow();
        if (index > -1) {
            assert 0 <= index && index < gcTraceSet.size();
            
            GCTrace gcTrace = gcTraceSet.findGCTrace(index);
            gcTraceSet.moveUp(gcTrace.getName());
        }
    }
    
    /**
     * It moves the currently selected GC trace down in the GC trace order.
     */
    private void moveDownTrace() {
        int index = getSelectedRow();
        if (index > -1) {
            assert 0 <= index && index < gcTraceSet.size();
            
            GCTrace gcTrace = gcTraceSet.findGCTrace(index);
            gcTraceSet.moveDown(gcTrace.getName());
        }
    }
    
    private void setComponentsEnabled(boolean enabled) {
        addButton.setEnabled(enabled);
        renameButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
        moveUpButton.setEnabled(enabled);
        moveDownButton.setEnabled(enabled);
        
        table.setEnabled(enabled);
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public String getPanelName() {
        return "Trace Management";
    }
    
    public GCTraceSetListener getListener() {
        return setListener;
    }
    
    public void setGCTraceSet(GCTraceSet gcTraceSet) {
        ArgumentChecking.notNull(gcTraceSet, "gcTraceSet");
        
        this.gcTraceSet = gcTraceSet;
        
        table.setModel(model);
    }
    
    /**
     * It creates a new instance of this panel.
     */
    public Panel() {
        initComponents();
        
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        ListSelectionModel selectionModel =
                table.getSelectionModel();
        selectionModel.addListSelectionListener(new SelectionListener());
        
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        GUIUtilities.setTableHeader(header);
        
        for (int i = 0; i < gcTraceGeneratorSet.size(); ++i) {
            GCTraceGenerator gcTraceGenerator = gcTraceGeneratorSet.get(i);
            String type = " " + gcTraceGenerator.getGCTraceType() + " ";
            generatorMenu.addItem(type);
            
            assert type.equals(generatorMenu.getItemAt(i));
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        traceInfoTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        addButton = new javax.swing.JButton();
        renameButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();
        generatorMenu = new javax.swing.JComboBox();
        forceGCButton = new javax.swing.JButton();

        jSplitPane1.setDividerLocation(400);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Traces");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(table);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel2);

        traceInfoTextArea.setColumns(20);
        traceInfoTextArea.setEditable(false);
        traceInfoTextArea.setRows(5);
        jScrollPane2.setViewportView(traceInfoTextArea);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Selected Trace Info");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel1);

        addButton.setText("Add...");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        renameButton.setText("Rename");
        renameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameButtonActionPerformed(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        moveUpButton.setText("Move Up");
        moveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpButtonActionPerformed(evt);
            }
        });

        moveDownButton.setText("Move Down");
        moveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownButtonActionPerformed(evt);
            }
        });

        forceGCButton.setText("Force GC");
        forceGCButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forceGCButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(generatorMenu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(addButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(renameButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(moveUpButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(moveDownButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 204, Short.MAX_VALUE)
                        .add(forceGCButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(generatorMenu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(addButton)
                    .add(renameButton)
                    .add(removeButton)
                    .add(moveUpButton)
                    .add(moveDownButton)
                    .add(forceGCButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void renameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameButtonActionPerformed
// TODO add your handling code here:
        forceSelectedTraceEdit();
    }//GEN-LAST:event_renameButtonActionPerformed
    
    private void moveDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownButtonActionPerformed
// TODO add your handling code here:
        moveDownTrace();
    }//GEN-LAST:event_moveDownButtonActionPerformed
    
    private void moveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpButtonActionPerformed
// TODO add your handling code here:
        moveUpTrace();
    }//GEN-LAST:event_moveUpButtonActionPerformed
    
    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
// TODO add your handling code here:
        removeGCTrace();
    }//GEN-LAST:event_removeButtonActionPerformed
    
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
// TODO add your handling code here:
        addGCTrace();
    }//GEN-LAST:event_addButtonActionPerformed

    private void forceGCButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forceGCButtonActionPerformed
        // TODO add your handling code here:
        System.gc();
}//GEN-LAST:event_forceGCButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton forceGCButton;
    private javax.swing.JComboBox generatorMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveUpButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton renameButton;
    private javax.swing.JTable table;
    private javax.swing.JTextArea traceInfoTextArea;
    // End of variables declaration//GEN-END:variables
    
}
