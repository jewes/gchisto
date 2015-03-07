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
package gchisto.gctracegenerator.file.hotspot;

import gchisto.gctrace.GCTrace;
import gchisto.gctracegenerator.file.GCLogFileReaderThrottle;
import gcparser.GCDataStore;
import gcparser.GCMetric;
import gcparser.GCParserDriver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 *
 * @author tony
 */
public class GCLogFileReader
        implements gchisto.gctracegenerator.file.GCLogFileReader {

    private class MetricData {

        private String name;
        private ArrayList<Double> times;
        private ArrayList<Double> data;
        private int index;
        private int length;

        public boolean hasMore() {
            return index < length;
        }

        public String getName() {
            return name;
        }

        public double getTime() {
            assert hasMore();
            return times.get(index);
        }

        public double getData() {
            assert hasMore();
            return data.get(index);
        }

        public void moveToNext() {
            assert hasMore();
            ++index;
        }

        public MetricData(String name,
                ArrayList<Double> times,
                ArrayList<Double> data) {
            assert times.size() == data.size();

            this.name = name;
            this.times = times;
            this.data = data;
            this.index = 0;
            this.length = times.size();
        }
    }

    private class MetricDataSet extends ArrayList<MetricData> {

        private MetricData last;

        void addMetricData(MetricData data) {
            add(data);
        }

        public boolean hasMore() {
            for (MetricData data : this) {
                if (data.hasMore()) {
                    return true;
                }
            }
            return false;
        }

        public void moveToNext() {
            assert last == getEarliest();
            last.moveToNext();
        }

        public MetricData getEarliest() {
            assert hasMore();

            MetricData ret = null;

            for (MetricData data : this) {
                if (data.hasMore()) {
                    if (ret == null) {
                        ret = data;
                    } else {
                        if (data.getTime() < ret.getTime()) {
                            ret = data;
                        }
                    }
                }
            }
            assert ret != null;

            last = ret;
            return ret;
        }
    }
    final private String[] SHARED_ACTIVITIES = {"Young GC", "Full GC"};
    final private List<String> gcActivityNames = new ArrayList<String>();

    private int mapGCActivityNameToID(String name)
            throws IOException {
        return gcActivityNames.indexOf(name);
    }

    private void ensureGCActivityAdded(GCTrace gcTrace, String name) {
        if (!gcActivityNames.contains(name)) {
            if (name.equals("Remark")) {
                ensureGCActivityAdded(gcTrace, "Initial Mark");
            }

            gcActivityNames.add(name);
            gcTrace.addGCActivityName(gcActivityNames.indexOf(name), name);
        }
    }

    public void setupGCActivityNames(GCTrace gcTrace) {
        for (String name : SHARED_ACTIVITIES) {
            ensureGCActivityAdded(gcTrace, name);
        }
    }

    public void readFile(
            File file,
            GCTrace gcTrace,
            GCLogFileReaderThrottle throttle) throws IOException {
        try {
            throttle.started();

            BitSet actions = new BitSet();
            actions.set(GCParserDriver.COLLECT_DATA);
            GCParserDriver driver = new GCParserDriver(actions);
            driver.parse(file);
            GCDataStore gcData = (GCDataStore) driver.gc_stats();

            ArrayList<Double> ygTimes = gcData.time(GCMetric.ygc_time);
            ArrayList<Double> ygData = gcData.data(GCMetric.ygc_time);
            MetricData youngGCData = new MetricData("Young GC", ygTimes, ygData);

            ArrayList<Double> imTimes = gcData.time(GCMetric.cms_im_time);
            ArrayList<Double> imData = gcData.data(GCMetric.cms_im_time);
            MetricData initialMarkData = new MetricData("Initial Mark", imTimes, imData);

            ArrayList<Double> rmTimes = gcData.time(GCMetric.cms_rm_time);
            ArrayList<Double> rmData = gcData.data(GCMetric.cms_rm_time);
            MetricData remarkData = new MetricData("Remark", rmTimes, rmData);

            ArrayList<Double> fgTimes = gcData.time(GCMetric.fgc_time);
            ArrayList<Double> fgData = gcData.data(GCMetric.fgc_time);
            MetricData fullGCData = new MetricData("Full GC", fgTimes, fgData);

            MetricDataSet set = new MetricDataSet();
            set.addMetricData(youngGCData);
            set.addMetricData(initialMarkData);
            set.addMetricData(remarkData);
            set.addMetricData(fullGCData);

            while (throttle.shouldContinue() && set.hasMore()) {
                MetricData data = set.getEarliest();

                String activityName = data.getName();
                double startSec = data.getTime();
                double durationSec = data.getData();

                throttle.beforeAddingGCActivity(startSec);

                ensureGCActivityAdded(gcTrace, activityName);
                int id = mapGCActivityNameToID(activityName);
                gcTrace.addGCActivity(id, startSec, durationSec);

                throttle.afterAddingGCActivity(startSec);

                set.moveToNext();
            }
        } finally {
            throttle.finished();
        }
    }
}
