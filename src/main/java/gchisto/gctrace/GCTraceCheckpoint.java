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
package gchisto.gctrace;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tony
 */
public class GCTraceCheckpoint {

    private GCTrace gcTrace;
    private int gcTraceSize;
    private List<Integer> prevCheckpointedSizes = new ArrayList<Integer>();
    private List<Integer> checkpointedSizes = new ArrayList<Integer>();
    private int prevAllSize;
    private int allSize;

    synchronized public void checkpoint() {
        assert prevCheckpointedSizes.size() == gcTraceSize;
        assert checkpointedSizes.size() == gcTraceSize;

        for (int i = 0; i < gcTraceSize; ++i) {
            int size = checkpointedSizes.get(i);
            prevCheckpointedSizes.set(i, size);
        }
        prevAllSize = allSize;

        for (int i = 0; i < gcTraceSize; ++i) {
            int size = gcTrace.get(i).size();
            checkpointedSizes.set(i, size);
        }
        allSize = gcTrace.getAllGCActivities().size();
    }

    public int gcTraceSize() {
        return gcTraceSize;
    }

    public int prevSize(int index) {
        return prevCheckpointedSizes.get(index);
    }

    public int size(int index) {
        return checkpointedSizes.get(index);
    }

    public int prevAllGCActivitiesSize() {
        return prevAllSize;
    }

    public int allGCActivitiesSize() {
        return allSize;
    }

    public boolean needsCheckpoint() {
        for (int i = 0; i < gcTraceSize; ++i) {
            if (checkpointedSizes.get(i) != gcTrace.get(i).size()) {
                return true;
            }
        }
        if (allSize != gcTrace.getAllGCActivities().size()) {
            return true;
        }
        return false;
    }

    public void extend(int id) {
        assert id == gcTraceSize;
        int newGCTraceSize = gcTraceSize + 1;
        assert prevCheckpointedSizes.size() == newGCTraceSize - 1;
        assert checkpointedSizes.size() == newGCTraceSize - 1;
        prevCheckpointedSizes.add(id, 0);
        checkpointedSizes.add(id, 0);
        assert prevCheckpointedSizes.size() == newGCTraceSize;
        assert checkpointedSizes.size() == newGCTraceSize;
        gcTraceSize = newGCTraceSize;
    }

    private void extendSizes() {
        assert gcTraceSize == 0;
        
        int newGCTraceSize = gcTrace.size();
        for (int i = 0; i < newGCTraceSize; ++i) {
            extend(i);
        }
        
        assert gcTraceSize == newGCTraceSize;
    }

    public GCTraceCheckpoint(GCTrace gcTrace) {
        this.gcTrace = gcTrace;
        this.gcTraceSize = 0;
        this.prevAllSize = 0;
        this.allSize = 0;

        extendSizes();
    }
}
