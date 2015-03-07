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
import java.util.ArrayList;
import java.util.List;

/**
 * A convenience class that includes array-related utility methods.
 * All methods are static and this class cannot be instantiated.
 *
 * @author Tony Printezis
 */
public class ArrayUtils {

    /**
     * It returns an ArrayList that contains the elements of the given
     * array, in the same order.
     *
     * @param array The array whose contents will be used to populate
     * the new array list.
     * @return An ArrayList that contains the elements of the given
     * array, in the same order.
     */
    static public ArrayList<String> generateArrayList(String[] array) {
        ArgumentChecking.notNull(array, "array");
        
        ArrayList<String> list = new ArrayList<String>(array.length);
        for (int i = 0; i < array.length; ++i) {
            list.add(i, array[i]);
        }
        return list;
    }
    
    /**
     * Private constructor to avoid the instantiation of this class.
     */
    private ArrayUtils() {
    }
}
