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
package gcparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class Stats
{
	public Stats()
	{
		initialize();
	}

	public Stats(double data[], int n)
	{
		initialize();
		add(data, n);
	}

	public Stats(String data[], int n)
	{
		initialize();
		add(data, n);
	}

	public Stats(BufferedReader r) throws IOException
	{
		initialize();
		add(r);
	}

	long n() { return _n; }
	long count() { return _n; }
	double sum() { return _sum; }

	// Valid only if count() > 0 (i.e., at least 1 data point required).
	// The geometric_mean() is valid only if all data points are >= 0.
	double min() { return _min; }
	double max() { return _max; }
	double mean() { return _sum / _n; }
	double arithmetic_mean() { return mean(); }
	double geometric_mean()
	{
		// Product of all the points to the power 1/n:
		//
		// 	(x1 * x2 * x3 ...) ** (1/n)
		// 
		// -or-
		// 
		// e to the power x, where x is the mean of the natural logs of
		// the data points:
		// 
		// exp((ln(x1) + ln(x2) + ln(x3) ...) / n).
		//
		// This uses the former.
		return Math.pow(_product, 1.0 / n());
	}

	double sum_of_squares() { return _sum_of_squares; }

	double sum_of_squared_deviations()
	{
		// sum[i=1:n]((x[i] - mean)^2)
		// sum[i=1:n](x[i]^2 - 2 mean x[i] + mean^2)
		// sum[i=1:n](x[i]^2) - 2 mean sum[i=1:n](x[i]) +
		// 	sum[i=1:n](mean^2)
		// sum_of_squares() - 2 mean sum() + n mean^2
		// sum_of_squares() - 2 (sum() / n) sum() + n mean^2
		// sum_of_squares() - 2 sum()^2 / n + n mean^2
		// sum_of_squares() - 2 sum()^2 / n + (n mean)^2 / n
		// sum_of_squares() - 2 sum()^2 / n + sum()^2 / n
		// sum_of_squares() - sum()^2 / n
		return sum_of_squares() - sum() * sum() / n();
	}

	double biased_variance()
	{
		// Assumes n >= 1.
		return sum_of_squared_deviations() / n();
	}

	double unbiased_variance()
	{
		// Assumes n >= 2.
		return sum_of_squared_deviations() / (n() - 1.0);
	}

	double variance()
	{
		// Assumes n >= 2.
		return unbiased_variance();
	}

	double stddev()
	{
		// Assumes n >= 2.
		return Math.sqrt(variance());
	}

	// Add a data point to the sample.
	public void add(double value)
	{
		if (value < _min) _min = value;
		if (value > _max) _max = value;
		_sum += value;
		_sum_of_squares += value * value;
		_product *= value;
		++_n;
	}

	public void add(String value)
	{
		add(Double.parseDouble(value));
	}

	// Add multiple data points from an array.
	public void add(double data[], int n)
	{
		for (int i = 0; i < n; ++i)
		{
			add(data[i]);
		}
	}

	// Add multiple data points from an array.
	public void add(String data[], int n)
	{
		for (int i = 0; i < n; ++i)
		{
			add(data[i]);
		}
	}

	// Add multiple data points from a collection.
	public void add(Collection<Double> data, int n)
	{
		Iterator<Double> iter = data.iterator();
		for (int i = 0; iter.hasNext() && i < n; ++i)
		{
			add(iter.next());
		}
	}

	// Add multiple data points from a stream.
	public void add(BufferedReader r) throws IOException
	{
		String s = r.readLine();
		while (s != null)
		{
			add(s);
		}
	}

	// Remove a data point from the sample.
	public void remove(double value, Collection<Double> data)
	{
		if (value == _min || value == _max)
		{
			initialize();
			add(data, data.size());
			return;
		}
		remove(value);
	}

	// Remove a single data point, without checking for min/max.
	protected void remove(double value)
	{
		_sum -= value;
		_sum_of_squares -= value * value;
		_product /= value;
		--_n;
	}

	protected void initialize()
	{
		_min = Double.MAX_VALUE;
		_max = Double.MIN_VALUE;
		_sum = 0.0;
		_sum_of_squares = 0.0;
		_product = 1.0;
		_n = 0;
	}

	// Member data.
	private double	_min;
	private double	_max;
	private double	_sum;
	private double	_sum_of_squares;
	private double	_product;	// Product of all n terms, for geomean.
	private long	_n;
}
