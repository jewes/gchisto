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

/**
 * A convenience class that includes methods that perform frequently-used
 * conversions. All methods are static and this class cannot be instantiated.
 *
 * @author Tony Printezis
 */
public class Conversions {
    
    /**
     * It converts seconds to millis.
     *
     * @param sec The seconds to be converted in millis.
     * @return The millis amount that equals the <tt>sec</tt> argument.
     *
     * @see gchisto.utils.Conversions#msToSec(double)
     */
    static public double secToMS(double sec) {
        return sec * 1000.0;
    }
    
    /**
     * It converts millis to seconds.
     *
     * @param ms The millis to be converted in seconds.
     * @return The seconds amount that equals the <tt>ms</tt> argument.
     *
     * @see gchisto.utils.Conversions#secToMS(double)
     */
    static public double msToSec(double ms) {
        return ms / 1000.0;
    }
    
    /**
     * Private constructor to avoid the instantiation of this class.
     */
    private Conversions() {
    }
}
