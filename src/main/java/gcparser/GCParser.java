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

import java.io.PrintStream;
import java.util.regex.*;

public abstract class GCParser
{
	// Frequently-used regular expressions.
	public static final String full_gc_re 	=
		"\\[Full GC (\\(System\\) )?";
	public static final int full_gc_re_groups = 1;

	public static final String heap_size_re =
		"([0-9]+)([KM])";
	public static final int heap_size_re_size_group = 1;
	public static final int heap_size_re_unit_group = 2;
	public static final int heap_size_re_groups = 2;

	public static final String heap_size_paren_re =
		"\\(" + heap_size_re + "\\)";
	public static final int heap_size_paren_re_groups =
		heap_size_re_groups;

	public static final String heap_size_change_re =
		heap_size_re + "->" + heap_size_re;
	public static final int heap_size_change_re_size_beg_group =
		heap_size_re_size_group;
	public static final int heap_size_change_re_unit_beg_group =
		heap_size_re_unit_group;
	public static final int heap_size_change_re_size_end_group =
		heap_size_re_groups + heap_size_re_size_group;
	public static final int heap_size_change_re_unit_end_group =
		heap_size_re_groups + heap_size_re_unit_group;
	public static final int heap_size_change_re_groups =
		heap_size_re_groups * 2;

	// 8K->4K(96K), or 8K->4K (96K)
	public static final String heap_size_status_re =
		heap_size_change_re + " ?" + heap_size_paren_re;

	public static final int heap_size_status_re_size_beg_group =
		heap_size_change_re_size_beg_group;
	public static final int heap_size_status_re_unit_beg_group =
		heap_size_change_re_unit_beg_group;
	public static final int heap_size_status_re_size_end_group =
		heap_size_change_re_size_end_group;
	public static final int heap_size_status_re_unit_end_group =
		heap_size_change_re_unit_end_group;
	public static final int heap_size_status_re_commit_size_group =
		heap_size_change_re_groups + heap_size_re_size_group;
	public static final int heap_size_status_re_commit_unit_group =
		heap_size_change_re_groups + heap_size_re_unit_group;
	public static final int heap_size_status_re_groups =
		heap_size_change_re_groups + heap_size_paren_re_groups;

	public static final String gc_time_re =
		"([0-9]+\\.[0-9]+)";
	public static final int gc_time_re_groups = 1;

	public static final String gc_time_secs_re =
		gc_time_re + " (secs)";
	public static final int gc_time_secs_re_time_group = 1;
	public static final int gc_time_secs_re_unit_group = 2;
	public static final int gc_time_secs_re_groups =
		gc_time_re_groups + 1;

	public static final String gc_time_ms_re =
		gc_time_re + " (ms)";
	public static final int gc_time_ms_re_groups = 
		gc_time_re_groups + 1;

	public static final String timestamp_re =
		"(" + gc_time_re + ": *)?";
	public static final int timestamp_re_time_group = 2;
	public static final int timestamp_re_groups =
		gc_time_re_groups + 1;

	public static final String timestamp_range_re =
		"(" + gc_time_re + "-" + gc_time_re + ": *)?";
	public static final int timestamp_range_re_groups =
		gc_time_re_groups * 2 + 1;

	// Heap size status plus elapsed time:  8K->4K(96K), 0.0517089 secs
	public static final String heap_report_re =
		heap_size_status_re + ", " + gc_time_secs_re;
	public static final int heap_report_re_size_beg_group =
		heap_size_status_re_size_beg_group;
	public static final int heap_report_re_unit_beg_group =
		heap_size_status_re_unit_beg_group;
	public static final int heap_report_re_size_end_group =
		heap_size_status_re_size_end_group;
	public static final int heap_report_re_unit_end_group =
		heap_size_status_re_unit_end_group;
	public static final int heap_report_re_commit_size_group =
		heap_size_status_re_commit_size_group;
	public static final int heap_report_re_commit_unit_group =
		heap_size_status_re_commit_unit_group;
	public static final int heap_report_re_time_group =
		heap_size_status_re_groups + gc_time_secs_re_time_group;
	public static final int heap_report_re_groups =
		heap_size_status_re_groups + gc_time_secs_re_groups;

	// Size printed at CMS initial mark and remark.
	public static final String cms_heap_size_re =
		heap_size_re + heap_size_paren_re;
	public static final int cms_heap_size_re_groups =
		heap_size_re_groups + heap_size_paren_re_groups;

	public static final String cms_heap_report_re =
		cms_heap_size_re + ", " + gc_time_secs_re;
	public static final int cms_heap_report_re_groups =
		cms_heap_size_re_groups + gc_time_secs_re_groups;

	public static final String cms_concurrent_phase_name_re =
		"(AS)?CMS-concurrent-(mark|(abortable-)?preclean|sweep|reset)";
	public static final int cms_concurrent_phase_name_re_groups = 3;
	public static final int cms_concurrent_phase_name_group = 2;

	// Generations which print optional messages.
	public static final String promo_failed_re =
		"(--| \\(promotion failed\\))?";
	public static final int promo_failed_re_groups = 1;
	public static final String pargc_young_gc_re =
		"\\[GC" + promo_failed_re + " ";
	public static final int pargc_young_gc_re_groups =
		promo_failed_re_groups;

	public static final String cms_gen_re =
		"(AS)?CMS( \\(concurrent mode failure\\))?";
	public static final int cms_gen_re_groups = 2;
	public static final String cms_perm_gen_re =
		"CMS Perm";
	public static final int cms_perm_gen_re_groups = 0;

	// 'Framework' GCs:  DefNew, ParNew, Tenured, CMS
	public static final String fw_young_gen_re =
		"(DefNew|(AS)?ParNew)" + promo_failed_re;
	public static final int fw_young_gen_re_groups =
		2 + promo_failed_re_groups;
 
	public static final String fw_old_gen_re =
		"(Tenured|" + cms_gen_re + ")";
	public static final int fw_old_gen_re_groups =
		1 + cms_gen_re_groups;
	public static final String fw_perm_gen_re =
		"(Perm|" + cms_perm_gen_re + ")";
	public static final int fw_perm_gen_re_groups =
		1 + cms_perm_gen_re_groups;

	// Garbage First (G1) pauses:
	//    [GC pause (young), 0.0082 secs]
	// or [GC pause (partial), 0.082 secs]
	// or [GC pause (young) (initial mark), 0.082 secs]
	// or [GC remark, 0.082 secs]
	// or [GC cleanup 11M->11M(25M), 0.126 secs]
	public static final String g1_cleanup_re =
		"cleanup " + heap_size_status_re;
	public static final String g1_pause_re =
		"pause \\((young|partial)\\)" +
		"( \\((initial-mark|evacuation failed)\\))?";
	public static final String g1_stw_re =
		"\\[GC (" + g1_pause_re + "|remark|" + g1_cleanup_re + "), " +
		gc_time_secs_re + "\\]";

	public static final String pargc_young_gen_name_re = "PSYoungGen";
	public static final int pargc_young_gen_name_re_groups = 0;

	public static final String pargc_old_gen_name_re = "(PS|Par)OldGen";
	public static final int pargc_old_gen_name_re_groups = 1;

	public static final String pargc_perm_gen_name_re = "PSPermGen";
	public static final int pargc_perm_gen_name_re_groups = 0;

	/* ------------------------------------------------------------------ */

	public GCParser(GCStats gcstats, boolean verbose, PrintStream vstream)
	{
		if (verbose && vstream == null)
		{
			throw new IllegalArgumentException(
				"null vstream when verbose == true");
		}
		_gcstats = gcstats;
		_verbose = verbose;
		_vstream = vstream;
	}

	public GCParser(GCStats gcstats)
	{
		this(gcstats, false, null);
	}

	public GCParser(GCStats gcstats, boolean verbose)
	{
		this(gcstats, verbose, verbose ? System.out : null);
	}

	public GCStats gcstats() { return _gcstats; }

	public abstract boolean parse(String filename, int line, String s);

	public boolean verbose() { return _verbose; }
	public PrintStream vstream() { return _vstream; }

	public String trace_line(String filename, int line, String s)
	{
		StringBuilder sb = new StringBuilder();
		if (filename != null)
		{
			sb.append(filename);
			sb.append(':');
		}
		sb.append(line);
		sb.append(':');
		sb.append(s);
		return sb.toString();
	}

	public String trace_line(String s)
	{
		return trace_line(_filename, _line, s);
	}

	public void vtrace(String filename, int line, String s)
	{
		if (verbose())
		{
			vstream().println(trace_line(filename, line, s));
		}
	}

	public void vtrace()
	{
		vtrace(_filename, _line, _text);
	}

	public void vtrace(String s)
	{
		vtrace(_filename, _line, s);
	}

	public long match_count() { return _match_count; }

	protected void save_match_state(String filename, int line, String s)
	{
		_filename = filename;
		_line = line;
		_text = s;
		++_match_count;
	}

	// Convert the strings to a size value in MB.  Unit is either "KB" or
	// "MB" (see heap_size_re); "GB" is also accepted.
	protected double heap_size_in_mb(String size, String unit)
	{
		double val = Double.parseDouble(size);
		if (unit.charAt(0) == 'K') return val / 1024.0;
		if (unit.charAt(0) == 'M') return val;
		if (unit.charAt(0) == 'G') return val * 1024.0;
		throw new IllegalArgumentException("unrecognized unit " + unit);
	}

	// Extract a number and a unit (KB or MB) from the matcher and return a
	// number in MB (converting if necessary).
	protected double get_sz(Matcher m, int group)
	{
		return heap_size_in_mb(m.group(group), m.group(group + 1));
	}

	protected double get_ts(Matcher m, int group)
	{
		String s = m.group(group);
		return s != null ? Double.parseDouble(s) :
			gcstats().generate_timestamp();
	}

	// Add a generic data point.
	protected void add_dp(GCMetric metric, double d)
	{
		vtrace(metric.toString() + ':' + d);
		gcstats().add(metric, d);
	}

	protected void add_dp(GCMetric metric, Matcher m, int group)
	{
		add_dp(metric, Double.parseDouble(m.group(group)));
	}

	// Add a time stamp.
	protected void add_ts(GCMetric metric, double beg, double end)
	{
		vtrace(metric.toString() + ".beg:" + beg);
		gcstats().add_timestamp(metric, beg, end);
	}

// 	protected void add_ts(GCMetric metric, Matcher m, int group)
// 	{
// 		double d = get_ts(m, group);
// 		add_ts(metric, d, d);
// 	}
	
	// Add a pause time data point.
	protected void add_pt(GCMetric metric, double pause_time)
	{
		vtrace(metric.toString() + ':' + pause_time);
		gcstats().add(metric, pause_time);
		gcstats().add(GCMetric.tgc_time, pause_time);
	}

	// Add a pause time data point with a timestamp.
	protected void
	add_pt(GCMetric metric, double timestamp, double pause_time)
	{
		add_pt(metric, pause_time);
		add_ts(metric, timestamp, timestamp + pause_time);
		add_ts(GCMetric.tgc_time, timestamp, timestamp + pause_time);
	}

	// Add a pause time data point.
	protected void add_pt(GCMetric metric, Matcher m, int group)
	{
		final double d = Double.parseDouble(m.group(group));
		add_pt(metric, d);
	}

	// Add a pause time data point with a timestamp.
	protected void add_pt(GCMetric metric, Matcher m, int timestamp_group,
		int pause_group)
	{
		final double ts = get_ts(m, timestamp_group);
		final double pt = Double.parseDouble(m.group(pause_group));
		add_pt(metric, ts, pt);
	}

	// Add a size data point to the specified metric.
	protected void add_sz(GCMetric metric, double val)
	{
		vtrace(metric.toString() + ':' + val);
		gcstats().add(metric, val);
	}

	// Extract a heap size value and unit from the matcher, convert to MB
	// and add the value to the specified metric.
	protected void add_sz(GCMetric metric, Matcher m, int group)
	{
		add_sz(metric, get_sz(m, group));
	}

	protected static double
	young_gc_amount_promoted(double yg_used_beg, double yg_used_end,
		double th_used_beg, double th_used_end)
	{
		// A GC *should* only reduce the heap usage.  Given that
		// assumption, the amount promoted is:
		// 
		// reduction in young gen occupancy - reduction in total
		//   heap occupancy
		// 
		// However, there are some gotchas.  The parallel collectors use
		// thread-local promotion buffers (PLABs) in the old gen and
		// survivor spaces.  Those parts of buffers which are not
		// completely used during GC are filled with dummy objects which
		// are considered live (at least in terms of heap usage).  This
		// is most prevalent when the heap is very full, in which case a
		// GC can appear to *increase* heap occupancy.  In addition, the
		// original values are reported in KB (or MB), so the truncation
		// can result in negative values, which are ignored.
		final double yg_delta = yg_used_beg - yg_used_end;
		final double th_delta = th_used_beg - th_used_end;
		final double raw_promo = yg_delta - th_delta;
		return Math.max(raw_promo, 0.0);
	}

	// This is just a (good) approximation.  See comments below as well as
	// the comments in young_gc_amount_promoted for sources of inaccuracy.
	protected double young_gc_amount_allocated(double yg_used_beg,
		double th_used_beg)
	{
		double prev_gc_used_end = gcstats().heap_used_end();
		if (th_used_beg >= prev_gc_used_end)
		{
			// The heap didn't shrink between the last time heap
			// sizes were recorded and the start of the current GC.
			return th_used_beg - prev_gc_used_end;
		}

		// The heap shrank, which is usually due to concurrent
		// collection.  Try to approximate allocation by young
		// generation allocation.
		prev_gc_used_end = gcstats().yg_used_end();
		if (yg_used_beg >= prev_gc_used_end)
		{
			return yg_used_beg - prev_gc_used_end;
		}

		// What to do here?  For now, give up.
		debug("unable to compute amount allocated"); debug();
		return 0.0;
	}

	protected void add_rate(GCMetric metric, double value,
		double prev_timestamp, double cur_timestamp)
	{
		if (prev_timestamp >= 0.0)
		{
			double elapsed = cur_timestamp - prev_timestamp;
			add_dp(metric, value / elapsed);
		}
	}

// 	private void
// 	add_timing_window_pause(double pause_time, double timestamp)
// 	{
// 		TimingWindowData twd = gcstats().timing_window();
// 		twd.add_pause_time(timestamp, pause_time);
// 	}

	protected void
	add_young_gc(double timestamp_beg,
		double yg_used_beg, double yg_used_end, double yg_commit_end,
		double th_used_beg, double th_used_end, double th_commit_end,
		double pause_time)
	{
		double timestamp_end = timestamp_beg + pause_time;

		add_pt(GCMetric.ygc_time,      pause_time);

		add_sz(GCMetric.yg_used_beg,   yg_used_beg);
		add_sz(GCMetric.yg_used_end,   yg_used_end);
		add_sz(GCMetric.yg_commit_end, yg_commit_end);
		add_sz(GCMetric.th_used_beg,   th_used_beg);
		add_sz(GCMetric.th_used_end,   th_used_end);
		add_sz(GCMetric.th_commit_end, th_commit_end);

		add_ts(GCMetric.yg_used_beg,   timestamp_beg, timestamp_end);
		add_ts(GCMetric.th_used_beg,   timestamp_beg, timestamp_end);
		add_ts(GCMetric.ygc_time,      timestamp_beg, timestamp_end);
		add_ts(GCMetric.tgc_time,      timestamp_beg, timestamp_end);

		final double yg_promo = young_gc_amount_promoted(yg_used_beg,
			yg_used_end, th_used_beg, th_used_end);

		final double th_alloc = young_gc_amount_allocated(yg_used_beg,
			th_used_beg);

		add_sz(GCMetric.yg_promo, yg_promo);
		add_sz(GCMetric.th_alloc, th_alloc);
		add_ts(GCMetric.yg_promo, timestamp_beg, timestamp_end);
		add_ts(GCMetric.th_alloc, timestamp_beg, timestamp_end);

		add_dp(GCMetric.yg_promo_ygc, yg_promo / pause_time);
		if (!gcstats().has_generated_timestamps())
		{
			add_rate(GCMetric.yg_promo_ela, yg_promo,
				gcstats().yg_timestamp_end(), timestamp_end);

			double prev_end = gcstats().heap_timestamp_end();
			add_rate(GCMetric.th_alloc_ela, th_alloc, prev_end,
				timestamp_end);

			if (prev_end >= 0.0)
			{
				TimingWindowData tw = gcstats().timing_window();
				tw.add_pause_time(timestamp_end, pause_time);
			}
		}

		gcstats().save_yg_info(yg_used_beg, yg_used_end,
			yg_commit_end, timestamp_beg, timestamp_end);
		gcstats().save_heap_sizes(th_used_beg, th_used_end,
			th_commit_end, timestamp_beg, timestamp_end);
	}

	protected void
	add_young_gc(Matcher m, int timestamp_grp,
		int yg_used_beg_grp, int yg_used_end_grp, int yg_commit_end_grp,
		int th_used_beg_grp, int th_used_end_grp, int th_commit_end_grp,
		int pause_time_grp)
	{
		add_young_gc(get_ts(m, timestamp_grp),
			get_sz(m, yg_used_beg_grp),
			get_sz(m, yg_used_end_grp),
			get_sz(m, yg_commit_end_grp),
			get_sz(m, th_used_beg_grp),
			get_sz(m, th_used_end_grp),
			get_sz(m, th_commit_end_grp),
			Double.parseDouble(m.group(pause_time_grp)));
	}

	protected void add_full_gc(double timestamp_beg,
		double og_used_beg, double og_used_end, double og_commit_end,
		double th_used_beg, double th_used_end, double th_commit_end,
		double pause_time)
	{
		double timestamp_end = timestamp_beg + pause_time;

		add_pt(GCMetric.fgc_time, pause_time);

		add_sz(GCMetric.og_used_beg,   og_used_beg);
		add_sz(GCMetric.og_used_end,   og_used_end);
		add_sz(GCMetric.og_commit_end, og_commit_end);
		add_sz(GCMetric.th_used_beg,   th_used_beg);
		add_sz(GCMetric.th_used_end,   th_used_end);
		add_sz(GCMetric.th_commit_end, th_commit_end);

		add_ts(GCMetric.og_used_beg, timestamp_beg, timestamp_end);
		add_ts(GCMetric.th_used_beg, timestamp_beg, timestamp_end);
		add_ts(GCMetric.fgc_time,    timestamp_beg, timestamp_end);
		add_ts(GCMetric.tgc_time,    timestamp_beg, timestamp_end);

		final double th_alloc = th_used_beg - gcstats().heap_used_end();
		add_sz(GCMetric.th_alloc, th_alloc);
		add_ts(GCMetric.th_alloc, timestamp_beg, timestamp_end);

		if (!gcstats().has_generated_timestamps())
		{
			double prev_end = gcstats().heap_timestamp_end();
			add_rate(GCMetric.th_alloc_ela, th_alloc, prev_end,
				timestamp_end);

			TimingWindowData tw = gcstats().timing_window();
			Stats tha_stats = gcstats().stats(GCMetric.th_alloc);
			if (prev_end >= 0.0)
			{
				tw.add_pause_time(timestamp_end, pause_time);

				final double alloc =
					tha_stats.sum() - tw.th_alloc();

				add_dp(GCMetric.th_alloc_cpu,
					alloc / tw.elapsed_cpu_time());

				add_dp(GCMetric.th_alloc_mut,
					alloc / tw.mutator_cpu_time());

				// Add a timestamp to just one of the metrics
				// since the underlying list is shared.
				add_ts(GCMetric.th_alloc_cpu, timestamp_beg,
					timestamp_end);
			}

			tw.reset(timestamp_end, tha_stats.sum());
		}

		gcstats().save_heap_sizes(th_used_beg, th_used_end,
			th_commit_end, timestamp_beg, timestamp_end);
	}

	protected void add_full_gc(double timestamp_beg,
		double og_used_beg, double og_used_end, double og_commit_end,
		double th_used_beg, double th_used_end, double th_commit_end,
		double pg_used_beg, double pg_used_end, double pg_commit_end,
		double pause_time)
	{
		add_full_gc(timestamp_beg,
			og_used_beg, og_used_end, og_commit_end,
			th_used_beg, th_used_end, th_commit_end,
			pause_time);

		double timestamp_end = timestamp_beg + pause_time;

		add_sz(GCMetric.pg_used_beg,   pg_used_beg);
		add_sz(GCMetric.pg_used_end,   pg_used_end);
		add_sz(GCMetric.pg_commit_end, pg_commit_end);

		add_ts(GCMetric.pg_used_beg, timestamp_beg, timestamp_end);
	}

	protected void add_full_gc(Matcher m, int timestamp_grp,
		int og_used_beg_grp, int og_used_end_grp, int og_commit_end_grp,
		int th_used_beg_grp, int th_used_end_grp, int th_commit_end_grp,
		int pause_time_grp)
	{
		add_full_gc(get_ts(m, timestamp_grp),
			get_sz(m, og_used_beg_grp),
			get_sz(m, og_used_end_grp),
			get_sz(m, og_commit_end_grp),
			get_sz(m, th_used_beg_grp),
			get_sz(m, th_used_end_grp),
			get_sz(m, th_commit_end_grp),
			Double.parseDouble(m.group(pause_time_grp)));
	}

	protected void add_full_gc(Matcher m, int timestamp_grp,
		int og_used_beg_grp, int og_used_end_grp, int og_commit_end_grp,
		int th_used_beg_grp, int th_used_end_grp, int th_commit_end_grp,
		int pg_used_beg_grp, int pg_used_end_grp, int pg_commit_end_grp,
		int pause_time_grp)
	{
		add_full_gc(get_ts(m, timestamp_grp),
			get_sz(m, og_used_beg_grp),
			get_sz(m, og_used_end_grp),
			get_sz(m, og_commit_end_grp),
			get_sz(m, th_used_beg_grp),
			get_sz(m, th_used_end_grp),
			get_sz(m, th_commit_end_grp),
			get_sz(m, pg_used_beg_grp),
			get_sz(m, pg_used_end_grp),
			get_sz(m, pg_commit_end_grp),
			Double.parseDouble(m.group(pause_time_grp)));
	}

	protected final GCStats _gcstats;
	protected final boolean _verbose;
	// The output stream for verbose mode.
	protected final PrintStream _vstream;

	// The parse 'state':  the input filename (null if none), the line
	// number and the text on the line currently being parsed.  Each
	// subclass should call save_match_state() with this information when a
	// line is matched.
	protected String _filename;	// Input filename (or null).
	protected int    _line;		// Line number.
	protected String _text;		// Text from the line.
	protected long   _match_count;  // Number of lines matched.

	/* ------------------------------------------------------------------ */

	// XXX - debugging.
	protected PrintStream _debug;	// XXX
	protected void debug() { if (_debug != null) _debug.println(); }
	protected void debug(String s) { if (_debug != null) _debug.print(s); }
	protected void debug(Matcher m, int grp, String grp_name)
	{
		if (_debug != null)
		{
			_debug.print(grp_name + " " + grp + " " + m.group(grp));
		}
	}
	protected void debug(Matcher m, int grp, GCMetric metric)
	{
		debug(m, grp, metric.toString());
	}
}
