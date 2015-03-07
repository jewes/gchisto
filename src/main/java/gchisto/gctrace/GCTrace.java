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

import gchisto.gcactivity.GCActivity;
import gchisto.gcactivity.GCActivitySet;
import gchisto.utils.errorchecking.ArgumentChecking;
import java.util.ArrayList;
import java.util.Date;

/**
 * It represents a GC trace. It contains a set of GC activity sets,
 * one for each of the different GC activities that appear in the GC trace file.
 * <p>
 * Because it extends <tt>java.util.ArrayList</tt>, an iteration over the GC
 * activitiy sets in it can be easily done using the standard for-loop over
 * collections.
 *
 * @author Tony Printezis
 * @see    gchisto.gcactivityset.GCActivitySet
 * @see    gchisto.gctraceset.GCActivityNames
 * @see    gchisto.gctraceset.GCTraceSet
 */
public abstract class GCTrace extends ArrayList<GCActivitySet> {

    /**
     * The name that is associated with this GC trace. This is unique across
     * all the GC traces that are included in a single GC trace set.
     *
     * @see #getName()
     * @see #setName(String)
     * @see gchisto.gctraceset.GCTraceSet#createUniqueGCTraceName(File)
     */
    private String name;
    /**
     * The date/time when this GC trace was populated.
     *
     * @see #getAddedDate()
     * @see #setAddedDate(Date)
     *
     * TODO
     */
    private Date addedDate;
    /**
     * A map of the GC activity names that appear in this GC trace.
     */
    final private GCActivityNames gcActivityNames = new GCActivityNames();
    /**
     * TODO
     */
    final private GCTraceListenerSet listeners = new GCTraceListenerSet();
    final private Object hashCodeObject = new Object();
    final private GCActivitySet allGCActivities = new GCActivitySet("All");
    private double lastTimeStampSec;

    /**
     * TODO
     */
    abstract public String getSuggestedName();

    /**
     * TODO
     */
    abstract public String getInfoString();

    public boolean equals(Object gcTrace) {
        assert gcTrace instanceof GCTrace;
        return ((GCTrace) gcTrace).hashCodeObject == hashCodeObject;
    }

    public int hashCode() {
        return hashCodeObject.hashCode();
    }

    /**
     * It returns the name that is associated with this GC trace. This is
     * unique across all the GC traces that are included in a single GC trace
     * set.
     *
     * @return The name that is associated with this GC trace. This is
     * unique across all the GC traces that are included in a single GC trace
     * set.
     *
     * @see #setName(String)
     * @see gchisto.gctraceset.GCTraceSet#createUniqueGCTraceName(File)
     */
    public String getName() {
        return name;
    }
    
    public String getLongName() {
        return getName();
    }

    /**
     * It returns the date/time when this GC trace was populated.
     *
     * @return The date/time when this GC trace was populated.
     * @see #setAddedDate(Date)
     *
     * TODO
     */
    public Date getAddedDate() {
        return addedDate;
    }

    /**
     * It returns a map of the GC activity names that appear in this GC trace.
     *
     * @return A map of the GC activity names that appear in this GC trace.
     */
    public GCActivityNames getGCActivityNames() {
        return gcActivityNames;
    }

    public GCActivitySet getAllGCActivities() {
        return allGCActivities;
    }

    public double getLastTimeStampSec() {
        return lastTimeStampSec;
    }
    
    /**
     * It returns an array containing the GC activity names that appear in
     * this GC trace.
     *
     * @return An array containing the GC activity names that appear in this
     * GC trace.
     */
    public String[] getGCActivityNamesArray() {
        return gcActivityNames.getNames();
    }

    /**
     * It sets the name of this GC trace.
     *
     * @param name The new name of this GC trace.
     *
     * @see #getName()
     * @see gchisto.gctraceset.GCTraceSet#createUniqueGCTraceName(File)
     */
    public void setName(String name) {
        ArgumentChecking.notNull(name, "name");

        this.name = name;
    }

    /**
     * It sets the read date/time of this GC trace.
     *
     *
     * @param addedDate The new read date/time of this GC trace.
     * @see #getAddedDate()
     *
     * TODO
     */
    public void setAddedDate(Date addedDate) {
        ArgumentChecking.notNull(addedDate, "addedDate");

        this.addedDate = addedDate;
    }

    /**
     * It adds a new GC activity to this GC trace. The GC activity will be added
     * to the GC activity set that corresponds to the given GC activity name.
     * If a GC activity set does not exist in this GC trace for this GC activity
     * name, it will be created. This version should be used for concurrent
     * GC activities.
     *
     * @param gcActivityName The name of the GC activity to be added.
     * @param startSec The time stamp of the start of the GC activity to
     * be added, in seconds.
     * @param durationSec The duration of the GC activity, in seconds.
     */
    public void addGCActivity(
            int id,
            double startSec,
            double durationSec) {
        ArgumentChecking.withinBounds(id, 0, size() - 1, "id");

        String gcActivityName = gcActivityNames.get(id);
        addGCActivity(id,
                new GCActivity(gcActivityName, startSec, durationSec));
    }

    /**
     * It adds a new GC activity to this GC trace. The GC activity will be added
     * to the GC activity set that corresponds to the given GC activity name.
     * If a GC activity set does not exist in this GC trace for this GC activity
     * name, it will be created. This version should be used for stop-the-world
     * GC activities.
     *
     * @param gcActivityName The name of the GC activity to be added.
     * @param startSec The time stamp of the start of the GC activity to
     * be added, in seconds.
     * @param durationSec The duration of the GC activity, in seconds.
     * @param overheadPerc The concurrent overhead of the GC activity to
     * be added.
     */
    public void addGCActivity(
            int id,
            double startSec,
            double durationSec,
            double overheadPerc) {
        ArgumentChecking.withinBounds(id, 0, size() - 1, "id");

        String gcActivityName = gcActivityNames.get(id);
        addGCActivity(id, new GCActivity(
                gcActivityName,
                startSec, durationSec,
                overheadPerc));
    }

    /**
     * It adds a new GC activity to this GC trace. This is a private method
     * that is used by all the public ones.
     *
     * @param gcActivityName The name of the GC activity to be added.
     * @param gcActivity The GC activity to be added.
     */
    synchronized private void addGCActivity(
            int id,
            GCActivity gcActivity) {
        assert 0 <= id && id < size();
        assert 0 <= id && id < gcActivityNames.size();
        assert gcActivityNames.get(id).equals(gcActivity.getName());

        GCActivitySet gcActivitySet = get(id);
        gcActivitySet.addGCActivity(gcActivity);
        allGCActivities.addGCActivity(gcActivity);
        lastTimeStampSec = gcActivity.getEndSec();

        listeners.callGCActivityAdded(this, gcActivitySet, gcActivity);
    }

    public void addGCActivityName(int id, String gcActivityName) {
        assert gcActivityNames.size() == id;
        gcActivityNames.add(id, gcActivityName);
        assert gcActivityNames.size() == id + 1;

        assert size() == id;
        GCActivitySet gcActivitySet = new GCActivitySet(gcActivityName);
        add(id, gcActivitySet);
        assert size() == id + 1;

        listeners.callGCActivityNameAdded(this, id, gcActivityName);
    }

    /**
     * TODO
     */
    synchronized public void addListener(GCTraceListener listener) {
        ArgumentChecking.notNull(listener, "listener");

        listeners.add(listener);
    }

    /**
     * TODO
     */
    synchronized public void removeListener(GCTraceListener listener) {
        ArgumentChecking.notNull(listener, "listener");

        listeners.remove(listener);
    }

    /**
     * TODO
     */
    public void afterAddingToGCTraceSet() {
    // do nothing, unless overriden
    }

    /**
     * TODO
     */
    public void beforeRemovingFromGCTraceSet() {
    // do nothing, unless overriden
    // this has to return before the GC trace is removed from the GC trace set
    }

    /**
     * It creates a new GC trace instance.
     *
     * @param file The file associated with the new GC trace.
     * @param lastModifiedDate The last modified date/time of the new GC trace.
     * @param addedDate Thenew read date/time of the new GC trace.
     *
     * TODO
     */
    public GCTrace() {
    }
}
