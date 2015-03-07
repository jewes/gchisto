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
package gchisto.gui.panels.gctimeline;

import gchisto.gcactivity.GCActivity;
import gchisto.gcactivity.GCActivitySet;
import gchisto.gctrace.GCTrace;
import gchisto.gctrace.GCTraceCheckpoint;
import gchisto.jfreechart.extensions.AbstractDatasetWithGroups;
import gchisto.jfreechart.extensions.IntervalXYDatasetWithGroups;
import gchisto.utils.Conversions;
import gchisto.utils.Formatting;
import gchisto.utils.errorchecking.ShouldNotReachHereException;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.DomainOrder;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author tony
 */
public class Dataset extends AbstractDatasetWithGroups
        implements IntervalXYDatasetWithGroups, XYToolTipGenerator {

    /**
     * The ID of the 'duration' metric.
     */
    static public final int METRIC_DURATION = 0;
    /**
     * The ID of the first available metric.
     */
    static public final int METRIC_FIRST = METRIC_DURATION;
    /**
     * The ID of the last available metric.
     */
    static public final int METRIC_LAST = METRIC_DURATION;
    /**
     * The number of available metrics.
     */
    static public final int METRIC_LENGTH = (METRIC_LAST - METRIC_FIRST) + 1;
    /**
     * The GC trace that will provide the data for the datasets.
     */
    final private GCTrace gcTrace;
    final private GCTraceCheckpoint checkpoint;
    /**
     * The metric that an instance of this class has been specialized for.
     */
    final private int metric;

    /**
     * It returns the GC activity set for a given series (e.g., the
     * index of the GC activity set within the current GC trace).
     *
     * @param series The index of the GC activity set within the current
     * GC trace.
     * @return The GC activity set for a given series.
     */
    private GCActivitySet gcActivitySet(int series) {
        assert 0 <= series && series < checkpoint.gcTraceSize();

        return gcTrace.get(series);
    }

    /**
     * It returns the GC activity for a given series (e.g., the
     * index of the GC activity set within the current GC trace) and item
     * (e.g., the index of the GC activity within the GC activity set)
     * combination.
     *
     * @param series The index of the GC activity set within the current
     * GC trace.
     * @param item The index of the GC activity within the GC activity set.
     * @return The GC activity object for a given series and item
     * combination.
     */
    private GCActivity gcActivity(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item < checkpoint.size(series);

        return gcActivitySet(series).get(item);
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
     * It returns the number of series (e.g., GC activity sets) in
     * the current GC trace.
     *
     * @return The number of series (e.g., GC activity sets) in the
     * current GC trace.
     */
    public int getSeriesCount() {
        return getGroupCount();
    }

    /**
     * It returns the series key (e.g., the name of the GC activity set)
     * of the given index.
     *
     * @param series The index of the series whose key will be returned.
     * @return The series key of the given index.
     */
    public Comparable getSeriesKey(int series) {
        assert 0 <= series && series < checkpoint.gcTraceSize();

        return getGroupName(series);
    }

    /**
     * It returns the series of the given series key (e.g., the index of
     * the GC activity set with the given name).
     *
     * @param seriesKey The series key whose index will be returned.
     * @return The series of the given series key.
     */
    public int indexOf(Comparable seriesKey) {
        assert seriesKey instanceof String;

        return indexOfGroupName((String) seriesKey);
    }

    /**
     * It returns the number of items in the given series (e.g., the
     * number of GC activities in the given GC activity set). If the
     * series is not currently active, this method will return 0.
     *
     * @param series The series whose item number will be returned.
     * @return The number of items in the given series, or 0 if the
     * series is not active.
     */
    public int getItemCount(int series) {
        assert 0 <= series && series < checkpoint.gcTraceSize();

        return (isGroupActive(series)) ? checkpoint.size(series) : 0;
    }

    /**
     * It returns the x-value for the given GC activity. The x-value here
     * is defined here as the middle of the timeline between GC start
     * and GC end times of the GC activity.
     *
     * @param series The index of the GC activity set within the current
     * GC trace.
     * @param item The index of the GC activity within the GC activity set.
     * @return The x-value for the given GC activity.
     *
     * @see #getX(int, int)
     * @see #getStartX(int, int)
     * @see #getStartXValue(int, int)
     * @see #getEndX(int, int)
     * @see #getEndXValue(int, int)
     */
    public Number getX(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item < checkpoint.size(item);


        return getXValue(series, item);
    }

    /**
     * It returns the x-value for the given GC activity. The x-value here
     * is defined here as the middle of the timeline between GC start
     * and GC end times of the GC activity.
     *
     * @param series The index of the GC activity set within the current
     * GC trace.
     * @param item The index of the GC activity within the GC activity set.
     * @return The x-value for the given GC activity.
     *
     * @see #getXValue(int, int)
     * @see #getStartX(int, int)
     * @see #getStartXValue(int, int)
     * @see #getEndX(int, int)
     * @see #getEndXValue(int, int)
     */
    public double getXValue(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item < checkpoint.size(series);


        double startSec = getStartXValue(series, item);
        double endSec = getEndXValue(series, item);
        double ret = startSec + (endSec - startSec) / 2.0;
        return ret;
    }

    /**
     * It returns the y-value for the given GC activity. The y-value here
     * is the value of the metric this dataset generator has been
     * customized with of the given GC activity.
     *
     * @param series The index of the GC activity set within the current
     * GC trace.
     * @param item The index of the GC activity within the GC activity set.
     * @return The y-value for the given GC activity.
     *
     * @see #getY(int, int)
     * @see #getStartY(int, int)
     * @see #getStartYValue(int, int)
     * @see #getEndY(int, int)
     * @see #getEndYValue(int, int)
     */
    public Number getY(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item < checkpoint.size(series);

        return getYValue(series, item);
    }

    /**
     * It returns the y-value for the given GC activity. The y-value here
     * is the value of the metric this dataset generator has been
     * customized with of the given GC activity.
     *
     * @param series The index of the GC activity set within the current
     * GC trace.
     * @param item The index of the GC activity within the GC activity set.
     * @return The y-value for the given GC activity.
     *
     * @see #getYValue(int, int)
     * @see #getStartY(int, int)
     * @see #getStartYValue(int, int)
     * @see #getEndY(int, int)
     * @see #getEndYValue(int, int)
     */
    public double getYValue(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item < checkpoint.size(series);

        switch (metric) {
            case METRIC_DURATION:
                double durationSec = gcActivity(series, item).getDurationSec();
                return Conversions.secToMS(durationSec);
            default:
                throw new ShouldNotReachHereException();
            }
    }

    /**
     * It returns the start x-value for the given GC activity. This value
     * here is the start time of the GC activity.
     *
     * @param series The index of the GC activity set within the current
     * GC trace.
     * @param item The index of the GC activity within the GC activity set.
     * @return The start x-value for the given GC activity.
     *
     * @see #getX(int, int)
     * @see #getXValue(int, int)
     * @see #getStartXValue(int, int)
     * @see #getEndX(int, int)
     * @see #getEndXValue(int, int)
     */
    public Number getStartX(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item < checkpoint.size(series);

        return getStartXValue(series, item);
    }

    /**
     * It returns the start x-value for the given GC activity. This value
     * here is the start time of the GC activity.
     *
     * @param series The index of the GC activity set within the current
     * GC trace.
     * @param item The index of the GC activity within the GC activity set.
     * @return The start x-value for the given GC activity.
     *
     * @see #getX(int, int)
     * @see #getXValue(int, int)
     * @see #getStartX(int, int)
     * @see #getEndX(int, int)
     * @see #getEndXValue(int, int)
     */
    public double getStartXValue(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item < checkpoint.size(series);

        double startSec = gcActivity(series, item).getStartSec();
        return startSec;
    }

    /**
     * It returns the end x-value for the given GC activity. This value
     * here is the end time of the GC activity.
     *
     * @param series The index of the GC activity set within the current
     * GC trace.
     * @param item The index of the GC activity within the GC activity set.
     * @return The end x-value for the given GC activity.
     *
     * @see #getX(int, int)
     * @see #getXValue(int, int)
     * @see #getStartX(int, int)
     * @see #getStartXValue(int, int)
     * @see #getEndXValue(int, int)
     */
    public Number getEndX(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item < checkpoint.size(series);

        return getEndXValue(series, item);
    }

    /**
     * It returns the end x-value for the given GC activity. This value
     * here is the end time of the GC activity.
     *
     * @param series The index of the GC activity set within the current
     * GC trace.
     * @param item The index of the GC activity within the GC activity set.
     * @return The end x-value for the given GC activity.
     *
     * @see #getX(int, int)
     * @see #getXValue(int, int)
     * @see #getStartX(int, int)
     * @see #getStartXValue(int, int)
     * @see #getEndX(int, int)
     */
    public double getEndXValue(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item < checkpoint.size(series);

        double startSec = gcActivity(series, item).getStartSec();
        double durationSec = gcActivity(series, item).getDurationSec();
        return startSec + durationSec;
    }

    /**
     * It returns the start y-value for the given GC activity. This value
     * here is 0.0.
     *
     * @param series The index of the GC activity set within the current
     * GC trace.
     * @param item The index of the GC activity within the GC activity set.
     * @return The start y-value for the given GC activity.
     *
     * @see #getY(int, int)
     * @see #getYValue(int, int)
     * @see #getStartYValue(int, int)
     * @see #getEndY(int, int)
     * @see #getEndYValue(int, int)
     */
    public Number getStartY(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item < checkpoint.size(series);

        return getStartYValue(series, item);
    }

    /**
     * It returns the start y-value for the given GC activity. This value
     * here is 0.0.
     *
     * @param series The index of the GC activity set within the current
     * GC trace.
     * @param item The index of the GC activity within the GC activity set.
     * @return The start y-value for the given GC activity.
     *
     * @see #getY(int, int)
     * @see #getYValue(int, int)
     * @see #getStartY(int, int)
     * @see #getEndY(int, int)
     * @see #getEndYValue(int, int)
     */
    public double getStartYValue(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item < checkpoint.size(series);

        return 0.0;
    }

    /**
     * It returns the end y-value for the given GC activity. This value here
     * is the value of the metric this dataset generator has been
     * customized with of the given GC activity.
     *
     * @param series The index of the GC activity set within the current
     * GC trace.
     * @param item The index of the GC activity within the GC activity set.
     * @return The end y-value for the given GC activity.
     *
     * @see #getY(int, int)
     * @see #getYValue(int, int)
     * @see #getStartY(int, int)
     * @see #getStartYValue(int, int)
     * @see #getEndYValue(int, int)
     */
    public Number getEndY(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item < checkpoint.size(series);

        return getEndYValue(series, item);
    }

    /**
     * It returns the end y-value for the given GC activity. This value here
     * is the value of the metric this dataset generator has been
     * customized with of the given GC activity.
     *
     * @param series The index of the GC activity set within the current
     * GC trace.
     * @param item The index of the GC activity within the GC activity set.
     * @return The end y-value for the given GC activity.
     *
     * @see #getY(int, int)
     * @see #getYValue(int, int)
     * @see #getStartY(int, int)
     * @see #getStartYValue(int, int)
     * @see #getEndY(int, int)
     */
    public double getEndYValue(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item < checkpoint.size(series);
        
        return getYValue(series, item);
    }

    public String generateToolTip(XYDataset dataset, int series, int item) {
        GCActivity gcActivity = gcActivity(series, item);
        String gcActivityName = gcActivity.getName();
        double startSec = gcActivity.getStartSec();
        double durationSec = gcActivity.getDurationSec();
        String str = String.format("%s, start = %s sec, duration = %s sec",
                gcActivityName,
                Formatting.formatDouble(startSec),
                Formatting.formatDouble(durationSec));
        return str;
    }
    /**
     * It creates a new instance of this dataset generator.
     *
     * @param gcTraceSet The GC trace set that contains the GC trace that will
     * provide the data for the new dataset generator.
     * @param gcTrace The GC trace that will provide data for the
     * new dataset generator.
     */
    public Dataset(GCTrace gcTrace, GCTraceCheckpoint checkpoint, int metric) {
        super(gcTrace.getGCActivityNamesArray());
        
        this.gcTrace = gcTrace;
        this.checkpoint = checkpoint;
        this.metric = metric;
    }

}
