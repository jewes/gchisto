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
import gchisto.utils.Calculations;
import gchisto.utils.Comparisons;
import gchisto.utils.Formatter;
import gchisto.utils.Formatting;
import gchisto.utils.Locker;
import gchisto.utils.errorchecking.ArgumentChecking;
import gchisto.utils.errorchecking.ShouldNotReachHereException;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import org.jfree.data.category.CategoryDataset;

/**
 * A table that shows summary GC statistics for the loaded GC traces,
 * either in text format or in charts. It assumes that there are more than
 * one GC traces loaded and it also shows comparative metrics between the
 * trace at index 0 and the others.
 *
 * @author Tony Printezis
 */
public class AllStatsTableMulti extends javax.swing.JPanel {

    /**
     * When showing the percentage difference between two GC traces and for
     * a particular metric, we color the label that contains the percentage
     * difference according to whether it's a positive or a negative difference
     * and how high the difference is. This array contains the boundaries of
     * the (absolute values of the) percentage difference. The labels of
     * percentage differences that fall within two boundaries will share
     * colors.
     *
     *
     * @see #FG_POS_DIFF_COLORS
     * @see #FG_NEG_DIFF_COLORS
     * @see #BG_POS_DIFF_COLORS
     * @see #BG_NEG_DIFF_COLORS
     * @see #DIFF_BOLD_FONT
     */
    static final private double[] DIFF_BOUNDS = {
        0.0, 5.0
    };
    /**
     * The background colors for the labels that show percentage differences
     * within certain boundaries. The boundaries are defined in the
     * <tt>DIFF_BOUNDS</tt> array. These colors should be used when the
     * percentage difference is positive.
     *
     * @see #DIFF_BOUNDS
     * @see #FG_POS_DIFF_COLORS
     * @see #FG_NEG_DIFF_COLORS
     * @see #BG_NEG_DIFF_COLORS
     * @see #DIFF_BOLD_FONT
     */
    static final private Color BG_POS_DIFF_COLORS[] = {
        // new Color(255, 170, 170), new Color(235, 0, 0)
        GUIUtilities.TABLE_COMPONENT_BG_COLOR, Color.WHITE, Color.WHITE
    };
    /**
     * The background colors for the labels that show percentage differences
     * within certain boundaries. The boundaries are defined in the
     * <tt>DIFF_BOUNDS</tt> array. These colors should be used when the
     * percentage difference is negative.
     *
     * @see #DIFF_BOUNDS
     * @see #FG_POS_DIFF_COLORS
     * @see #FG_NEG_DIFF_COLORS
     * @see #BG_POS_DIFF_COLORS
     * @see #DIFF_BOLD_FONT
     */
    static final private Color BG_NEG_DIFF_COLORS[] = {
        // new Color(150, 255, 150), new Color(0, 170, 0)
        GUIUtilities.TABLE_COMPONENT_BG_COLOR, Color.WHITE, Color.WHITE
    };
    /**
     * The foreground colors for the labels that show percentage differences
     * within certain boundaries. The boundaries are defined in the
     * <tt>DIFF_BOUNDS</tt> array. These colors should be used when the
     * percentage difference is positive.
     *
     * @see #DIFF_BOUNDS
     * @see #FG_NEG_DIFF_COLORS
     * @see #BG_POS_DIFF_COLORS
     * @see #BG_NEG_DIFF_COLORS
     * @see #DIFF_BOLD_FONT
     */
    static final private Color FG_POS_DIFF_COLORS[] = {
        // Color.BLACK, Color.WHITE
        GUIUtilities.TABLE_COMPONENT_FG_COLOR, new Color(220, 0, 0), new Color(220, 0, 0)
    };
    /**
     * The foreground colors for the labels that show percentage differences
     * within certain boundaries. The boundaries are defined in the
     * <tt>DIFF_BOUNDS</tt> array. These colors should be used when the
     * percentage difference is negative.
     *
     * @see #DIFF_BOUNDS
     * @see #FG_POS_DIFF_COLORS
     * @see #BG_POS_DIFF_COLORS
     * @see #BG_NEG_DIFF_COLORS
     * @see #DIFF_BOLD_FONT
     */
    static final private Color FG_NEG_DIFF_COLORS[] = {
        // Color.BLACK, Color.WHITE
        GUIUtilities.TABLE_COMPONENT_FG_COLOR, new Color(0, 150, 0), new Color(0, 150, 0)
    };
    /**
     * Whether the font of the labels that show percentage differences
     * within certain boundaries should be bold or not. The boundaries
     * are defined in the <tt>DIFF_BOUNDS</tt> array.
     *
     * @see #DIFF_BOUNDS
     * @see #FG_POS_DIFF_COLORS
     * @see #BG_POS_DIFF_COLORS
     * @see #BG_NEG_DIFF_COLORS
     */
    static final private boolean DIFF_BOLD_FONT[] = {
        false, false, true
    };
    /**
     * The color of the table grid.
     */
    static final private Color GRID_COLOR = Color.GRAY;
    /**
     * The height of separator rows.
     */
    static final private int SEPARATOR_ROW_HEIGHT = 1;

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
         * The number of rows in the table. This is constructed incrementally
         * when the table is constructed, hence it cannot be <tt>final</tt>.
         */
        private int rows;
        /**
         * The number of columns in the table.
         */
        final private int columns = 2 + DatasetGenerator.METRIC_LENGTH;
        /**
         * A list of lists that creates a 2D data structure which holds the
         * labels that will be added to the cells of the able. The first-level
         * list is a list of rows, and the second-level lists are lists with
         * one entry per column. The table is constructed one row at a time.
         * So, the top-level list starts empty and, as each row is added,
         * the top-level list is expanded and a new row list is added to it.
         */
        private List<JLabel[]> labels;
        /**
         * A list that contains the indexes of rows that are considered to
         * be separator rows. These are rows that contain no values, are
         * shorter, are painted solid, and serve as horizontal separators.
         */
        private List<Integer> separatorRows;
        /**
         * A list containing the column names.
         */
        final private String[] columnNames = new String[columns];
        final private CategoryDataset[] datasets =
                new CategoryDataset[DatasetGenerator.METRIC_LENGTH];
        /**
         * Maps on which row the data for a particular GC activity / GC trace
         * tuple are held. This should be null for invalid GC activity / GC trace
         * tuples. The indexing is dataRows[GC activity][GC trace].
         */
        private JLabel[][][] dataRows;
        /**
         * Maps on which row the percentages (against the base GC trace) for a
         * particular GC activity / GC trace tuple are held. This should be null
         * for invalid GC activity / GC trace tuples.  The indexing is
         * percRows[GC activity][GC trace].
         */
        private JLabel[][][] percRows;
        private int gcTraceNum;
        private int gcActivityNum;

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
         * @return The numbe of columns in the table.
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

            return labels.get(rowIndex)[columnIndex];
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

        /**
         * It sets the style of a label of a given table cell according to its
         * contents, in this case the percentage difference that's contained in
         * the label. The style of the label includes background and foreground
         * colors,
         *
         * @param rowIndex The row of the table cell.
         * @param columnIndex The column of the table cell.
         * @param percDiff The percentage difference that will dictate the
         * style of the label.
         * @param italic Whether the label text will be italic or not.
         */
        private void setLabelStyle(
                JLabel label,
                double percDiff, boolean italic) {
            assert label != null;
            double absDiff = Math.abs(percDiff);
            int index = 0;
            while (index < DIFF_BOUNDS.length &&
                    Comparisons.gt(absDiff, DIFF_BOUNDS[index])) {
                ++index;
            }
            if (Comparisons.lt(percDiff, 0.0)) {
                label.setForeground(FG_NEG_DIFF_COLORS[index]);
                label.setBackground(BG_NEG_DIFF_COLORS[index]);
            } else {
                label.setForeground(FG_POS_DIFF_COLORS[index]);
                label.setBackground(BG_POS_DIFF_COLORS[index]);
            }
            if (!DIFF_BOLD_FONT[index] && !italic) {
                GUIUtilities.setPlain(label);
            } else if (DIFF_BOLD_FONT[index] && !italic) {
                GUIUtilities.setBold(label);
            } else if (!DIFF_BOLD_FONT[index] && italic) {
                GUIUtilities.setItalic(label);
            } else if (DIFF_BOLD_FONT[index] && italic) {
                GUIUtilities.setBoldItalic(label);
            }
        }

        private int metricToColumn(int metric) {
            return 2 + metric - DatasetGenerator.METRIC_FIRST;
        }

        public void updateData() {
            // assert gcActivityNum <= datasetGenerator.getGCActivityNum();
            // assert gcTraceNum <= datasetGenerator.getGCTraceNum();

            for (int i = 0; i < gcActivityNum; ++i) {
                for (int j = 0; j < gcTraceNum; ++j) {
                    JLabel[] dataRow = dataRows[i][j];
                    JLabel[] percRow = null;
                    if (percRows != null) {
                        percRow = percRows[i][j];
                    }


                    if (dataRow == null) {
                        assert percRow == null;
                        continue;
                    }

                    for (int metric = DatasetGenerator.METRIC_FIRST;
                            metric <= DatasetGenerator.METRIC_LAST;
                            ++metric) {
                        CategoryDataset dataset =
                                datasets[metric - DatasetGenerator.METRIC_FIRST];
                        int c = metricToColumn(metric);
                        Number value = dataset.getValue(j, i);
                        Formatter formatter = DatasetGenerator.getFormatter(metric);
                        String str = formatter.format(value);

                        JLabel dataLabel = dataRow[c];
                        dataLabel.setText(str);
                    }

                    if (percRow == null) {
                        continue;
                    }

                    for (int metric = DatasetGenerator.METRIC_FIRST;
                            metric <= DatasetGenerator.METRIC_LAST;
                            ++metric) {
                        CategoryDataset dataset =
                                datasets[metric - DatasetGenerator.METRIC_FIRST];
                        int c = metricToColumn(metric);
                        Number value = dataset.getValue(j, i);
                        Number base = dataset.getValue(
                                DatasetGenerator.AGGREGATE_GC_ACTIVITY_INDEX, i);
                        JLabel dataLabel = dataRow[c];
                        JLabel percLabel = percRow[c];

                        double diffPerc = Calculations.percDiff(value, base);
                        String str;
                        if (Double.isNaN(diffPerc)) {
                            str = Formatting.formatPercDiff(diffPerc);
                        } else if (Comparisons.eq(diffPerc, 0.0)) {
                            str = "";
                        } else {
                            str = Formatting.formatPercDiff(diffPerc);
                        }
                        percLabel.setText(str);
                        setLabelStyle(dataLabel, diffPerc, false);
                        setLabelStyle(percLabel, diffPerc, true);
                    }
                }
            }
        }

        private JLabel[] createNewRow() {
            return createNewRow(null);
        }
        
        private JLabel[] createNewRow(String tip) {
            JLabel[] rowLabels = new JLabel[columns];
            for (int i = 0; i < columns; ++i) {
                JLabel label = GUIUtilities.createJLabelForTable();
                rowLabels[i] = label;
                if (tip != null) {
                    label.setToolTipText(tip);
                }
            }

            labels.add(rows, rowLabels);
            rows += 1;

            return rowLabels;
        }

        /**
         * It addres a new row separator row to the table.
         */
        private void createNewSeparatoRow() {
            separatorRows.add(rows);
            JLabel[] rowLabels = createNewRow();
            assert rowLabels.length == columns;
            for (int i = 0; i < rowLabels.length; ++i) {
                rowLabels[i].setBackground(GRID_COLOR);
            }
        }

        private JLabel[] createNewDataRow(String tip) {
            JLabel[] rowLabels = createNewRow(tip);
            assert rowLabels.length == columns;

            GUIUtilities.setTableHeader(rowLabels[0]);
            GUIUtilities.setTableHeader(rowLabels[1]);
            for (int i = 2; i < columns; ++i) {
                rowLabels[i].setHorizontalAlignment(SwingConstants.RIGHT);
            }

            return rowLabels;
        }

        public void updateSeparatorRows() {
            for (int i : separatorRows) {
                table.setRowHeight(i, SEPARATOR_ROW_HEIGHT);
            }
        }

        public void update() {
            gcActivityNum = datasetGenerator.getGCActivityNum();
            gcTraceNum = datasetGenerator.getGCTraceNum();

            boolean comparison = comparisonCheckBox.isSelected();
            rows = 0;
            labels = new ArrayList<JLabel[]>();
            separatorRows = new ArrayList<Integer>();
            dataRows = new JLabel[gcActivityNum][gcTraceNum][];
            if (comparison) {
                percRows = new JLabel[gcActivityNum][gcTraceNum][];
            } else {
                percRows = null;
            }

            for (int i = 0; i < gcActivityNum; ++i) {
                createNewSeparatoRow();
                String gcActivityName = datasetGenerator.getGCActivityName(i);

                boolean firstRow = true;
                boolean firstValid = false;
                for (int j = 0; j < gcTraceNum; ++j) {
                    if (!datasetGenerator.isValueValid(j, i)) {
                        continue;
                    }
                    
                    String tip = datasetGenerator.getLongGCTraceName(j);

                    if (j == 0) {
                        firstValid = true;
                    }

                    JLabel[] rowLabels = createNewDataRow(tip);
                    dataRows[i][j] = rowLabels;

                    if (firstRow) {
                        rowLabels[0].setText(gcActivityName);
                        firstRow = false;
                    }
                    String gcTraceName = datasetGenerator.getGCTraceName(j);
                    rowLabels[1].setText(gcTraceName);

                    if (j == 0 || !firstValid || percRows == null) {
                        continue;
                    }

                    rowLabels = createNewDataRow(tip);
                    percRows[i][j] = rowLabels;
                }
            }
            createNewSeparatoRow();
        }

        /**
         * It creates a new instance of this table model.
         *
         * @param datasetGenerator The dataset generator that will generator
         * the values for the table.
         */
        public Model(DatasetGenerator datasetGenerator) {
            this.datasetGenerator = datasetGenerator;

            columnNames[0] = "";
            columnNames[1] = "";
            for (int metric = DatasetGenerator.METRIC_FIRST;
                    metric <= DatasetGenerator.METRIC_LAST; ++metric) {
                String metricName =
                        DatasetGenerator.getMetricNameWithUnit(metric);

                int c = metricToColumn(metric);
                columnNames[c] = metricName;
                datasets[metric - DatasetGenerator.METRIC_FIRST] =
                        datasetGenerator.newCategoryDataset(metric, false);
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
                        model.updateSeparatorRows();
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
                        model.updateSeparatorRows();
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
    public AllStatsTableMulti(
            DatasetGenerator datasetGenerator,
            Locker locker) {
        ArgumentChecking.notNull(datasetGenerator, "datasetGenerator");

        initComponents();

        table.setDefaultRenderer(JLabel.class, new Renderer());
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
        table.setGridColor(GRID_COLOR);

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
        comparisonCheckBox = new javax.swing.JCheckBox();

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

        comparisonCheckBox.setText("Comparison");
        comparisonCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comparisonCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(comparisonCheckBox)
                .addContainerGap())
            .add(tableScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(tableScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(comparisonCheckBox))
        );
    }// </editor-fold>//GEN-END:initComponents
    private void comparisonCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comparisonCheckBoxActionPerformed
        // TODO add your handling code here:
        update();
}//GEN-LAST:event_comparisonCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox comparisonCheckBox;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane tableScrollPane;
    // End of variables declaration//GEN-END:variables
}
