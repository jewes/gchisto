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
package gchisto.utils.errorchecking;

import gchisto.utils.Comparisons;

/**
 * A convenience class that includes methods that perform frequently-used
 * checks on method arguments. All methods are static and this class cannot be
 * instantiated.
 *
 * @author Tony Printezis
 */
public class ArgumentChecking {
    
    /**
     * It checks whether an argument is <tt>null</tt>. If it is, it throws
     * an <tt>IllegalArgumentException</tt>.
     *
     * @param arg The argument to be checked.
     * @param argName The name of the argument to be checked.
     * @exception IllegalArgumentException It is thrown if the given
     * argument is null;
     */
    static public void notNull(Object arg, String argName) {
        if (!(arg != null)) {
            throw new IllegalArgumentException(
                    String.format("%s should not be null", argName));
        }
    }
    
    /**
     * It checks whether an argument is within bounds. If it is not, it throws
     * an <tt>IllegalArgumentException</tt>.
     *
     * @param arg The argument to be checked.
     * @param min The minimum allowed value for the given argument, inclusively
     * @param max The maximum allowed value for the given argument, inclusively
     * @param argName The name of the argument to be checked.
     * @exception IllegalArgumentException It is thrown if the given
     * argument is not within bounds;
     *
     * @see #withinBounds(double, double, double, String)
     */
    static public void withinBounds(int arg, int min, int max,
            String argName) {
        if (!( min <= arg && arg <= max)) {
            throw new IllegalArgumentException(
                    String.format("%s (%d) out of bounds", argName, arg));
        }
    }
    
    /**
     * It checks whether an argument is within bounds. If it is not, it throws
     * an <tt>IllegalArgumentException</tt>.
     *
     * @param arg The argument to be checked.
     * @param min The minimum allowed value for the given argument, inclusively
     * @param max The maximum allowed value for the given argument, inclusively
     * @param argName The name of the argument to be checked.
     * @exception IllegalArgumentException It is thrown if the given
     * argument is not within bounds;
     *
     * @see #withinBounds(int, int, int, String)
     */
    static public void withinBounds(double arg, double min, double max,
            String argName) {
        if (!Comparisons.betweeneq(min, arg, max)) {
            throw new IllegalArgumentException(
                    String.format("%s (%1.4f) out of bounds", argName, arg));
        }
    }
    
    /**
     * It checks whether an argument is not less than a given minimum. If it is,
     * it throws an <tt>IllegalArgumentException</tt>.
     *
     * @param arg The argument to be checked.
     * @param min The minimum allowed value for the given argument, inclusively
     * @param argName The name of the argument to be checked.
     * @exception IllegalArgumentException It is thrown if the given
     * argument is not less than the given minimum
     */
    static public void lowerBound(double arg, double min, String argName) {
        if (!Comparisons.gteq(arg, min)) {
            throw new IllegalArgumentException(
                    String.format("%s (%1.4f) smaller than the lower bound", argName, arg));
        }
    }
    
    /**
     * It checks whether an argument is not less than a given minimum. If it is,
     * it throws an <tt>IllegalArgumentException</tt>.
     *
     * @param arg The argument to be checked.
     * @param min The minimum allowed value for the given argument, inclusively
     * @param argName The name of the argument to be checked.
     * @exception IllegalArgumentException It is thrown if the given
     * argument is not less than the given minimum
     */
    static public void lowerBound(int arg, int min, String argName) {
        if (!(arg >= min)) {
            throw new IllegalArgumentException(
                    String.format("%s (%d) smaller than the lower bound", argName, arg));
        }
    }
    
    static public void checkCondition(boolean cond, String message) {
        if (!cond) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Private constructor to avoid the instantiation of this class.
     */
    private ArgumentChecking() {
    }
}
