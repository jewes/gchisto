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
 * comparisons. In the double comparisons, values whose difference is 
 * smaller than a given small value are considered equal. All methods are
 * static and this class cannot be instantiated.
 *
 * @author Tony Printezis
 */
public class Comparisons {

    /**
     * If the difference between two values is smaller than this value,
     * then they are considered equal.
     */
    static private final double VERY_SMALL_VALUE = 0.0000001;
    
    /**
     * It determines whether <tt>v1</tt> == <tt>v2</tt>.
     *
     * @param v1 The v1 value in the comparison calculation.
     * @param v2 The v2 value in the comparison calculation.
     * @return Whether <tt>v1</tt> == <tt>v2</tt>.
     */
    static public boolean eq(double v1, double v2) {
        return Math.abs(v1 - v2) < VERY_SMALL_VALUE;
    }
    
    /**
     * It determines whether <tt>v1</tt> < <tt>v2</tt>.
     *
     * @param v1 The v1 value in the comparison calculation.
     * @param v2 The v2 value in the comparison calculation.
     * @return Whether <tt>v1</tt> < <tt>v2</tt>.
     */
    static public boolean lt(double v1, double v2) {
        return (v2 - v1) > VERY_SMALL_VALUE;
    }
    
    /**
     * It determines whether <tt>v1</tt> <= <tt>v2</tt>.
     *
     * @param v1 The v1 value in the comparison calculation.
     * @param v2 The v2 value in the comparison calculation.
     * @return Whether <tt>v1</tt> <= <tt>v2</tt>.
     */
    static public boolean lteq(double v1, double v2) {
        return (v2 - v1) > -VERY_SMALL_VALUE;
    }
    
    /**
     * It determines whether <tt>v1</tt> > <tt>v2</tt>.
     *
     * @param v1 The v1 value in the comparison calculation.
     * @param v2 The v2 value in the comparison calculation.
     * @return Whether <tt>v1</tt> > <tt>v2</tt>.
     */
    static public boolean gt(double v1, double v2) {
        return (v1 - v2) > VERY_SMALL_VALUE;
    }
    
    /**
     * It determines whether <tt>v1</tt> >= <tt>v2</tt>.
     *
     * @param v1 The v1 value in the comparison calculation.
     * @param v2 The v2 value in the comparison calculation.
     * @return Whether <tt>v1</tt> >= <tt>v2</tt>.
     */
    static public boolean gteq(double v1, double v2) {
        return (v1 - v2) > -VERY_SMALL_VALUE;
    }
    
    /**
     * It determines whether <tt>v1</tt> < <tt>v</tt> < <tt>v2</tt>.
     *
     * @param v1 The v1 value in the comparison calculation.
     * @param v2 The v2 value in the comparison calculation.
     * @return Whether <tt>v1</tt> < <tt>v</tt> < <tt>v2</tt>.
     */
    static public boolean between(double v1, double v, double v2) {
        return lt(v1, v) && lt(v, v2);
    }
    
    /**
     * It determines whether <tt>v1</tt> <= <tt>v</tt> <= <tt>v2</tt>.
     *
     * @param v1 The v1 value in the comparison calculation.
     * @param v2 The v2 value in the comparison calculation.
     * @return Whether <tt>v1</tt> <= <tt>v</tt> <= <tt>v2</tt>.
     */
    static public boolean betweeneq(double v1, double v, double v2) {
        return lteq(v1, v) && lteq(v, v2);
    }
    
    /**
     * Private constructor to avoid the instantiation of this class.
     */
    private Comparisons() {
    }
}
