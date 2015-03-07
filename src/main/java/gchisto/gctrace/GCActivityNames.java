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

import gchisto.utils.errorchecking.ArgumentChecking;
import java.util.ArrayList;

/**
 * It is a map that keeps track of GC activity names associated with a GC trace.
 * It can produce a unique string for each GC activity name, to avoid
 * replication and fast string comparison. It also associates a unique ID with
 * each GC activity name. The allocated IDs start from 0 and then monotonically
 * increase as new GC activity names are added to the map.
 *
 * @author Tony Printezis
 * @see    gchisto.gctraceset.GCTrace
 * @see    gchisto.gctraceset.GCTraceSet
 */
public class GCActivityNames extends ArrayList<String> {
    
    /**
     * It returns an array that contains all the unique strings of the GC
     * activity names that have been added to this map. The index of each
     * GC activity name in the array is the same as its ID in the map.
     *
     * @return An array that contains all the unique strings of the GC activity
     * names that have been added to this map.
     */
    public String[] getNames() {
        return toArray(new String[size()]);
    }
    
    /**
     * It iterates over the GC activity names of the parameter and, any of
     * them which do not exist in this map, it will add them.
     *
     * @param gcActivityNames The GC activity names that will be merged
     * with this map.
     */
    public void merge(GCActivityNames gcActivityNames) {
        ArgumentChecking.notNull(gcActivityNames, "gcActivityNames");
        
        for (String activityName : gcActivityNames.getNames()) {
            if (!contains((activityName))) {
                add(activityName);
            }
        }
    }
    
}
