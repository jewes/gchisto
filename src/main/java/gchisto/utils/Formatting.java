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

import java.text.NumberFormat;

/**
 * A convenience class that includes methods that format numbers into 
 * frequently-used formats. Two ways that do the formatting are provided.
 * One is to simply call a method that does the formatting. The other
 * is to retrieve a instance of a class that implements that <tt>Formatter</tt>
 * interface which can also do the formatting. All methods are static and this
 * class cannot be instantiated. 
 *
 * @author Tony Printezis
 * @see    gchisto.utils.Formatter
 * @see    java.text.NumberFormat
 */
public class Formatting {

    /**
     * The numbe of fraction digits when formatting doubles.
     */
    static final private int DOUBLE_FRACTION_DIGITS = 3;
    /**
     * The number of fraction digits when formatting percentages.
     */
    static final private int PERC_FRACTION_DIGITS = 2;
    static final private String NA_STRING = "n/a";
    /**
     * A <tt>NumberFormat</tt> instance that formats doubles.
     */
    static final private NumberFormat doubleFormat;
    /**
     * A <tt>NumberFormat</tt> instance that formats integers.
     */
    static final private NumberFormat intFormat;
    /**
     * A <tt>NumberFormat</tt> instance that formats percentages.
     */
    static final private NumberFormat percFormat;

    static private boolean isNA(Number number) {
        double val = number.doubleValue();
        return Double.isNaN(val) || Double.isInfinite(val);
    }
    /**
     * A <tt>Formatter</tt> instance that formats doubles.
     */
    static final private Formatter doubleFormatter = new Formatter() {

        public String format(Number number) {
            if (isNA(number)) {
                return NA_STRING;
            } else {
                return doubleFormat.format(number);
            }
        }
    };
    /**
     * A <tt>Formatter</tt> instance that formats integers.
     */
    static final private Formatter intFormatter = new Formatter() {

        public String format(Number number) {
            if (isNA(number)) {
                return NA_STRING;
            } else {
                return intFormat.format(number);
            }
        }
    };
    /**
     * A <tt>Formatter</tt> instance that formats percentages.
     */
    static final private Formatter percFormatter = new Formatter() {

        public String format(Number number) {
            if (isNA(number)) {
                return NA_STRING;
            } else {
                double perc = number.doubleValue() / 100.0;
                return percFormat.format(perc);
            }
        }
    };
    /**
     * A <tt>Formatter</tt> instance that formats percentage differences.
     */
    static final private Formatter percDiffFormatter = new Formatter() {

        public String format(Number number) {
            if (isNA(number)) {
                return NA_STRING;
            } else {
                double percDiff = number.doubleValue();
                double perc = Math.abs(percDiff);
                String str = formatPerc(perc);
                if (Comparisons.lt(percDiff, 0.0)) {
                    str = "-" + str;
                } else if (Comparisons.gt(percDiff, 0.0)) {
                    str = "+" + str;
                }
                return str;
            }
        }
    };

    /**
     * Static initializer that sets up the <tt>NumberFormat</tt> instances
     * that are used internally.
     */
    static {
        doubleFormat = NumberFormat.getNumberInstance();
        doubleFormat.setMinimumFractionDigits(DOUBLE_FRACTION_DIGITS);
        doubleFormat.setMaximumFractionDigits(DOUBLE_FRACTION_DIGITS);

        intFormat = NumberFormat.getNumberInstance();
        intFormat.setMinimumFractionDigits(0);
        intFormat.setMaximumFractionDigits(0);

        percFormat = NumberFormat.getPercentInstance();
        percFormat.setMinimumFractionDigits(PERC_FRACTION_DIGITS);
        percFormat.setMaximumFractionDigits(PERC_FRACTION_DIGITS);
    }

    /**
     * It returns a <tt>Formatter</tt> that formats a double so that it has
     * exactly 3 fraction digits and commas separate groups of three
     * integral digits
     *
     * @return A double formatter.
     *
     * @see gchisto.utils.Formatting#formatDouble(double)
     * @see #DOUBLE_FRACTION_DIGITS
     */
    static public Formatter doubleFormatter() {
        return doubleFormatter;
    }

    /**
     * It formats a double so that it has exactly 3 fraction digits and commas
     * separate groups of three integral digits.
     *
     * @param number The number to be formatted.
     * @return The given number formatted.
     *
     * @see gchisto.utils.Formatting#doubleFormatter()
     * @see #DOUBLE_FRACTION_DIGITS
     */
    static public String formatDouble(double number) {
        return doubleFormatter().format(number);
    }
    
    /**
     * It returns a <tt>Formatter</tt> that formats an integer so that commas
     * separate groups of three integral digits.
     *
     * @return An integer formatter.
     *
     * @see gchisto.utils.Formatting#formatInt(int)
     */
    static public Formatter intFormatter() {
        return intFormatter;
    }

    /**
     * It formats an so that commas separate groups of three integral digits.
     *
     * @see gchisto.utils.Formatting#intFormatter()
     */
    static public String formatInt(int number) {
        return intFormatter().format(number);
    }

    static public String formatIntOrDouble(double number) {
        if (Comparisons.eq((double) ((int) number), number)) {
            return formatInt((int) number);
        } else {
            return formatDouble(number);
        }
    }

    /**
     * It returns a <tt>Formatter</tt> that formats a percentage so that
     * it has exactly 2 fraction digits and commas separate groups of three
     * integral digits.
     *
     * @return A percentage formatter.
     *
     * @see gchisto.utils.Formatting#formatPerc(double)
     * @see #PERC_FRACTION_DIGITS
     */
    static public Formatter percFormatter() {
        return percFormatter;
    }

    /**
     * It formats a percentage so that it has exactly 2 fraction digits and
     * commas separate groups of three integral digits.
     *
     * @param percent The percentage to be formatted.
     * @return The given percentage formatted.
     *
     * @see gchisto.utils.Formatting#percFormatter()
     * @see #PERC_FRACTION_DIGITS
     */
    static public String formatPerc(double percent) {
        return percFormatter().format(percent);
    }

    /**
     * It returns a <tt>Formatter</tt> that formats a percentage difference
     * so that it has exactly 2 fraction digits and commas separate groups of
     * three integral digits. If the given percentage difference is not 0, then
     * the sign (+ or -) will always preceed the number.
     *
     * @return A percentage difference formatter.
     * 
     * @see gchisto.utils.Formatting#formatPercDiff(double)
     * @see #PERC_FRACTION_DIGITS
     */
    static public Formatter percDiffFormatter() {
        return percDiffFormatter;
    }

    /**
     * It formats a percentage difference so that it has exactly 2 fraction
     * digits and commas separate groups of three integral digits. If the 
     * given percentage difference is not 0, then the sign (+ or -) will
     * always preceed the number.
     *
     * @param percDiff The percentage difference to be formatted.
     * @return The given percentage difference formatted.
     * 
     * @see gchisto.utils.Formatting#percDiffFormatter()
     * @see #PERC_FRACTION_DIGITS
     */
    static public String formatPercDiff(double percDiff) {
        return percDiffFormatter().format(percDiff);
    }

    /**
     * Private constructor to avoid the instantiation of this class.
     */
    private Formatting() {
    }
}
