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

import gchisto.utils.ListenerSet;
import gchisto.utils.errorchecking.ArgumentChecking;

/**
 * A set of GC trace set listeners.
 *
 * @author Tony Printezis
 * @see    gchisto.gctraceset.GCTraceSet
 * @see    gchisto.gctraceset.GCTraceSetListener
 */
public class GCTraceSetListenerSet extends ListenerSet<GCTraceSetListener> {
    
    /**
     * It calls the <tt>gcTraceAdded()</tt> method on all the listeners in
     * the listener set.
     *
     * @param gcTrace The GC trace that has just been added to the GC
     * trace set.
     * 
     * @see gchisto.gctraceset.GCTraceSetListener#gcTraceAdded(GCTrace)
     */
    public void callGCTraceAdded(GCTrace gcTrace) {
        ArgumentChecking.notNull(gcTrace, "gcTrace");
        
        for (GCTraceSetListener listener : listeners()) {
            listener.gcTraceAdded(gcTrace);
        }
    }
    
    /**
     * It calls the <tt>gcTraceRenamed()</tt> method on all the listeners in
     * the listener set.
     *
     * @param gcTrace The GC trace that has just been renamed in the GC
     * trace set.
     * 
     * @see gchisto.gctraceset.GCTraceSetListener#gcTraceRenamed(GCTrace)
     */
    public void callGCTraceRenamed(GCTrace gcTrace) {
        ArgumentChecking.notNull(gcTrace, "gcTrace");
        
        for (GCTraceSetListener listener : listeners()) {
            listener.gcTraceRenamed(gcTrace);
        }
    }
    
    /**
     * It calls the <tt>gcTraceRemoved()</tt> method on all the listeners in
     * the listener set.
     *
     * @param gcTrace The GC trace that has just been removed from the GC
     * trace set.
     * 
     * @see gchisto.gctraceset.GCTraceSetListener#gcTraceRemoved(GCTrace)
     */
    public void callGCTraceRemoved(GCTrace gcTrace) {
        ArgumentChecking.notNull(gcTrace, "gcTrace");
        
        for (GCTraceSetListener listener : listeners()) {
            listener.gcTraceRemoved(gcTrace);
        }
    }
    
    /**
     * It calls the <tt>gcTraceMovedUp()</tt> method on all the listeners in
     * the listener set.
     *
     * @param gcTrace The GC trace that has just been moved up in the GC
     * trace set.
     * 
     * @see gchisto.gctraceset.GCTraceSetListener#gcTraceMovedUp(GCTrace)
     */
    public void callGCTraceMovedUp(GCTrace gcTrace) {
        ArgumentChecking.notNull(gcTrace, "gcTrace");
        
        for (GCTraceSetListener listener : listeners()) {
            listener.gcTraceMovedUp(gcTrace);
        }
    }
    
    /**
     * It calls the <tt>gcTraceMovedDown()</tt> method on all the listeners in
     * the listener set.
     *
     * @param gcTrace The GC trace that has just been moved down in the GC
     * trace set.
     * 
     * @see gchisto.gctraceset.GCTraceSetListener#gcTraceMovedDown(GCTrace)
     */
    public void callGCTraceMovedDown(GCTrace gcTrace) {
        ArgumentChecking.notNull(gcTrace, "gcTrace");
        
        for (GCTraceSetListener listener : listeners()) {
            listener.gcTraceMovedDown(gcTrace);
        }
    }
    
    /**
     * It creates a new GC trace set listener set.
     */
    public GCTraceSetListenerSet() {
    }
    
}
