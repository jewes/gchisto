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

public class ParCompactPhaseGCParser extends GCParser
{
	public ParCompactPhaseGCParser(GCStats gcstats, boolean verbose)
	{
		super(gcstats, verbose);
// 		_debug = System.err;	// XXX
		if (_debug == null) return;
	}

	public boolean parse(String filename, int line, String s)
	{
		for (int i = 0; i < _patterns.length; ++i)
		{
			Matcher m = _patterns[i].matcher(s);
			if (m.find())
			{
				save_match_state(filename, line, s);
				debug(s, m, _metrics[i]);	// XXX

				String str = m.group(m.groupCount() - 1);
				double len = Double.parseDouble(str);
				add_dp(_metrics[i], len);

				double ts = get_ts(m, timestamp_re_time_group);
				add_ts(_metrics[i], ts, ts + len);

				return true;
			}
		}

		return false;
	}

	// XXX
	private void debug(String s, Matcher m, GCMetric metric)
	{
		if (_debug == null) return;
		debug(s); debug();
		debug(m, m.groupCount() - 1, metric.toString());
		debug();
	}

	private static Pattern phase_pattern(String phase_name_re)
	{
		return Pattern.compile(timestamp_re + "\\[" + phase_name_re +
				" *[,:] *" + gc_time_secs_re + "\\]");
	}

	// Regular expressions to match the phase names printed by
	// -XX:+PrinParallelOldGCPhaseTimes.
	private static String _phase_name_res[] =
	{
		// [pre compact, 0.0000078 secs]
		// [par mark, 0.0096726 secs]
		// [reference processing, 0.0000857 secs]
		// [class unloading, 0.0013404 secs]
		// [marking phase, 0.0112408 secs]
		// [summary phase, 0.0017760 secs]
		// [adjust roots, 0.0012449 secs]
		// [compact perm gen, 0.0103245 secs]
		// [drain task setup, 0.0000919 secs]
		// [dense prefix task setup, 0.0000043 secs]
		// [steal task setup, 0.0000013 secs]
		// [par compact, 0.0074996 secs]
		// [deferred updates, 0.0026894 secs]
		// [compaction phase, 0.0105486 secs]
		// [post compact, 0.0015983 secs]
		"pre compact",
		"par mark",
		"marking flush",
		"reference processing",
		"class unloading",
		"(par )?marking phase",
		"summary phase",
		"adjust roots",
		"compact perm gen",
		"drain(ing)? task setup",
		"dense prefix task setup",
		"steal task setup",
		"par compact",
		"deferred updates",
		"compaction phase",
		"post compact"
	};

	// The metrics that correspond to the above phase names.
	private static GCMetric _metrics[] =
	{
		GCMetric.pc_pre_comp,
		GCMetric.pc_par_mark,
		GCMetric.pc_mark_flush,
		GCMetric.pc_ref_proc,
		GCMetric.pc_cls_unload,
		GCMetric.pc_mark,
		GCMetric.pc_summary,
		GCMetric.pc_adj_roots,
		GCMetric.pc_perm_gen,
		GCMetric.pc_drain_ts,
		GCMetric.pc_dpre_ts,
		GCMetric.pc_steal_ts,
		GCMetric.pc_par_comp,
		GCMetric.pc_deferred,
		GCMetric.pc_compact,
		GCMetric.pc_post_comp,
	};

	private static Pattern _patterns[];

	static
	{
		final int n = _phase_name_res.length;
		_patterns = new Pattern[n];
		for (int i = 0; i < n; ++i)
		{
			_patterns[i] = phase_pattern(_phase_name_res[i]);
		}
	}
}
