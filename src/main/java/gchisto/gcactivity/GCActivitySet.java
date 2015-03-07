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

import gchisto.utils.NumberSeq;
import gchisto.utils.errorchecking.NotImplementedException;
import java.util.ArrayList;

/**
 * A set of GC activities. All GC activities in the set share the same name,
 * i.e.. they are of the same "kind". The set should be ordered in increasing
 * order based on the startSec() value of the included GC activities.
 * GC activities in the set should not overlap.
 * <p>
 * Because it extends <tt>java.util.ArrayList</tt>, an iteration over the GC
 * activities in it can be easily done using the standard for-loop over
 * collections.
 *
 * @author Tony Printezis
 * @see    gchisto.gcactivityset.GCActivity
 * @see    gchisto.gcactivityset.GCActivitySetListener
 * @see    java.util.ArrayList
 */
public class GCActivitySet extends ArrayList<GCActivity> {
    
    /**
     * The name of the GC activities in the set. This name is the "kind"
     * of the GC activities in the set.
     *
     * @see #getGCActivityName()
     */
    final private String gcActivityName;
    
    final private NumberSeq numberSeq = new NumberSeq();
    
    /**
     * It adds a new GC activity to the set. After adding it, it will call the
     * <tt>added()</tt> method on the listeners of this set.
     *
     * @param gcActivity The GC activity to be added to the set.
     */
    public void addGCActivity(GCActivity gcActivity) {
        assert gcActivity != null;
        
        add(gcActivity);
        numberSeq.add(gcActivity.getDurationSec());
    }
    
    /**
     * It returns the name of the GC activities in the set. This name is 
     * the "kind" of the GC activities in the set.
     *
     * @return The name of the GC activities in the set.
     */
    public String getGCActivityName() {
        return gcActivityName;
    }
    
    public NumberSeq getNumberSeq() {
        return numberSeq;
    }
    
    /**
     * It verifies the correctness of the contents in the set.
     */
    public void verify() {
        throw new NotImplementedException();
    }
    
    /**
     * It creates a new GC activity set instance.
     * 
     * @param gcActivityName The name of the GC activites in the set. This
     * name is the "kind" of the GC activities in the set.
     */
    public GCActivitySet(String gcActivityName) {
        this.gcActivityName = gcActivityName;
    }
    
}
