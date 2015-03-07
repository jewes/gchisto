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

import gchisto.gcactivity.GCActivitySet;
import gchisto.gctrace.GCActivityNames;
import gchisto.gctrace.GCTrace;
import gchisto.gctrace.GCTraceSet;
import gchisto.jfreechart.extensions.AbstractChangingDataset;
import gchisto.jfreechart.extensions.ChangingCategoryDataset;
import gchisto.jfreechart.extensions.ChangingCategoryDatasetWithTTG;
import gchisto.jfreechart.extensions.ChangingPieDatasetWithTTG;
import gchisto.utils.Calculations;
import gchisto.utils.Comparisons;
import gchisto.utils.Conversions;
import gchisto.utils.Formatter;
import gchisto.utils.Formatting;
import gchisto.utils.NumberSeq;
import gchisto.utils.errorchecking.ArgumentChecking;
import gchisto.utils.errorchecking.ShouldNotReachHereException;
import java.util.ArrayList;
import java.util.List;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;

/**
 *
 * @author tony
 */
public class DatasetGenerator {

    /**
     * The ID of the 'number' metric, i.e., how many occurances of a GC activity
     * there are.
     */
    static final public int METRIC_NUM = 0;
    /**
     * The ID of the 'percentage in terms of number' metric, i.e., what
     * percentage of all the GC activity occurances, a particular GC activity
     * takes up.
     */
    static final public int METRIC_NUM_PERC = 1;
    /**
     * The ID of the 'total duration' metric.
     */
    static final public int METRIC_TOTAL = 2;
    /**
     * The ID of the 'percentage in terms of total duration' metric, i.e., what
     * percentage of the total duration of all GC activities, a particular GC
     * activity takes up.
     */
    static final public int METRIC_TOTAL_PERC = 3;
    static final public int METRIC_OVERHEAD_PERC = 4;
    /**
     * The ID of the 'average' metric.
     */
    static final public int METRIC_AVG = 5;
    /**
     * The ID of the 'standard deviation' metric.
     */
    static final public int METRIC_SIGMA = 6;
    /**
     * The ID of the 'minimum duration' metric.
     */
    static final public int METRIC_MIN = 7;
    /**
     * The ID of the 'maximum duration' metric.
     */
    static final public int METRIC_MAX = 8;
    /**
     * The ID of the first available metric.
     */
    static final public int METRIC_FIRST = METRIC_NUM;
    /**
     * The ID of the last available metric.
     */
    static final public int METRIC_LAST = METRIC_MAX;
    /**
     * The number of available metrics.
     */
    static final public int METRIC_LENGTH = (METRIC_LAST - METRIC_FIRST) + 1;
    /**
     * The name of the of the aggregate GC activity.
     */
    static final private String AGGREGATE_GC_ACTIVITY_NAME = "All";
    /**
     * The index of the aggregate GC activity in the GC activity name space.
     */
    static final public int AGGREGATE_GC_ACTIVITY_INDEX = 0;
    /**
     * The first index of the GC activities, minus any system added ones
     * (e.g., the aggregate one).
     */
    static final private int FIRST_GC_ACTIVITY_INDEX = AGGREGATE_GC_ACTIVITY_INDEX + 1;
    static final private double UNAVAILABLE_VALUE = 0.0;
    /**
     * The names of the available metrics.
     */
    static final private String[] METRIC_NAMES = {
        "Num", "Num", "Total GC", "Total GC", "Overhead", "Avg", "Sigma", "Min", "Max"
    };
    /**
     * The names of the units of the available metrics.
     */
    static final private String[] METRIC_UNIT_NAMES = {
        null, "%", "sec", "%", "%", "ms", "ms", "ms", "ms"
    };
    /**
     * A formatter that knows how to properly format the value of each metric
     * to a string.
     */
    static final private Formatter[] METRIC_FORMATTERS = {
        Formatting.intFormatter(), Formatting.percFormatter(),
        Formatting.doubleFormatter(), Formatting.percFormatter(),
        Formatting.percFormatter(),
        Formatting.doubleFormatter(), Formatting.doubleFormatter(),
        Formatting.doubleFormatter(), Formatting.doubleFormatter()
    };
    final GCTraceSet gcTraceSet;
    /**
     * A list that contains the names of the loaded GC traces.
     */
    private List<String> gcTraceNames;
    private List<String> longGCTraceNames;
    /**
     * A list that contains the names of all the GC activites in all the
     * loaded traces, as well as the aggregate GC activity.
     */
    private List<String> gcActivityNames;
    /**
     * A list that contains the names of all the GC activities in all the
     * loaded traces, without the aggregate GC activity.
     */
    private List<String> gcActivityNamesMinusAggregate;
    private NumberSeq[][] seqs;
    private int gcTraceNum;
    private int gcActivityNum;

    /**
     * It returns the name of the metric with the given ID.
     *
     * @param metric The ID of the metric whose name will be returned.
     * @return The name of the metric with the given ID.
     *
     * @see #getMetricNameWithUnit(int)
     * @see #getUnitName(int)
     * @see #getFormatter(int)
     */
    static public String getMetricName(int metric) {
        ArgumentChecking.withinBounds(metric, METRIC_FIRST, METRIC_LAST, "which");

        return METRIC_NAMES[metric];
    }

    /**
     * It returns the name of the unit (e.g., %, ms, sec) of the metric with
     * the given ID.
     *
     * @param metric The ID of the metric whose unit's name will be returned.
     * @return The name of the unit of the metric with the given ID.
     *
     * @see #getMetricName(int)
     * @see #getMetricNameWithUnit(int)
     * @see #getFormatter(int)
     */
    static public String getUnitName(int metric) {
        ArgumentChecking.withinBounds(metric, METRIC_FIRST, METRIC_LAST, "which");

        return METRIC_UNIT_NAMES[metric];
    }

    /**
     * It returns the name of the given name, suffixed with the unit of that
     * metric in round brackets.
     *
     * @param metric The ID of the metric whose name will be returned.
     * @return The name of the given name, suffixed with the unit of that
     * metric in round brackets.
     *
     * @see #getUnitName(int)
     * @see #getFormatter(int)
     */
    static public String getMetricNameWithUnit(int metric) {
        ArgumentChecking.withinBounds(metric, METRIC_FIRST, METRIC_LAST, "which");

        String name = METRIC_NAMES[metric];
        String unitName = METRIC_UNIT_NAMES[metric];
        if (unitName != null) {
            name += " (" + unitName + ")";
        }
        return name;
    }

    /**
     * It returns a formatter for value of the metric with the given ID.
     *
     * @param metric The ID of the metric for which a formatter will be returned.
     * @return The ID of the metric for which a formatter will be returned.
     *
     * @see #getMetricName(int)
     * @see #getMetricNameWithUnit(int)
     * @see #getUnitName(int)
     */
    static public Formatter getFormatter(int metric) {
        ArgumentChecking.withinBounds(metric, METRIC_FIRST, METRIC_LAST, "which");

        return METRIC_FORMATTERS[metric];
    }

    /**
     * It returns the name of the given name, suffixed with the unit of that
     * metric in round brackets.
     *
     * @param metric The ID of the metric whose name will be returned.
     * @return The name of the given name, suffixed with the unit of that
     * metric in round brackets.
     *
     * @see #getUnitName(int)
     * @see #getFormatter(int)
     */
    private class Facade extends AbstractChangingDataset
            implements ChangingCategoryDatasetWithTTG, ChangingPieDatasetWithTTG {

        final private int metric;
        final private boolean ignoreAggregate;

        public Comparable getRowKey(int row) {
            assert 0 <= row && row < gcTraceNum;

            return gcTraceNames.get(row);
        }

        public int getRowIndex(Comparable rowKey) {
            String stringKey = (String) rowKey;

            return gcTraceNames.indexOf(stringKey);
        }

        public List getRowKeys() {
            assert gcTraceNum <= gcTraceNames.size();

            return gcTraceNames.subList(0, gcTraceNum);
        }

        public Comparable getColumnKey(int column) {
            assert !ignoreAggregate || 0 <= column && column < gcActivityNum :
                    "column = " + column + ", gcActivityNum = " + gcActivityNum;
            assert ignoreAggregate || 0 <= column && column < gcActivityNum + 1 :
                    "column = " + column + ", gcActivityNum = " + gcActivityNum;

            if (ignoreAggregate) {
                return gcActivityNamesMinusAggregate.get(column);
            } else {
                return gcActivityNames.get(column);
            }
        }

        public int getColumnIndex(Comparable columnKey) {
            String stringKey = (String) columnKey;

            if (ignoreAggregate) {
                return gcActivityNamesMinusAggregate.indexOf(stringKey);
            } else {
                return gcActivityNames.indexOf(stringKey);
            }
        }

        public List getColumnKeys() {
            if (ignoreAggregate) {
                assert gcActivityNum <= gcActivityNamesMinusAggregate.size();
                return gcActivityNamesMinusAggregate.subList(0, gcActivityNum);
            } else {
                assert 1 + gcActivityNum <= gcActivityNames.size();
                return gcActivityNames.subList(0, 1 + gcActivityNum);
            }
        }

        public Number getValue(Comparable rowKey, Comparable columnKey) {
            String stringRow = (String) rowKey;
            String stringColumn = (String) columnKey;

            return getValue(getRowIndex(stringRow), getColumnIndex(stringColumn));
        }

        public int getRowCount() {
            return gcTraceNum;
        }

        public int getColumnCount() {
            if (ignoreAggregate) {
                return gcActivityNum;
            } else {
                return gcActivityNum + 1;
            }
        }

        public Number getValue(int row, int column) {
            if (row >= gcTraceNum) {
                return UNAVAILABLE_VALUE;
            }
            if (ignoreAggregate && column >= gcActivityNum) {
                return UNAVAILABLE_VALUE;
            }
            if (!ignoreAggregate && column >= (gcActivityNum + 1)) {
                return UNAVAILABLE_VALUE;
            }

            assert 0 <= row && row < gcTraceNum;
            assert !ignoreAggregate || 0 <= column && column < gcActivityNum :
                    "column = " + column + ", gcActivityNum = " + gcActivityNum;
            assert ignoreAggregate || 0 <= column && column < gcActivityNum + 1 :
                    "column = " + column + ", gcActivityNum = " + gcActivityNum;

            if (ignoreAggregate) {
                column = FIRST_GC_ACTIVITY_INDEX + column;
            }
            if (seqs[row][column] == null) {
                return UNAVAILABLE_VALUE;
            }

            switch (metric) {
                case METRIC_NUM:
                    return seqs[row][column].getNum();
                case METRIC_NUM_PERC:
                    return Calculations.perc(seqs[row][column].getNumD(),
                            seqs[row][AGGREGATE_GC_ACTIVITY_INDEX].getNumD());
                case METRIC_TOTAL:
                    return seqs[row][column].getSum();
                case METRIC_TOTAL_PERC:
                    return Calculations.perc(seqs[row][column].getSum(),
                            seqs[row][AGGREGATE_GC_ACTIVITY_INDEX].getSum());
                case METRIC_OVERHEAD_PERC:
                    double lastTimeStampSec = gcTraceSet.get(row).getLastTimeStampSec();
                    if (Comparisons.eq(lastTimeStampSec, 0.0)) {
                        return 0.0;
                    } else {
                        return Calculations.perc(seqs[row][column].getSum(), lastTimeStampSec);
                    }
                case METRIC_AVG:
                    return Conversions.secToMS(seqs[row][column].getAvg());
                case METRIC_SIGMA:
                    return Conversions.secToMS(seqs[row][column].getSigma());
                case METRIC_MIN:
                    return Conversions.secToMS(seqs[row][column].getMin());
                case METRIC_MAX:
                    return Conversions.secToMS(seqs[row][column].getMax());
                default:
                    throw new ShouldNotReachHereException();
            }
        }

        // for pie dataset
        public Comparable getKey(int index) {
            return getColumnKey(index);
        }

        public int getIndex(Comparable key) {
            String stringKey = (String) key;

            return getColumnIndex(stringKey);
        }

        public List getKeys() {
            return getColumnKeys();
        }

        public Number getValue(Comparable key) {
            return getValue(0, getIndex(key));
        }

        public int getItemCount() {
            return getColumnCount();
        }

        public Number getValue(int index) {
            return getValue(0, index);
        }

        public String generateToolTip(
                CategoryDataset dataset,
                int row, int column) {
            assert this == dataset;

            String gcTraceName = longGCTraceNames.get(row);
            String gcActivityName;
            if (ignoreAggregate) {
                gcActivityName = gcActivityNamesMinusAggregate.get(column);
            } else {
                gcActivityName = gcActivityNames.get(column);
            }
            String metricName = METRIC_NAMES[metric];
            String unitName = METRIC_UNIT_NAMES[metric];
            double value = getValue(row, column).doubleValue();
            String str = String.format("[%s] %s for %s = %s %s",
                    gcTraceName, metricName, gcActivityName,
                    Formatting.formatIntOrDouble(value),
                    (unitName == null) ? "" : unitName);
            return str;
        }

        public String generateToolTip(
                PieDataset dataset, Comparable key) {
            return generateToolTip(this, 0, getColumnIndex(key));
        }

        public Facade(int metric, boolean ignoreAggregate) {
            this.metric = metric;
            this.ignoreAggregate = ignoreAggregate;
        }
    }

    synchronized void checkpoint() {
        gcTraceNum = gcTraceSet.size();
        GCActivityNames allGCActivityNames = gcTraceSet.getAllGCActivityNames();
        gcActivityNum = allGCActivityNames.size();
    }

    public void update() {
        checkpoint();

        // the number of rows
        gcTraceNames = new ArrayList<String>(gcTraceNum);
        longGCTraceNames = new ArrayList<String>(gcTraceNum);
        for (int i = 0; i < gcTraceNum; ++i) {
            gcTraceNames.add(i, gcTraceSet.get(i).getName());
            longGCTraceNames.add(i, gcTraceSet.get(i).getLongName());
        }

        // the number of columns
        GCActivityNames allGCActivityNames = gcTraceSet.getAllGCActivityNames();
        // +1 for the aggregate name
        gcActivityNames = new ArrayList<String>(1 + gcActivityNum);
        gcActivityNamesMinusAggregate = new ArrayList<String>(gcActivityNum);
        gcActivityNames.add(AGGREGATE_GC_ACTIVITY_NAME);
        for (int i = 0; i < gcActivityNum; ++i) {
            String gcActivityName = allGCActivityNames.get(i);
            gcActivityNames.add(gcActivityName);
            gcActivityNamesMinusAggregate.add(gcActivityName);
        }
        assert gcActivityNames.size() == 1 + gcActivityNum;
        assert gcActivityNamesMinusAggregate.size() == gcActivityNum;
        assert gcActivityNames.get(AGGREGATE_GC_ACTIVITY_INDEX).equals(AGGREGATE_GC_ACTIVITY_NAME);
        assert FIRST_GC_ACTIVITY_INDEX + gcActivityNum == gcActivityNames.size();
        for (int i = 0; i < gcActivityNum; ++i) {
            assert !gcActivityNames.get(FIRST_GC_ACTIVITY_INDEX + i).equals(AGGREGATE_GC_ACTIVITY_NAME);
            assert !gcActivityNamesMinusAggregate.get(i).equals(AGGREGATE_GC_ACTIVITY_NAME);
        }

        seqs = new NumberSeq[gcTraceNum][1 + gcActivityNum];
        for (int i = 0; i < gcTraceNum; ++i) {
            NumberSeq[] rowSeq = seqs[i];

            GCTrace gcTrace = gcTraceSet.get(i);
            rowSeq[AGGREGATE_GC_ACTIVITY_INDEX] =
                    gcTrace.getAllGCActivities().getNumberSeq();

            for (GCActivitySet gcActivitySet : gcTrace) {
                String gcActivityName = gcActivitySet.getGCActivityName();
                int index = allGCActivityNames.indexOf(gcActivityName);
                assert index != -1;
                rowSeq[FIRST_GC_ACTIVITY_INDEX + index] = gcActivitySet.getNumberSeq();
            }
        }
    }

    public int getGCTraceNum() {
        return gcTraceNum;
    }

    public String getGCTraceName(int id) {
        assert 0 <= id && id < gcTraceNum;
        return gcTraceNames.get(id);
    }

    public String getLongGCTraceName(int id) {
        assert 0 <= id && id < gcTraceNum;
        return gcTraceSet.get(id).getLongName();
    }

    public int getGCActivityNum() {
        return 1 + gcActivityNum;
    }

    public String getGCActivityName(int id) {
        assert 0 <= id && id < 1 + gcActivityNum;
        return gcActivityNames.get(id);
    }

    public boolean isValueValid(int row, int column) {
        assert 0 <= row && row < gcTraceNum;
        assert 0 <= column && column < 1 + gcActivityNum;

        return seqs[row][column] != null;
    }

    public ChangingCategoryDataset newCategoryDataset(
            int metric,
            boolean ignoreAggregate) {
        return new Facade(metric, ignoreAggregate);
    }

    public ChangingCategoryDatasetWithTTG newCategoryDatasetWithTTG(
            int metric,
            boolean ignoreAggregate) {
        return new Facade(metric, ignoreAggregate);
    }

    public ChangingPieDatasetWithTTG newPieDatsetWithTTG(int metric) {
        return new Facade(metric, true);
    }

    public DatasetGenerator(GCTraceSet gcTraceSet) {
        ArgumentChecking.notNull(gcTraceSet, "gcTraceset");

        this.gcTraceSet = gcTraceSet;

        update();
    }
}
