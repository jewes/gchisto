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
package gchisto.gui.panels.gcdistribution;

import gchisto.gcactivity.GCActivity;
import gchisto.gcactivity.GCActivitySet;
import gchisto.gctrace.GCTrace;
import gchisto.gctrace.GCTraceCheckpoint;
import gchisto.jfreechart.extensions.AbstractDatasetWithGroups;
import gchisto.jfreechart.extensions.IntervalXYDatasetWithGroups;
import gchisto.utils.Comparisons;
import gchisto.utils.Conversions;
import gchisto.utils.Formatting;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
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
     * The bucket duration in millis.
     */
    static final private double BUCKET_DURATION_MS = 5;
    static final private int INITIAL_BUCKET_LEN = 200;
    static final private double BUCKET_RESIZING_FACTOR = 1.5;
    private int gcTraceSize;
    /**
     * It contains the buckets that will provide the data for the dataset
     * generators. The indexing is buckets[GC activity ID][bucket ID].
     */
    final private List<int[]> buckets = new ArrayList<int[]>();
    /**
     * The number of buckets, one per GC activity set in the GC trace, that
     * ensures that it encompasses all buckets that have non-zero values.
     */
    final private List<Integer> maxIndex = new ArrayList<Integer>();
    private int maxMaxIndex;
    final private GCTrace gcTrace;
    final private GCTraceCheckpoint checkpoint;

    /**
     * It returns the value at the top of a bar for a given GC activity
     * ID / bucket combination.
     *
     * @param series The ID of the GC activity.
     * @param item The bucket index.
     * @return The value at the top of a bar for a given GC activity
     * ID / bucket combination.
     */
    private double getHighValue(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize() :
                "series = " + series + ", length = " + checkpoint.gcTraceSize();
        assert 0 <= item && item <= maxMaxIndex :
                "series = " + series + ", item = " + item + ", max index = " + maxMaxIndex;

        double total = 0.0;
        while (series >= 0) {
            if (isGroupActive(series)) {
                if (item < buckets.get(series).length) {
                    total += buckets.get(series)[item];
                }
            }
            --series;
        }
        return total;
    }

    /**
     * It returns the value at the bottom of a bar for a given GC activity
     * ID / bucket combination.
     *
     * @param series The ID of the GC activity.
     * @param item The bucket index.
     * @return The value at the bottom of a bar for a given GC activity
     * ID / bucket combination.
     */
    private double getLowValue(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize() :
                "series = " + series + ", length = " + checkpoint.gcTraceSize();
        assert 0 <= item && item <= maxMaxIndex :
                "series = " + series + ", item = " + item + ", max index = " + maxMaxIndex;

        --series;
        while (series >= 0) {
            if (isGroupActive(series)) {
                return getHighValue(series, item);
            }
            --series;
        }
        return 0.0;
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
     * It returns the number of series (e.g., GC activities) in
     * the current GC trace.
     *
     * @return The number of series (e.g., GC activities) in the
     * current GC trace.
     */
    public int getSeriesCount() {
        return getGroupCount();
    }

    /**
     * It returns the series key (e.g., the name of the GC activity)
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
     * the GC activity with the given name).
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
     * number of buckets for a given GC activity). If the
     * series is not currently active, this method will return 0.
     *
     * @param series The series whose item number will be returned.
     * @return The number of items in the given series, or 0 if the
     * series is not active.
     */
    public int getItemCount(int series) {
        assert 0 <= series && series < checkpoint.gcTraceSize();

        return (isGroupActive(series)) ? maxIndex.get(series) + 1 : 0;
    }

    /**
     * It returns the x-value for a given bucket. The x-value here
     * is defined here as the middle of the time between the start of
     * a bucket and the start of the next bucket.
     *
     * @param series The GC activity ID within the current GC trace.
     * @param item The bucket index.
     * @return The x-value for the given bucket.
     *
     * @see #getX(int, int)
     * @see #getStartX(int, int)
     * @see #getStartXValue(int, int)
     * @see #getEndX(int, int)
     * @see #getEndXValue(int, int)
     */
    public Number getX(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item <= maxIndex.get(series);

        return getXValue(series, item);
    }

    /**
     * It returns the x-value for a given bucket. The x-value here
     * is defined here as the middle of the time between the start of
     * a bucket and the start of the next bucket.
     *
     * @param series The GC activity ID within the current GC trace.
     * @param item The bucket index.
     * @return The x-value for the given bucket.
     *
     * @see #getX(int, int)
     * @see #getStartX(int, int)
     * @see #getStartXValue(int, int)
     * @see #getEndX(int, int)
     * @see #getEndXValue(int, int)
     */
    public double getXValue(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item <= maxIndex.get(series);

        return getBucketStartSec(item) + bucketDurationSec() / 2.0;
    }

    /**
     * It returns the y-value for a given bucket. The y-value here
     * is defined here as the sum of the bucket values for all active GC
     * activities with ID less than or equal to the given one.
     *
     * @param series The GC activity ID within the current GC trace.
     * @param item The bucket index.
     * @return The y-value for the given bucket.
     *
     * @see #getY(int, int)
     * @see #getStartY(int, int)
     * @see #getStartYValue(int, int)
     * @see #getEndY(int, int)
     * @see #getEndYValue(int, int)
     */
    public Number getY(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item <= maxIndex.get(series);

        return getYValue(series, item);
    }

    /**
     * It returns the y-value for a given bucket. The y-value here
     * is defined here as the sum of the bucket values for all active GC
     * activities with ID less than or equal to the given one.
     *
     * @param series The GC activity ID within the current GC trace.
     * @param item The bucket index.
     * @return The y-value for the given bucket.
     *
     * @see #getYValue(int, int)
     * @see #getStartY(int, int)
     * @see #getStartYValue(int, int)
     * @see #getEndY(int, int)
     * @see #getEndYValue(int, int)
     */
    public double getYValue(int series, int item) {
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item <= maxIndex.get(series);

        return getHighValue(series, item);
    }

    /**
     * It returns the start x-value for the given bucket. This value
     * here is the start time of the bucket.
     *
     * @param series The GC activity ID within the current GC trace.
     * @param item The bucket index.
     * @return The start x-value for the given bucket.
     *
     * @see #getX(int, int)
     * @see #getXValue(int, int)
     * @see #getStartXValue(int, int)
     * @see #getEndX(int, int)
     * @see #getEndXValue(int, int)
     */
    public Number getStartX(int series, int item) {
        assert isGroupActive(series);
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item <= maxIndex.get(series);

        return getStartXValue(series, item);
    }

    /**
     * It returns the start x-value for the given bucket. This value
     * here is the start time of the bucket.
     *
     * @param series The GC activity ID within the current GC trace.
     * @param item The bucket index.
     * @return The start x-value for the given bucket.
     *
     * @see #getXValue(int, int)
     * @see #getXValue(int, int)
     * @see #getStartXValue(int, int)
     * @see #getEndX(int, int)
     * @see #getEndXValue(int, int)
     */
    public double getStartXValue(int series, int item) {
        assert isGroupActive(series) : "series = " + series;
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item <= maxIndex.get(series);

        return getBucketStartSec(item);
    }

    /**
     * It returns the end x-value for the given GC activity. This value
     * here is the end time of the bucket.
     *
     * @param series The GC activity ID within the current GC trace.
     * @param item The bucket index.
     * @return The end x-value for the given bucket.
     *
     * @see #getX(int, int)
     * @see #getXValue(int, int)
     * @see #getStartX(int, int)
     * @see #getEndX(int, int)
     * @see #getEndXValue(int, int)
     */
    public Number getEndX(int series, int item) {
        assert isGroupActive(series);
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item <= maxIndex.get(series);

        return getEndXValue(series, item);
    }

    /**
     * It returns the end x-value for the given GC activity. This value
     * here is the end time of the bucket.
     *
     * @param series The GC activity ID within the current GC trace.
     * @param item The bucket index.
     * @return The end x-value for the given bucket.
     *
     * @see #getXValue(int, int)
     * @see #getXValue(int, int)
     * @see #getStartX(int, int)
     * @see #getEndX(int, int)
     * @see #getEndXValue(int, int)
     */
    public double getEndXValue(int series, int item) {
        assert isGroupActive(series);
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item <= maxIndex.get(series);

        return getBucketStartSec(item + 1);
    }

    /**
     * It returns the start y-value for the given GC activity. This value
     * here is the start y-value of the preceding GC activity, or 0.
     *
     * @param series The GC activity ID within the current GC trace.
     * @param item The bucket index.
     * @return The start y-value for the given bucket.
     *
     * @see #getY(int, int)
     * @see #getYValue(int, int)
     * @see #getStartYValue(int, int)
     * @see #getEndY(int, int)
     * @see #getEndYValue(int, int)
     */
    public Number getStartY(int series, int item) {
        assert isGroupActive(series);
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item <= maxIndex.get(series);

        return getStartYValue(series, item);
    }

    /**
     * It returns the start y-value for the given GC activity. This value
     * here is the start y-value of the preceding GC activity, or 0.
     *
     * @param series The GC activity ID within the current GC trace.
     * @param item The bucket index.
     * @return The start y-value for the given bucket.
     *
     * @see #getYValue(int, int)
     * @see #getYValue(int, int)
     * @see #getStartYValue(int, int)
     * @see #getEndY(int, int)
     * @see #getEndYValue(int, int)
     */
    public double getStartYValue(int series, int item) {
        assert isGroupActive(series) : "series = " + series;
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item <= maxIndex.get(series);

        return getLowValue(series, item);
    }

    /**
     * It returns the end y-value for the given GC activity. This value
     * here is the sum of the bucket values of all active GC activities
     * with ID less than or equal to the given one.
     *
     * @param series The GC activity ID within the current GC trace.
     * @param item The bucket index.
     * @return The end y-value for the given bucket.
     *
     * @see #getY(int, int)
     * @see #getYValue(int, int)
     * @see #getStartY(int, int)
     * @see #getStartYValue(int, int)
     * @see #getEndYValue(int, int)
     */
    public Number getEndY(int series, int item) {
        assert isGroupActive(series);
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item <= maxIndex.get(series);

        return getEndYValue(series, item);
    }

    /**
     * It returns the end y-value for the given GC activity. This value
     * here is the sum of the bucket values of all active GC activities
     * with ID less than or equal to the given one.
     *
     * @param series The GC activity ID within the current GC trace.
     * @param item The bucket index.
     * @return The end y-value for the given bucket.
     *
     * @see #getYValue(int, int)
     * @see #getYValue(int, int)
     * @see #getStartY(int, int)
     * @see #getStartYValue(int, int)
     * @see #getEndYValue(int, int)
     */
    public double getEndYValue(int series, int item) {
        assert isGroupActive(series);
        assert 0 <= series && series < checkpoint.gcTraceSize();
        assert 0 <= item && item <= maxIndex.get(series);

        return getHighValue(series, item);
    }

    public String generateToolTip(XYDataset dataset, int series, int item) {
        String gcActivityName = (String) getSeriesKey(series);
        double startSec = getBucketStartSec(item);
        double endSec = getBucketStartSec(item + 1);
        int value = buckets.get(series)[item];
        String str = String.format("%s count in [%s sec, %s sec) = %s",
                gcActivityName,
                Formatting.formatDouble(startSec),
                Formatting.formatDouble(endSec),
                Formatting.formatInt(value));
        return str;
    }
    /**
     * It returns the duration of a bucket in millis.
     *
     * @return The duration of a bucket in millis.
     */
    static public double bucketDurationMS() {
        return BUCKET_DURATION_MS;
    }

    /**
     * It returns the duration of a bucket in seconds.
     *
     * @return The duration of a bucket in seconds.
     */
    static public double bucketDurationSec() {
        return Conversions.msToSec(BUCKET_DURATION_MS);
    }

    /**
     * It returns the index of the bucket that contains the given duration.
     *
     * @param durationSec The duration for which the containing bucket will
     * be returned.
     * @return The index of the bucket that contains the given duration.
     */
    private int getBucketIndex(double durationSec) {
        assert Comparisons.gteq(durationSec, 0.0);

        int index = (int) (durationSec / bucketDurationSec());
        if (index < 0) {
            index = 0;
        }
        return index;
    }

    /**
     * It returns the starting duration of the given bucket index.
     *
     * @param index A bucket index.
     * @return The starting duration of the given bucket index.
     */
    private double getBucketStartSec(int index) {
        assert index >= 0;

        return (double) index * bucketDurationSec();
    }

    private void incrementBucket(int id, int bucketIndex) {
        int[] bucket = buckets.get(id);
        int bucketLen = bucket.length;
        if (bucketIndex >= bucketLen) {
            int newLen = (int) ((double) bucketIndex * BUCKET_RESIZING_FACTOR);
            int[] newBucket = new int[newLen];
            assert newBucket.length == newLen;
            System.arraycopy(bucket, 0, newBucket, 0, bucketLen);

            buckets.set(id, newBucket);
            bucket = newBucket;
            bucketLen = newLen;
        }
        assert bucketIndex < bucketLen;
        bucket[bucketIndex] += 1;

        if (bucketIndex > maxIndex.get(id)) {
            maxIndex.set(id, bucketIndex);
        }
    }

    private void updateMaxMaxIndex() {
        for (int i = 0; i < gcTraceSize; ++i) {
            if (maxIndex.get(i) > maxMaxIndex) {
                maxMaxIndex = maxIndex.get(i);
            }
        }
    }

    public void updateBuckets() {
        assert buckets.size() == gcTraceSize;
        assert maxIndex.size() == gcTraceSize;

        for (int i = 0; i < gcTraceSize; ++i) {
            GCActivitySet gcActivitySet = gcTrace.get(i);

            int from = checkpoint.prevSize(i);
            int to = checkpoint.size(i);
            for (int j = from; j < to; ++j) {
                GCActivity gcActivity = gcActivitySet.get(j);
                if (gcActivity.isSTW()) {
                    double durationSec = gcActivity.getDurationSec();
                    int index = getBucketIndex(durationSec);
                    incrementBucket(i, index);
                }
            }
        }
        updateMaxMaxIndex();
    }

    public void addGCActivity(int id, String groupName) {
        addGroup(id, groupName);

        assert id == gcTraceSize;
        int[] newBucket = new int[INITIAL_BUCKET_LEN];
        buckets.add(id, newBucket);
        maxIndex.add(id, 0);
        gcTraceSize += 1;
        assert id + 1 == gcTraceSize;
    }

    private void addGCActivities() {
        int newGCTraceSize = checkpoint.gcTraceSize();
        for (int i = 0; i < newGCTraceSize; ++i) {
            addGCActivity(i, gcTrace.get(i).getGCActivityName());
        }
        assert newGCTraceSize == gcTraceSize;
    }

    /**
     * It creates a new instance of this dataset generator.
     *
     * @param gcTraceSet The GC trace set that contains the GC trace that will
     * provide the data for the new dataset generator.
     * @param gcTrace The GC trace that will provide data for the
     * new dataset generator.
     */
    public Dataset(GCTrace gcTrace, GCTraceCheckpoint checkpoint) {
        super();

        this.gcTrace = gcTrace;
        this.checkpoint = checkpoint;
        this.gcTraceSize = 0;

        addGCActivities();
        updateBuckets();
    }

}
