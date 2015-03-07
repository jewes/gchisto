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

/**
 * A listener for changes in a GC trace set.
 *
 * @author Tony Printezis
 * @see    gchisto.gctraceset.GCTraceSet
 */
public interface GCTraceSetListener {
    
    /**
     * Called after a GC trace has been added to the GC trace set.
     *
     * @param gcTrace The GC trace added to the GC trace set.
     */
    public void gcTraceAdded(GCTrace gcTrace);
    
    /**
     * Called after a GC trace has been renamed in the GC trace set.
     *
     * @param gcTrace The GC trace renamed in the GC trace set.
     */
    public void gcTraceRenamed(GCTrace gcTrace);
    
    /**
     * Called after a GC trace has been removed from the GC trace set.
     *
     * @param gcTrace The GC trace removed from the GC trace set.
     */
    public void gcTraceRemoved(GCTrace gcTrace);
    
    /**
     * Called after a GC trace has been moved up in the GC trace set.
     *
     * @param gcTrace The GC trace moved up in the GC trace set.
     */
    public void gcTraceMovedUp(GCTrace gcTrace);
    
    /**
     * Called after a GC trace has been moved down in the GC trace set.
     *
     * @param gcTrace The GC trace moved down in the GC trace set.
     */
    public void gcTraceMovedDown(GCTrace gcTrace);
    
}
