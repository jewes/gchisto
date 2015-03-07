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

import gchisto.gui.utils.GUIUtilities;
import gchisto.utils.Formatter;
import gchisto.utils.Locker;
import gchisto.utils.errorchecking.ArgumentChecking;
import gchisto.utils.errorchecking.ShouldNotReachHereException;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import org.jfree.data.category.CategoryDataset;

/**
 * A table that shows summary GC statistics for the loaded GC trace,
 * either in text format or in charts. It can only handle a single GC trace
 * and it assumes that there's only one GC trace loaded.
 *
 * @author Tony Printezis
 */
public class AllStatsTableSingle extends javax.swing.JPanel {

    /**
     * The default renderer for this table. It assumes that the model will
     * actually return <tt>JLabel</tt>s as the table values, so it simply
     * returns the same value as is.
     */
    private class Renderer implements TableCellRenderer {

        /**
         * It returns a label for a particular table cell.
         *
         * @param table The table in which the label will be included.
         * @param value The label to be returned.
         * @param isSelected Whether the label is selected or not; currently
         * this is ignored.
         * @param hasFocus Whether the label has focus or not; currently
         * this is ignored.
         * @param row The row in which the label will be added.
         * @param col The column in which the label will be added.
         * @return The label of a particular table cell.
         */
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            assert value instanceof JLabel;

            return (JLabel) value;
        }
    }
    final private Model model;
    final private Locker locker;

    /**
     * The model for this table.
     */
    private class Model extends AbstractTableModel {

        final private DatasetGenerator datasetGenerator;
        /**
         * The number of rows in the table.
         */
        private int rows;
        /**
         * The number of columns in the table.
         */
        final private int columns = 1 + DatasetGenerator.METRIC_LENGTH;
        /**
         * The labels for the table cells. The indexing is labels[row][column].
         */
        private JLabel[][] labels;
        /**
         * A list containing the column names.
         */
        final private String[] columnNames = new String[columns];
        final private CategoryDataset[] datasets = new CategoryDataset[columns];

        /**
         * The set of the table model listeners that are added to this model.
         */
        // final private TableModelListenerSet listeners = new TableModelListenerSet();
        /**
         * It returns the number of rows in the table.
         *
         * @return The number of rows in the table.
         */
        public int getRowCount() {
            return rows;
        }

        /**
         * It returns the number of columns in the table.
         *
         * @return The number of columns in the table.
         */
        public int getColumnCount() {
            return columns;
        }

        /**
         * It returns the name of the given column.
         *
         * @param columnIndex The index of the column whose name to be returned.
         * @return The name of the given column.
         */
        public String getColumnName(int columnIndex) {
            assert 0 <= columnIndex && columnIndex < columns;

            return columnNames[columnIndex];
        }

        /**
         * It returns the class of the items in a given column.
         *
         * @param columnIndex The index of the column.
         * @return The class of the items in a given column.
         */
        public Class<?> getColumnClass(int columnIndex) {
            assert 0 <= columnIndex && columnIndex < columns;

            return JLabel.class;
        }

        /**
         * It returns whether the given cell is editable. Right now, no cells
         * are editable.
         *
         * @param rowIndex The row of the cell.
         * @param columnIndex The column of the cell.
         * @return Whether the given cell is editable.
         */
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            assert 0 <= rowIndex && rowIndex < rows;
            assert 0 <= columnIndex && columnIndex < columns;

            return false;
        }

        /**
         * It returns the value of the given cell. Right now, the value is
         * actually the <tt>JLabel</tt> for that cell.
         *
         * @param rowIndex The row of the cell.
         * @param columnIndex The column of the cell.
         * @return The value of the given cell.
         */
        public Object getValueAt(int rowIndex, int columnIndex) {
            assert 0 <= rowIndex && rowIndex < rows;
            assert 0 <= columnIndex && columnIndex < columns;

            return labels[rowIndex][columnIndex];
        }

        /**
         * It sets the value of the given cell. Right now, we do not allow
         * cells to be editable, hence this should not be called.
         *
         * @param value The new value for the given cell.
         * @param rowIndex The row of the given cell.
         * @param columnIndex The column of the given cell.
         */
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            assert 0 <= rowIndex && rowIndex < rows;
            assert 0 <= columnIndex && columnIndex < columns;

            throw new ShouldNotReachHereException();
        }

        private int metricToColumn(int metric) {
            return 1 + metric - DatasetGenerator.METRIC_FIRST;
        }

        public void updateData() {
            for (int r = 0; r < rows; ++r) {
                for (int metric = DatasetGenerator.METRIC_FIRST;
                        metric <= DatasetGenerator.METRIC_LAST;
                        ++metric) {
                    int c = metricToColumn(metric);
                    Formatter formatter = DatasetGenerator.getFormatter(metric);
                    Number number = datasets[c].getValue(0, r);
                    String str = formatter.format(number);
                    labels[r][c].setText(str);
                }
            }
        }

        public void update() {
            rows = datasetGenerator.getGCActivityNum();

            labels = new JLabel[rows][columns];
            for (int r = 0; r < rows; ++r) {
                String gcActivityName = datasetGenerator.getGCActivityName(r);
                JLabel label = GUIUtilities.createJLabelForTable(gcActivityName);
                GUIUtilities.setTableHeader(label);
                labels[r][0] = label;

                for (int c = 1; c < columns; ++c) {
                    label = GUIUtilities.createJLabelForTable();
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                    labels[r][c] = label;
                }
            }
        }

        public Model(DatasetGenerator datasetGenerator) {
            this.datasetGenerator = datasetGenerator;

            columnNames[0] = "";
            for (int metric = DatasetGenerator.METRIC_FIRST;
                    metric <= DatasetGenerator.METRIC_LAST; ++metric) {
                String metricName =
                        DatasetGenerator.getMetricNameWithUnit(metric);

                int c = metricToColumn(metric);
                columnNames[c] = metricName;
                datasets[c] = datasetGenerator.newCategoryDataset(metric, false);
            }

            update();
        }
    }

    public void refresh() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                locker.doWhileLocked(new Runnable() {

                    public void run() {
                        model.updateData();
                        model.fireTableDataChanged();
                    }
                });
            }
        });
    }

    public void update() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                locker.doWhileLocked(new Runnable() {

                    public void run() {
                        model.update();
                        model.updateData();
                        model.fireTableStructureChanged();
                    }
                });
            }
        });
    }

    /**
     * It creates a new instance of this panel.
     *
     * @param datasetGenerator The dataset generator that will produce
     * the values for the table.
     */
    public AllStatsTableSingle(
            DatasetGenerator datasetGenerator,
            Locker locker) {
        ArgumentChecking.notNull(datasetGenerator, "datasetGenerator");

        initComponents();

        table.setDefaultRenderer(JLabel.class, new Renderer());
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        GUIUtilities.setTableHeader(header);

        model = new Model(datasetGenerator);
        table.setModel(model);

        this.locker = locker;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
