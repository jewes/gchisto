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

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;

public class GCDataStore extends GCStats
{
	GCDataStore(EnumMap<GCMetric, Boolean> enabled_map, int cpu_count,
		boolean has_time_zero)
	{
		super(enabled_map, cpu_count, has_time_zero);

		Class<GCMetric> c = GCMetric.class;
		_data_map = new EnumMap<GCMetric, ArrayList<Double>>(c);
		_time_map = new EnumMap<GCMetric, ArrayList<Double>>(c);

		ArrayList<Double> tlist = null;
		for (GCMetric metric:  GCMetric.values())
		{
			_data_map.put(metric, new ArrayList<Double>());
			switch (metric.timestamp_type())
			{
			case 0:	 tlist = null; break;
			case 1:  tlist = new ArrayList<Double>(); break;
			}
			_time_map.put(metric, tlist);
		}
	}

	public void add(GCMetric metric, double val)
	{
		super.add(metric, val);
		_data_map.get(metric).add(new Double(val));
	}

	public void add(GCMetric metric, String s)
	{
		Double val = Double.parseDouble(s);
		add(metric, val);
	}

	public void add_timestamp(GCMetric metric, double beg, double end)
	{
		super.add_timestamp(metric, beg, end);
		ArrayList<Double> tlist = _time_map.get(metric);
		if (tlist != null)
		{
			tlist.add(timestamp_offset() + beg);
		}
	}

	public ArrayList<Double> data(GCMetric metric)
	{
		return _data_map.get(metric);
	}

	public ArrayList<Double> time(GCMetric metric)
	{
		return _time_map.get(metric);
	}

	public void save(String prefix, String suffix) throws IOException
	{
		for (GCMetric metric:  GCMetric.values())
		{
			save(metric, prefix, suffix);
		}
	}

	public void save(GCMetric metric, String prefix, String suffix)
	throws IOException
	{
		if (disabled(metric)) return;

		ArrayList<Double> d = data(metric);
		if (d.size() == 0) return;
		Iterator<Double> diter = d.iterator();

		ArrayList<Double> t = time(metric);
		Iterator<Double> titer = t.iterator();
		// t != null ? t.iterator() : new NumberIterator(0.0, 1.0);

		String name = filename(metric, prefix, suffix);
		FileWriter fw = new FileWriter(name);
		BufferedWriter w = new BufferedWriter(fw);

		while (diter.hasNext())
		{
			w.write(titer.next().toString());
			w.write(' ');
			w.write(diter.next().toString());
			w.write(eol);
		}
		w.close();
	}

	protected String filename(GCMetric metric, String prefix, String suffix)
	{
		StringBuilder filename = new StringBuilder();
		if (prefix != null) filename.append(prefix);
		filename.append(metric);
		if (suffix != null) filename.append(suffix);
		return filename.toString();
	}

	private EnumMap<GCMetric, ArrayList<Double>> _data_map;
	private EnumMap<GCMetric, ArrayList<Double>> _time_map;
}
