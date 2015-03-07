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

import java.util.ArrayList;
import java.util.List;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.xy.XYDataset;

/**
 * This is an <tt>XYDataset</tt>, the <tt>DatasetWithGroups</tt> flavor, that
 * consolidates a number of <tt>XYDatasets</tt>. Some restrictions are imposed
 * on these (let's call them the children) datasets.
 * <ul>
 * <li> They datasets should share groups and groups should correspond to
 * the series in each dataset.
 * <li> In each dataset, for a given x-value, the y-value of series S should
 * be the >= the value of series S-1. In fact, it's assumed that the y-value
 * for series S is the value of series S-1 plus some N.
 * <li> They should share the same x-axis, with increments of a given
 * value, and the x-value of their items should start from 0 and be in
 * ascending order.
 * </ul>
 *
 * In the consolidating dataset, each series corresponds to each child
 * dataset and the y-value for each series is the value for the highest
 * series. The resulting dataset actually has twice the number of items
 * for each child dataset than the most populous series in that child dataset.
 * For every item in the child dataset, the consolidating dataset generates
 * two items, one for the beginning and one for the end of the [x, x + xStep]
 * range.
 *
 * The consolidating dataset is used in the
 * <tt>gchisto.gui.panels.gcdistribution</tt> package.
 *
 * @author Tony Printezis
 *
 * @see org.jfree.data.xy.XYDataset
 * @see gchisto.jfreechart.extensions.DatasetWithGroups
 * @see gchisto.jfreechart.extensions.XYDatasetWithGroups
 * @see gchisto.gui.panels.gcdistribution
 */
public class ConsolidatingXYDatasetWithGroups extends AbstractDataset
        implements XYDatasetWithGroups {
    
    /**
     * The names of children datasets.
     */
    final private List<String> names = new ArrayList<String>();
    
    /**
     * The children datasets.
     */
    final private List<XYDatasetWithGroups> datasets = new ArrayList<XYDatasetWithGroups>();
    
    /**
     * The number of items in the most populous series for all children datasets.
     */
    final private List<Integer> maxCounts = new ArrayList<Integer>();
    
    /**
     * The increment for the x-axis values.
     */
    final private double xStep;
    
    /**
     * An array with one entry per GC activity in the GC trace that
     * dictates whether that GC activity is active (i.e., whether
     * it will be displayed in the chart).
     */
    private boolean active[];
    
    /**
     * The names of the groups that all children datasetes have.
     */
    private String groupNames[];
    
    /**
     * It returns the number of items in the most populous series
     * in the dataset.
     *
     * @param dataset The dataset whose series will be iterated over.
     * @return The number of items in the most populous series
     * in the dataset.
     */
    private int calculateMaxCount(XYDataset dataset) {
        int max = 0;
        for (int i = 0; i < dataset.getSeriesCount(); ++i) {
            int count = dataset.getItemCount(i);
            if (count > max) {
                max = count;
            }
        }
        return max;
    }
    
    /**
     * It iterates over the children datasets and updates the maximum item
     * number of the most populous series in each dataset.
     */
    private void recalculateMaxCounts() {
        for (int i = 0; i < datasets.size(); ++i) {
            XYDatasetWithGroups dataset = datasets.get(i);
            maxCounts.set(i, calculateMaxCount(dataset));
        }
    }
    
    /**
     * It returns the order of the domain axis (i.e., the x-axis).
     *
     * @return The order of the domain axis (i.e., the x-axis).
     */
    public DomainOrder getDomainOrder() {
        return DomainOrder.ASCENDING;
    }
    
    /**
     * It returns the number of series (e.g., children datasets) in
     * the dataset.
     *
     * @return The number of series in the dataset
     */
    public int getSeriesCount() {
        return datasets.size();
    }
    
    /**
     * It returns the series key (e.g., the name of the child dataset)
     * of the given index.
     *
     * @param series The index of the series whose key will be returned.
     * @return The series key of the given index.
     */
    public Comparable getSeriesKey(int series) {
        assert 0 <= series && series < names.size();
        
        return names.get(series);
    }
    
    /**
     * It returns the series of the given series key (e.g., the index of
     * the child dataset with the given name).
     *
     * @param seriesKey The series key whose index will be returned.
     * @return The series of the given series key.
     */
    public int indexOf(Comparable seriesKey) {
        assert seriesKey instanceof String;
        
        return names.indexOf(seriesKey);
    }
    
    /**
     * It returns the number of items in the given series (e.g., the given
     * child dataset). This corresponds to twice the number of items in the
     * most populous series of that child dataset.
     *
     * @param series The series whose item number will be returned.
     * @return The number of items in the given series.
     */
    public int getItemCount(int series) {
        assert 0 <= series && series < datasets.size();
        return maxCounts.get(series) * 2;
    }
    
    /**
     * It returns the x-value for a given data point of the given series.
     *
     * @param series The series of the data point.
     * @param item The index of the given data point.
     * @return The x-value for a given data point of the given series.
     */
    public Number getX(int series, int item) {
        assert 0 <= series && series < datasets.size();
        
        return getXValue(series, item);
    }
    
    /**
     * It returns the x-value for a given data point of the given series.
     *
     * @param series The series of the data point.
     * @param item The index of the given data point.
     * @return The x-value for a given data point of the given series.
     */
    public double getXValue(int series, int item) {
        assert 0 <= series && series < datasets.size();
        
        return ((double) ((item + 1) / 2)) * xStep;
    }
    
    /**
     * It returns the y-value for a given data point of the given series.
     *
     * @param series The series of the data point.
     * @param item The index of the given data point.
     * @return The y-value for a given data point of the given series.
     */
    public Number getY(int series, int item) {
        assert 0 <= series && series < datasets.size();
        
        return getYValue(series, item);
    }
    
    /**
     * It returns the y-value for a given data point of the given series.
     *
     * @param series The series of the data point.
     * @param item The index of the given data point.
     * @return The y-value for a given data point of the given series.
     */
    public double getYValue(int series, int item) {
        assert 0 <= series && series < datasets.size();
        
        XYDataset dataset = datasets.get(series);
        int index = dataset.getSeriesCount() - 1;
        assert index >= 0;
        return dataset.getYValue(index, item / 2);
    }
    
    /**
     * It returns the number of groups in the dataset, e.g., the number
     * of groups in the children dataset (which should be the same for all
     * of them).
     *
     * @return The number of groups in the dataset.
     */
    public int getGroupCount() {
        return groupNames.length;
    }
    
    /**
     * It returns the name of the group with the given index (which should
     * be the same for all children datasets).
     *
     * @param group The index of the group to be returned.
     * @return The name of the group with the givan index.
     */
    public String getGroupName(int group) {
        assert 0 <= group && group < groupNames.length;
        
        return groupNames[group];
    }
    
    /**
     * It determines whether the group with the given index is active (and
     * it should have the same state in all children datasets).
     *
     * @param group The index of the group to be determined whether it is
     * active.
     * @return Whether the group with the given index is active.
     */
    public boolean isGroupActive(int group) {
        assert 0 <= group && group < active.length;
        
        boolean value = active[group];
        for (XYDatasetWithGroups dataset : datasets) {
            assert dataset.isGroupActive(group) == value;
        }
        
        return value;
    }
    
    /**
     * It sets whether the group with the given index is active or not and
     * propagates this value to all children datasets.
     *
     * @param group The index of the group to be set as active or not active.
     * @param value Whether the group will be set as active or not.
     */
    public void setGroupActive(int group, boolean value) {
        assert 0 <= group && group < active.length;
        
        active[group] = value;
        for (XYDatasetWithGroups dataset : datasets) {
            dataset.setGroupActive(group, value);
        }
        recalculateMaxCounts();
        notifyListeners(new DatasetChangeEvent(this, this));
    }
    
    /**
     * It adds a new child dataset to this dataset and associates it with a
     * name.
     *
     * @param name The name of the child dataset to be added.
     * @param dataset The child dataset to be added.
     */
    public void add(String name, XYDatasetWithGroups dataset) {
        assert names.size() == datasets.size();
        assert datasets.size() == maxCounts.size();
        
        names.add(name);
        datasets.add(dataset);
        maxCounts.add(calculateMaxCount(dataset));
        if (datasets.size() == 1) {
            assert active == null;
            int groupCount = dataset.getGroupCount();
            active = new boolean[groupCount];
            groupNames = new String[groupCount];
            for (int i = 0; i < groupCount; ++i) {
                active[i] = true;
                dataset.setGroupActive(i, true);
                groupNames[i] = dataset.getGroupName(i);
            }
        } else {
            assert active != null;
            assert datasets.size() > 1;
            assert dataset.getGroupCount() == active.length;
            for (int i = 0; i < groupNames.length; ++i) {
                assert groupNames[i].equals(dataset.getGroupName(i));
            }
        }
        
        assert names.size() == datasets.size();
        assert datasets.size() == maxCounts.size();
    }
    
    /**
     * It creates a new instance of this dataset.
     *
     * @param xStep The x-axis increment for the children of this dataet.
     */
    public ConsolidatingXYDatasetWithGroups(double xStep) {
        this.xStep = xStep;
    }
    
}
