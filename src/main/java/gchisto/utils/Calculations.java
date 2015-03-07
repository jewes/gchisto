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

/**
 * A convenience class that includes methods that perform frequently-used
 * calculations. All methods are static and this class cannot be
 * instantiated.
 *
 * @author Tony Printezis
 */
public class Calculations {

    /**
     * It returns the percentage 100 * <tt>value</tt> / <tt>max</tt>.
     *
     * @param value The value component of the percentage calculation.
     * @param max The max value component of the percentage calculation.
     * @return The percentage 100 * <tt>value</tt> / <tt>max</tt>.
     *
     * @see gchisto.utils.Calculations#perc(double, double)
     * @see gchisto.utils.Calculations#perc(Number, Number)
     */
    static public double perc(int value, int max) {
        return perc((double) value, (double) max);
    }
    
    /**
     * It returns the percentage 100 * <tt>value</tt> / <tt>max</tt>.
     *
     * @param value The value component of the percentage calculation.
     * @param max The max value component of the percentage calculation.
     * @return The percentage 100 * <tt>value</tt> / <tt>max</tt>.
     *
     * @see gchisto.utils.Calculations#perc(double, double)
     * @see gchisto.utils.Calculations#perc(int, int)
     */
    static public double perc(Number value, Number max) {
        ArgumentChecking.notNull(value, "value");
        ArgumentChecking.notNull(max, "max");
        
        return perc(value.doubleValue(), max.doubleValue());
    }
    
    /**
     * It returns the percentage 100 * <tt>value</tt> / <tt>max</tt>.
     *
     * @param value The value component of the percentage calculation.
     * @param max The max value component of the percentage calculation.
     * @return The percentage 100 * <tt>value</tt> / <tt>max</tt>.
     *
     * @see gchisto.utils.Calculations#perc(int, int)
     * @see gchisto.utils.Calculations#perc(Number, Number)
     */
    static public double perc(double value, double max) {
        return (value / max) * 100.0;
    }
    
    /**
     * It returns the percentage difference between two values in the 
     * form 100 * (<tt>value</tt> - <tt>base</tt>) / <tt>base</bb>.
     *
     * @param value The value component in the percentage calculation.
     * @param base The base component in the percentage calculation.
     * @return The percentage difference between two values in the 
     * form 100 * (<tt>value</tt> - <tt>base</tt>) / <tt>base</bb>.
     *
     * @see gchisto.utils.Calculations#percDiff(double, double)
     */
    static public double percDiff(Number value, Number base) {
        ArgumentChecking.notNull(value, "value");
        ArgumentChecking.notNull(base, "base");
        
        return percDiff(value.doubleValue(), base.doubleValue());
    }
    
    /**
     * It returns the percentage difference between two values in the 
     * form 100 * (<tt>value</tt> - <tt>base</tt>) / <tt>base</bb>.
     *
     * @param value The value component in the percentage calculation.
     * @param base The base component in the percentage calculation.
     * @return The percentage difference between two values in the 
     * form 100 * (<tt>value</tt> - <tt>base</tt>) / <tt>base</bb>.
     *
     * @see gchisto.utils.Calculations#percDiff(Number, Number)
     */
    static public double percDiff(double value, double base) {
        return perc(value - base, base);
    }
    
    /**
     * Private constructor to avoid the instantiation of this class.
     */
    private Calculations() {
    }
}
