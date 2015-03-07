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
import gchisto.utils.errorchecking.ErrorReporting;
import java.util.Date;
import java.util.LinkedList;

/**
 * A set of GC traces. Each GC trace is associated with a unique name, as well
 * as an index, which is the position of the GC trace in the GC trace set list,
 * starting from 0. This is the data structure from which all the data
 * in the loaded GC traces are reachable from.
 * <p>
 * Because it extends <tt>java.util.LinkedList</tt>, an iteration over the GC
 * activities in it can be easily done using the standard for-loop over
 * collections.
 *
 * @author Tony Printezis
 * @see    GCTrace
 * @see    GCTraceSetListener
 * @see    java.util.LinkedList
 */
public class GCTraceSet extends LinkedList<GCTrace> implements GCTraceListener {
    
    /**
     * The map that contains all the GC activity names of all the
     * GC traces added to this set.
     *
     * @see #recreateAllGCActivityNames()
     */
    private GCActivityNames allGCActivityNames = new GCActivityNames();
    
    /**
     * The GC trace set listeners.
     *
     * @see #addListener(GCTraceSetListener)
     * @see #removeListener(GCTraceSetListener)
     */
    final private GCTraceSetListenerSet listeners = new GCTraceSetListenerSet();
    
    /**
     * It creates a name for the GC trace that is associated with the
     * given file that is unique in this GC trace set. Typically, the name will
     * be the name of the file, let's call it NAME, without the associated path
     * information. If that is not unique, a suffix .NUM will be added, where
     * NUM is an integer. NUM will start from 0 and will increase until
     * NAME.NUM is unique.
     *
     * @param file The file for which a unique name will be created.
     * @return A unique GC trace name for the given file.
     *
     * TODO
     */
    private String createUniqueGCTraceName(GCTrace gcTrace) {
        assert gcTrace != null;
        
        String originalName = gcTrace.getSuggestedName();
        String name = originalName;
        int i = 0;
        while (findGCTrace(name) != null) {
            ++i;
            name = originalName + "." + i;
        }
        return name;
    }
    
    /**
     * It iterates over the GC traces in this set and recreates
     * the map that contains all the GC activity names.
     */
    private void recreateAllGCActivityNames() {
        allGCActivityNames = new GCActivityNames();
        for (GCTrace trace : this) {
            GCActivityNames gcActivityNames =  trace.getGCActivityNames();
            allGCActivityNames.merge(gcActivityNames);
            
        }
    }
    
    /**
     * It finds the GC trace associated with the given name.
     *
     * @param gcTraceName The name of the GC trace to be looked up.
     * @return The GC trace associated with the given name, or <tt>null</tt>
     * if the name does not appear in this GC trace set.
     */
    public GCTrace findGCTrace(String gcTraceName) {
        ArgumentChecking.notNull(gcTraceName, "gcTraceName");
        
        for (GCTrace trace : this) {
            if (trace.getName().equals(gcTraceName)) {
                return trace;
            }
        }
        return null;
    }
    
    /**
     * It finds the GC trace with the given index.
     *
     * @param index The index of the GC trace to be looked up.
     * @return The GC trace associated with the given index.
     */
    public GCTrace findGCTrace(int index) {
        ArgumentChecking.withinBounds(index, 0, size() - 1, "index");
        
        return get(index);
    }
    
    /**
     * It finds the index of the GC trace associated with the given name.
     *
     * @param gcTraceName The name of the GC trace to be looked up.
     * @return The index of the GC trace associated with the given name,
     * or <tt>-1</tt> if the name does not appear in this GC trace set.
     */
    public int findGCTraceIndex(String gcTraceName) {
        ArgumentChecking.notNull(gcTraceName, "gcTraceName");
        
        int index = 0;
        for (GCTrace trace : this) {
            if (trace.getName().equals(gcTraceName)) {
                return index;
            }
            ++index;
        }
        return -1;
    }
    
    /**
     * It adds a new GC trace to this set. Before adding it, it will set
     * its name to one that is based on the file of the GC trace and
     * that is unique in this set. After adding it, it will call the
     * <tt>gcTraceAdded()</tt> method on the listeners of this set.
     *
     * @param gcTrace The new GC trace to be added to this set.
     *
     * @see #createUniqueGCTraceName(File)
     * @see gchisto.gctraceset.GCTraceSetListener#gcTraceAdded(GCTrace)
     *
     * TODO
     */
    synchronized public void addGCTrace(GCTrace gcTrace) {
        ArgumentChecking.notNull(gcTrace, "gcTrace");
        
        String gcTraceName = createUniqueGCTraceName(gcTrace);
        gcTrace.setName(gcTraceName);
        gcTrace.setAddedDate(new Date(System.currentTimeMillis()));
        gcTrace.addListener(this);
        add(gcTrace);
        
        recreateAllGCActivityNames();
        listeners.callGCTraceAdded(gcTrace);
        gcTrace.afterAddingToGCTraceSet();
    }
    
    /**
     * It renames a GC trace. If the new name already exists, it will do
     * nothing. If the new name does not exist, it will set the name of
     * the GC trace to the new one. After renaming it, it will call the
     * <tt>gcTraceRenamed()</tt> method on the listeners of this set.
     *
     * @param gcTraceName The name of the GC trace to be renamed.
     * @param newName The new name of the GC trace.
     *
     * @see gchisto.gctraceset.GCTraceSetListener#gcTraceRenamed(GCTrace)
     */
    synchronized public void rename(String gcTraceName, String newName) {
        ArgumentChecking.notNull(gcTraceName, "gcTraceName");
        ArgumentChecking.notNull(newName, "newName");
        
        GCTrace gcTrace = findGCTrace(gcTraceName);
        ErrorReporting.fatalError(gcTrace != null,
                gcTraceName + " does not exist in the GC trace set.");
        if (findGCTrace(newName) == null) {
            gcTrace.setName(newName);
            listeners.callGCTraceRenamed(gcTrace);
        } else {
            ErrorReporting.warning("GC trace name " + newName +
                    " already exists.");
        }
    }
    
    /**
     * It removes the GC trace associated with the given name from this set.
     * After removing it, it will call the <tt>gcTraceRemoved</tt> method
     * on the listeners of this set.
     *
     * @param gcTraceName The name of the GC trace to be removed.
     *
     * @see gchisto.gctraceset.GCTraceSetListener#gcTraceRemoved(GCTrace)
     */
    synchronized public void remove(String gcTraceName) {
        ArgumentChecking.notNull(gcTraceName, "gcTraceName");
        
        GCTrace gcTrace = findGCTrace(gcTraceName);
        ErrorReporting.fatalError(gcTrace != null,
                gcTraceName + " does not exist in the GC trace set");
        gcTrace.beforeRemovingFromGCTraceSet();
        boolean ret = super.remove(gcTrace);
        assert ret;
        
        recreateAllGCActivityNames();
        listeners.callGCTraceRemoved(gcTrace);
    }
    
    /**
     * It moves the GC trace associated with the given name up in the order
     * in this set, so that its index is its old index minus 1. If its
     * index is 0, then it does nothing. After moving it, it will call
     * the <tt>gcTraceMovedUp</tt> method on the listeners of this set.
     *
     * @param gcTraceName The name of the GC trace to be moved.
     *
     * @see gchisto.gctraceset.GCTraceSetListener#gcTraceMovedUp(GCTrace)
     */
    synchronized public void moveUp(String gcTraceName) {
        ArgumentChecking.notNull(gcTraceName, "gcTraceName");
        
        GCTrace gcTrace = findGCTrace(gcTraceName);
        ErrorReporting.fatalError(gcTrace != null,
                gcTraceName + " does not exist in the GC trace set");
        int index = indexOf(gcTrace);
        assert 0 <= index && index < size();
        if (index > 0) {
            super.remove(gcTrace);
            add(index - 1, gcTrace);
            listeners.callGCTraceMovedUp(gcTrace);
        } else {
            ErrorReporting.warning("GC trace " + gcTraceName +
                    " already at small index.");
        }
    }
    
    /**
     * It moves the GC trace associated with the given name down in the order
     * in this set, so that its index is its old index plus 1. If its
     * index is the highest, then it does nothing. After moving it, it will call
     * the <tt>gcTraceMovedDown</tt> method on the listeners of this set.
     *
     * @param gcTraceName The name of the GC trace to be moved.
     *
     * @see gchisto.gctraceset.GCTraceSetListener#gcTraceMovedDown(GCTrace)
     */
    synchronized public void moveDown(String gcTraceName) {
        ArgumentChecking.notNull(gcTraceName, "gcTraceName");
        
        GCTrace gcTrace = findGCTrace(gcTraceName);
        ErrorReporting.fatalError(gcTrace != null,
                gcTraceName + " does not exist in the GC trace set");
        int index = indexOf(gcTrace);
        assert 0 <= index && index < size();
        if (index < (size() - 1)) {
            super.remove(gcTrace);
            add(index + 1, gcTrace);
            listeners.callGCTraceMovedDown(gcTrace);
        } else {
            ErrorReporting.warning("GC trace " + gcTraceName +
                    " already at highest index.");
        }
    }
    
    /**
     * It adds a listener to this set.
     *
     * @param listener The listener to be added to this set.
     */
    synchronized public void addListener(GCTraceSetListener listener) {
        ArgumentChecking.notNull(listener, "listener");
        
        listeners.add(listener);
    }
    
    /**
     * It removes a listener from this set.
     *
     * @param listener The listener to be removed from this set.
     */
    synchronized public void removeListener(GCTraceSetListener listener) {
        ArgumentChecking.notNull(listener, "listener");
        
        listeners.remove(listener);
    }
    
    public GCActivityNames getAllGCActivityNames() {
        return allGCActivityNames;
    }
    
    /**
     * It returns the ID of the given GC activity name in the map that
     * contains all the GC activity names that appear in the GC traces of
     * this set.
     *
     * @param gcActivityName The GC activity name that will be looked up.
     * @return The ID of the given GC activity name in the map that
     * contains all the GC activity names that appear in the GC traces of
     * this set.
     */
    public int getActivityID(String gcActivityName) {
        return allGCActivityNames.indexOf(gcActivityName);
    }
    
    public void gcActivityAdded(
            GCTrace gcTrace,
            GCActivitySet gcActivitySet,
            GCActivity gcActivity) {
    }

    public void gcActivityNameAdded(GCTrace gcTrace,
            int id,
            String gcActivityName) {
        recreateAllGCActivityNames();
    }
    
    /**
     * It creates a new GC trace set instance.
     */
    public GCTraceSet() {
    }

}
