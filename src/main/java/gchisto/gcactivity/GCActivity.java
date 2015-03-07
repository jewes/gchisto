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
package gchisto.gcactivity;

import gchisto.utils.errorchecking.ArgumentChecking;

/**
 * Each instance of this class represents a single piece of GC activity.
 * It can be either stop-the-world or concurrent.
 *
 * @author Tony Printezis
 * @see    gchisto.gcactivityset.GCActivitySet
 */
public class GCActivity {
    
    private String name;
    
    /**
     * The time stamp of the start of the GC activity, in seconds.
     *
     * @see #getStartSec()
     */
    private double startSec;
    
    /**
     * The duration of the GC activity, in seconds.
     *
     * @see #getDurationSec()
     */
    private double durationSec;
    
    /**
     * It indicates whether the GC activity is stop-the-world (true),
     * or concurrent (false).
     *
     * @see #isSTW()
     */
    private boolean stw;
    
    /**
     * The overhead percentage of the GC activity.
     *
     * @see #getOverheadPerc()
     */
    private double overheadPerc;
    
    public String getName() {
        return name;
    }
    
    /**
     * It returns the time stamp of the start of the GC activity, in seconds. 
     *
     * @return The time stamp of the start of the GC activity, in seconds. 
     */
    public double getStartSec() {
        return startSec;
    }
    
    /**
     * It returns the time stamp of the end of the GC activity, in seconds. 
     *
     * @return The time stamp of the end of the GC activity, in seconds. 
     */
    public double getEndSec() {
        return startSec + durationSec;
    }
    
    /**
     * It returns the duration of the GC activity, in seconds. 
     *
     * @return The duration of the GC activity, in seconds. 
     */
    public double getDurationSec() {
        return durationSec;
    }
    
    /**
     * It returns whether the GC activity is stop-the-world.
     *
     * @return Whether the GC activity is stop-the-world.
     */
    public boolean isSTW() {
        return stw;
    }
    
    /**
     * It returns whether the GC activity is concurrent.
     *
     * @return Whether the GC activity is concurrent.
     */
    public boolean isConcurrent() {
        return !stw;
    }
    
    /**
     * It returns the overhead percentage of the GC activity.
     *
     * @return The overhead percentage of the GC activity.
     */
    public double getOverheadPerc() {
        return overheadPerc;
    }
    
    /**
     * It creates a new GC activity instance. This version should be used for
     * stop-the-world GC activities.
     *
     * @param startSec The time stamp of the start of the GC activity, in
     * seconds.
     * @param durationSec The duration of the GC activity, in seconds.
     */
    public GCActivity(
            String name,
            double startSec,
            double durationSec) {
        this(name, startSec, durationSec, true, 100.0);
    }
    
    /**
     * It creates a new GC activity instance. This version should be used for
     * concurrent GC activities.
     *
     * @param startSec The time stamp of the start of the GC activity, in
     * seconds.
     * @param durationSec The duration of the GC activity, in seconds.
     * @param overheadPerc The concurrent overhead of the GC activity.
     */
    public GCActivity(
            String name,
            double startSec,
            double durationSec,
            double overheadPerc) {
        this(name, startSec, durationSec, false, overheadPerc);
    }
    
    /**
     * It creates a new GC activity instance. This is a private constructor
     * for use by the public ones.
     *
     * @param startSec The time stamp of the start of the GC activity, in
     * seconds.
     * @param durationSec The duration of the GC activity, in seconds.
     * @param stw It indicates whether the GC activity is stop-the-world (true),
     * or concurrent (false).
     * @param overheadPerc The concurrent overhead of the GC activity.
     */
    private GCActivity(
            String name,
            double startSec,
            double durationSec,
            boolean stw,
            double overheadPerc) {
        ArgumentChecking.lowerBound(startSec, 0.0, "startSec");
        ArgumentChecking.lowerBound(durationSec, 0.0, "durationSec");
        ArgumentChecking.withinBounds(overheadPerc, 0.0, 100.0, "oveheadPerc");
        
        this.name = name;
        this.startSec = startSec;
        this.durationSec = durationSec;
        this.stw = stw;
        this.overheadPerc = overheadPerc;
    }
    
}
