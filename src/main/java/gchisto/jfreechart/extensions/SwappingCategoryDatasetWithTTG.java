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
package gchisto.jfreechart.extensions;

import gchisto.utils.errorchecking.ArgumentChecking;
import java.util.List;
import org.jfree.data.category.CategoryDataset;

/**
 * A <tt>CategoryDataset</tt> that swaps the rows / columns of another
 * <tt>CategoryDataset</tt>.
 *
 * @author Tony Printezis
 *
 * @see gchisto.jfreechart.extensions.DatasetWithGroups
 */
public class SwappingCategoryDatasetWithTTG extends AbstractChangingDataset
            implements ChangingCategoryDatasetWithTTG {
    
    /**
     * The source category dataset.
     */
    final private ChangingCategoryDatasetWithTTG dataset;
    
    /**
     * It returns the key of the row with the given index (i.e., the key
     * of the column with the given index in the source dataset). 
     *
     * @param i The index of the row whose key will be returned.
     * @return The key of the row with the given index.
     */
    public Comparable getRowKey(int i) {
        return dataset.getColumnKey(i);
    }

    /**
     * It returns the index of the of the row that has the given key 
     * (i.e., the index of the column that has this particular key in the
     * source dataset).
     *
     * @param rowKey The key to be looked up.
     * @return The index of the row that has the given key.
     */
    public int getRowIndex(Comparable rowKey) {
        assert rowKey != null;
        
        return dataset.getColumnIndex(rowKey);
    }

    /**
     * It returns all the row keys in a list (i.e., all the column keys
     * in the source dataset).
     *
     * @return All the row keys in a list.
     */
    public List getRowKeys() {
        return dataset.getColumnKeys();
    }

    /**
     * It returns the key of the column with the given index (i.e., the key
     * of the row with the given index in the source dataset). 
     *
     * @param i The index of the column whose key will be returned.
     * @return The key of the column with the given index.
     */
    public Comparable getColumnKey(int i) {
        return dataset.getRowKey(i);
    }

    /**
     * It returns the index of the of the column that has the given key 
     * (i.e., the index of the row that has this particular key in the
     * source dataset).
     *
     * @param columnKey The key to be looked up.
     * @return The index of the column that has the given key.
     */
    public int getColumnIndex(Comparable columnKey) {
        assert columnKey != null;
        
        return dataset.getRowIndex(columnKey);
    }

    /**
     * It returns all the column keys in a list (i.e., all the row keys
     * in the source dataset).
     *
     * @return All the row keys in a list.
     */
    public List getColumnKeys() {
        return dataset.getRowKeys();
    }

    /**
     * It returns the number of rows in the dataset (i.e., the number
     * of columns in the source dataset).
     *
     * @return The number of rows in the dataset.
     */
    public int getRowCount() {
        return dataset.getColumnCount();
    }

    /**
     * It returns the number of columns in the dataset (i.e., the number
     * of row in the source dataset).
     *
     * @return The number of columns in the dataset.
     */
    public int getColumnCount() {
        return dataset.getRowCount();
    }

    /**
     * It returns the value in the dataset that corresponds to the given
     * row and column (i.e., the value of the source dataset with the row
     * and column swapped).
     *
     * @param row The row of the value to be looked up.
     * @param column The column of the value to be looked up.
     * @return The value in the dataset that corresponds to the given
     * row and column.
     */
    public Number getValue(Comparable row, Comparable column) {
        assert row != null;
        assert column != null;
        
        return dataset.getValue(column, row);
    }

    /**
     * It returns the value in the dataset that corresponds to the given
     * row and column (i.e., the value of the source dataset with the row
     * and column swapped).
     *
     * @param row The row of the value to be looked up.
     * @param column The column of the value to be looked up.
     * @return The value in the dataset that corresponds to the given
     * row and column.
     */
    public Number getValue(int row, int column) {
        return dataset.getValue(column, row);
    }
    
    public String generateToolTip(CategoryDataset dataset, int row, int column) {
        assert dataset == this;
        return this.dataset.generateToolTip(this.dataset, column, row);
    }

    /**
     * It creates a new instance of the swapping category dataset and
     * associates the given dataset with it.
     *
     * @param dataset The source dataset.
     */
    public SwappingCategoryDatasetWithTTG(ChangingCategoryDatasetWithTTG dataset) {
        ArgumentChecking.notNull(dataset, "dataset");
        
        this.dataset = dataset;
    }

}
