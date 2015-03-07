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
 * A class representing a number sequence. Numbers can be added to this 
 * sequence and standard statistical values (like average, standard deviation,
 * etc.) can be calculated against this sequence.
 *
 * @author Tony Printezis
 */
public class NumberSeq {
    
    /**
     * How many numbers added to this sequence.
     *
     * @see #getNum()
     * @see #getNumD()
     */
    private int num;
    
    /**
     * The sum of numbers added to this sequence.
     *
     * @see #getSum()
     */
    private double sum;
    
    /**
     * The sum of squares of the numbers added to this sequence.
     *
     * @see #getSumOfSquares()
     */
    private double sumOfSquares;
    
    /**
     * The minimum number added to this sequence.
     *
     * @see #getMin()
     */
    private double min;
    
    /**
     * The maximum number added to this sequence.
     *
     * @see #getMax()
     */
    private double max;
    
    /**
     * It returns how many numbers added to this sequence.
     *
     * @return How many numbers added to this sequence.
     *
     * @see #getNumD()
     */
    public int getNum() {
        return num;
    }
    
    /**
     * It returns how many numbers added to this sequence.
     *
     * @return How many numbers added to this sequence.
     *
     * #see #getNum()
     */
    public double getNumD() {
        return (double) num;
    }
    
    /**
     * It returns the sum of numbers added to this sequence.
     *
     * @return The sum of numbers added to this sequence.
     */
    public double getSum() {
        return sum;
    }
    
    /**
     * It returns the sum of squares of the numbers added to this sequence.
     *
     * @return The sum of squares of the numbers added to this sequence.
     */
    private double getSumOfSquares() {
        return sumOfSquares;
    }
    
    /**
     * It returns the minimum number added to this sequence.
     *
     * @return The minimum number added to this sequence, or <tt>0</tt> if
     * the sequence is empty.
     */
    public double getMin() {
        return min;
    }
    
    /**
     * It returns the maximum number added to this sequence.
     *
     * @return The maximum number added to this sequence, or <tt>0</tt> if 
     * the sequnce is empty.
     */
    public double getMax() {
        return max;
    }
    
    /**
     * It calculates the average of the sequence.
     *
     * @return The average of the sequence, or <tt>0</tt> if the sequence
     * is empty.
     */
    public double getAvg() {
        return (getNum() > 0) ? getSum() / getNumD() : 0.0;
    }
    
    /**
     * It calculates the variance of the sequence.
     *
     * @return The variance of the sequence, or <tt>0</tt> if the sequence
     * is empty.
     */
    private double getVariance() {
        if (getNum() <= 1)
            return 0.0;
        
        double variance = 
                (getSumOfSquares() - getSum() * getSum() / getNumD()) / (getNum() - 1.0);
        if (variance < 0.0) {
            assert Comparisons.eq(variance, 0.0);
            variance = 0.0;
        }
        return variance;
    }
    
    /**
     * It calculates the standard deviation of the sequence.
     *
     * @return The standard deviation of the sequence, or <tt>0</tt> if the
     * sequence is empty.
     */
    public double getSigma() {
        double variance = getVariance();
        assert variance >= 0.0 : "variance = " + variance;
        return Math.sqrt(variance);
    }
    
    /**
     * It adds a new number to the sequence.
     *
     * @param number The new number to be added to the sequence.
     */
    public void add(double number) {
        if (num == 0) {
            min = number;
            max = number;
        } else {
            assert num > 0;
            if (number < min)
                min = number;
            if (number > max)
                max = number;
        }
        sum += number;
        sumOfSquares += number * number;
        ++num;
    }
    
    /**
     * It empties the sequence.
     */
    public void empty() {
        num = 0;
        sum = 0.0;
        sumOfSquares = 0.0;
        min = 0.0;
        max = 0.0;
    }
    
    /**
     * It creates a new number sequence instance.
     */
    public NumberSeq() {
        empty();
    }
    
}
