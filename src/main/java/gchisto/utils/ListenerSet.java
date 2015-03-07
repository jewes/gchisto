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
package gchisto.utils;

import gchisto.utils.errorchecking.ArgumentChecking;
import java.util.LinkedList;
import java.util.List;

/**
 * A convenience class that keeps track of listener objects. It is used by
 * all the listener sets used in this application.
 *
 * @author Tony Printezis
 */
abstract public class ListenerSet<L> {
    
    /**
     * The list that contains the added listener objects.
     */
    final private List<L> listeners = new LinkedList<L>();
    
    /**
     * It returns the linked list that holds the listeners.
     *
     * @return The linked list that holds the listeners.
     */
    protected List<L> listeners() {
        return listeners;
    }
    
    /**
     * It adds a listener to this set.
     *
     * @param listener The listener to be added to this set.
     */
    public void add(L listener) {
        ArgumentChecking.notNull(listener, "listener");
        
        assert !listeners.contains(listener) :
            "listener " + listener + " should not already exist in the listener set.";
        listeners.add(listener);
    }
    
    /**
     * It removes a listener from this set.
     *
     * @param listener The listener to the removed from this set.
     */
    public void remove(L listener) {
        ArgumentChecking.notNull(listener, "listener");

        assert listeners.contains(listener) :
            "listener " + listener + " should already exist in the listener set.";
        listeners.remove(listener);
    }
    
    /**
     * It creates a listener set instance.
     */
    protected ListenerSet() {
    }
    
}
