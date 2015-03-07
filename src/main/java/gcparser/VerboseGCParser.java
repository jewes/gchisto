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

import java.util.regex.*;

public class VerboseGCParser extends GCParser
{
	public VerboseGCParser(GCStats gcstats, boolean verbose)
	{
		super(gcstats, verbose);
		// _debug = System.err;	// XXX
	}

	protected void extract(Matcher m, GCMetric gc_time_metric, int base_grp)
	{
		double th_used_beg = get_sz(m, base_grp + th_used_beg_ofs);
		double th_used_end = get_sz(m, base_grp + th_used_end_ofs);
		double th_commit_end = get_sz(m, base_grp + th_commit_end_ofs);

		double timestamp_beg = get_ts(m, timestamp_grp);
		int pause_grp = base_grp + gc_time_ofs;
		double pause_time = Double.parseDouble(m.group(pause_grp));
		double timestamp_end = timestamp_beg + pause_time;

		add_pt(gc_time_metric, pause_time);

		add_sz(GCMetric.th_used_beg,   th_used_beg);
		add_sz(GCMetric.th_used_end,   th_used_end);
		add_sz(GCMetric.th_commit_end, th_commit_end);

		add_ts(GCMetric.th_used_beg, timestamp_beg, timestamp_end);
		add_ts(gc_time_metric,       timestamp_beg, timestamp_end);
		add_ts(GCMetric.tgc_time,    timestamp_beg, timestamp_end);

		double prev_gc_used_end = gcstats().heap_used_end();
		if (th_used_beg >= prev_gc_used_end)
		{
			// The heap didn't shrink between the last time heap
			// sizes were recorded and the start of the current GC.
			final double th_alloc = th_used_beg - prev_gc_used_end;
			add_sz(GCMetric.th_alloc, th_alloc);

			if (!gcstats().has_generated_timestamps())
			{
				final double prev_end =
					gcstats().heap_timestamp_end();
				add_rate(GCMetric.th_alloc_ela, th_alloc,
					prev_end, timestamp_end);
			}
		}

		gcstats().save_heap_sizes(th_used_beg, th_used_end,
			th_commit_end, timestamp_beg, timestamp_end);
	}

	public boolean parse(String filename, int line, String s)
	{
		Matcher m = _ygc_pattern.matcher(s);
		if (m.find())
		{
			save_match_state(filename, line, s);
			// debug(s, m, ygc_base_grp);	// XXX
			extract(m, GCMetric.ygc_time, ygc_base_grp);
			return true;
		}

		m = _fgc_pattern.matcher(s);
		if (m.find())
		{
			save_match_state(filename, line, s);
			// debug(s, m, fgc_base_grp);	// XXX
			extract(m, GCMetric.fgc_time, fgc_base_grp);
			return true;
		}

		return false;
	}

	// XXX
	private void debug(String s, Matcher m, int base_grp)
	{
		if (_debug == null) return;
		debug(s); debug();
		debug(m, timestamp_grp,                "ts ");
		debug(m, base_grp + th_used_beg_ofs,   " tub");
		debug(m, base_grp + th_used_end_ofs,   " tue");
		debug(m, base_grp + th_commit_end_ofs, " tco");
		debug(m, base_grp + gc_time_ofs,       " gct");
		debug();
	}

	private static Pattern _ygc_pattern = Pattern.compile(
		timestamp_re + pargc_young_gc_re + heap_size_status_re + ", " + 
		gc_time_secs_re + "\\]");

	private static Pattern _fgc_pattern = Pattern.compile(
		timestamp_re + "\\[Full GC " + heap_size_status_re + ", " + 
		gc_time_secs_re + "\\]");

	private static final int timestamp_grp = timestamp_re_time_group;

	private static final int ygc_base_grp = 
		timestamp_re_groups + pargc_young_gc_re_groups;
	private static final int fgc_base_grp =
		timestamp_re_groups;

	// These are offsets from one of the above base group numbers.
	private static final int th_used_beg_ofs =
		heap_size_status_re_size_beg_group;
	private static final int th_used_end_ofs =
		heap_size_status_re_size_end_group;
	private static final int th_commit_end_ofs =
		heap_size_status_re_commit_size_group;

	private static final int gc_time_ofs =
		heap_size_status_re_groups + gc_time_secs_re_time_group;
}
