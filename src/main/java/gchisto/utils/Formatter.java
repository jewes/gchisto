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
 * An interface that allows a particular number formatter to format a number
 * into a string, according to the formatter's requirements. For several
 * reasons it was not straightforward to use <tt>java.text.NumberFormat</tt>.
 * This interface is similar, but more straightforward. The <tt>Formatting</tt>
 * class provides <tt>Formatter</tt> factories.
 *
 * @author Tony Printezis
 * @see    java.text.NumberFormat
 */
public interface Formatter {
    
    /**
     * It formats the given number into a string, according to the formatter's
     * requirements.
     *
     * @param number The number to be formatted into a string.
     * @return The given number, formatted according to the formatter's 
     * requirements.
     *
     * @see gchisto.utils.Formatting
     */
    public String format(Number number);
    
}
