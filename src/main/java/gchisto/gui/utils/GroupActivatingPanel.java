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
package gchisto.gui.utils;

import gchisto.jfreechart.extensions.DatasetWithGroups;
import gchisto.utils.Locker;
import gchisto.utils.errorchecking.ShouldNotReachHereException;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * A panel that shows a series of group names and allows the user to active /
 * de-activate them. Currently, it is implemented as a <tt>JTable</tt>.
 * The callbacks when a name has been activated or de-activated are
 * done using the <tt>DatasetWithGroups</tt> interface. This can be used,
 * for example, to allow the user to show / hide groups of data in
 * charts. Such a group can be a JFreeChart series, an abstraction which
 * appears in a lot of the datasets. However, the interface is more general
 * to apply to other situations as well.
 *
 *
 * @author Tony Printezis
 * @see gchisto.jfreechart.extensions.DatasetWithGroups
 */
public class GroupActivatingPanel extends javax.swing.JPanel {

    /**
     * The column header names.
     *
     * @see Model
     */
    static private final String COLUMN_NAMES[] = {"Name", "Active"};
    /**
     * The classes of the cells in each column.
     *
     * @see Model
     */
    static private final Class COLUMN_CLASSES[] = {String.class, Boolean.class};
    /**
     * Whether the cells in each column are editable.
     *
     * @see Model
     */
    static private final boolean COLUMN_EDITABLE[] = {false, true};
    /**
     * The active dataset which will be notified when groups are activated
     * or de-activated.
     */
    final private DatasetWithGroups dataset;
    final private Locker locker;
    final private Model model;

    /**
     * A string renderer for the table. Right now, it's only used for the
     * row headers.
     */
    private class StringRenderer implements TableCellRenderer {

        /**
         * It returns a new label for a particular table cell.
         *
         * @param table The table in which the label will be included.
         * @param value A string representing the text for the label.
         * @param isSelected Whether the label is selected or not; currently
         * this is ignored.
         * @param hasFocus Whether the label has focus or not; currently
         * this is ignored.
         * @param row The row in which the label will be added.
         * @param col The column in which the label will be added.
         * @return A new label for a particular table cell.
         */
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int col) {
            assert col == 0;
            assert value instanceof String;

            String str = (String) value;
            JLabel label = GUIUtilities.createJLabelForTable(str);
            GUIUtilities.setTableHeader(label);
            return label;
        }
    }

    /**
     * A boolean renderer for the table. Right now, it is only used for the
     * check boxes which active / de-activate each group.
     */
    private class BooleanRenderer implements TableCellRenderer {

        /**
         * It returns a new check box for a particular table cell.
         *
         * @param table The table in which the check box will be included.
         * @param value A boolean representing whether the check box should
         * be selected or not.
         * @param isSelected Whether the check box is selected or not;
         * currently this is ignored.
         * @param hasFocus Whether the check box has focus or not; currently
         * this is ignored.
         * @param row The row in which the check box will be added.
         * @param col The column in which the check box will be added.
         * @return A new check box for a particular table cell.
         */
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int col) {
            assert col == 1 : "row =" + row + " col = " + col;
            assert value instanceof Boolean;

            boolean selected = ((Boolean) value).booleanValue();
            JCheckBox checkBox = GUIUtilities.createJCheckBoxForTable(selected);
            return checkBox;
        }
    }

    /**
     * The model for the table. The values for the cells it returns are the
     * following:
     * <ul>
     * <li> column 0 (<tt>String</tt>): the name of the row
     * <li> column 1 (<tt>Boolean</tt>): whether the corresponding name is
     * active or not
     * </ul>
     */
    private class Model extends AbstractTableModel {

        /**
         * It returns the number of rows in the table.
         *
         * @return The number of rows in the table.
         */
        public int getRowCount() {
            return dataset.getGroupCount();
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
            switch (columnIndex) {
                case 0:
                    return dataset.getGroupName(rowIndex);
                case 1:
                    return dataset.isGroupActive(rowIndex);
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
        public void setValueAt(Object value, final int rowIndex, int columnIndex) {
            assert columnIndex == 1;
            assert COLUMN_EDITABLE[columnIndex];
            assert value instanceof Boolean;

            final boolean active = ((Boolean) value).booleanValue();
            locker.doWhileLocked(new Runnable() {

                public void run() {
                    dataset.setGroupActive(rowIndex, active);
                }
            });
        }
    }

    public void groupAdded() {
        model.fireTableStructureChanged();
    }
    
    /**
     * It creates a new instance of the group activating / de-acticating panel.
     *
     * @param dataset The dataset that holds whether the groups are
     * activated / de-activated and that will be notified when changes
     * are made.
     */
    public GroupActivatingPanel(DatasetWithGroups dataset, Locker locker) {
        this.dataset = dataset;
        this.locker = locker;
        this.model = new Model();

        initComponents();

        table.setDefaultRenderer(String.class, new StringRenderer());
        table.setDefaultRenderer(Boolean.class, new BooleanRenderer());

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        GUIUtilities.setTableHeader(header);

        table.setModel(model);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        tableScrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

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
        tableScrollPane.setViewportView(table);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tableScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tableScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable table;
    private javax.swing.JScrollPane tableScrollPane;
    // End of variables declaration//GEN-END:variables
}
