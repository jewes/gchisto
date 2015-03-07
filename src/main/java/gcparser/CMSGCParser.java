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

public class CMSGCParser extends GCParser
{
	public CMSGCParser(GCStats gcstats, boolean verbose)
	{
		super(gcstats, verbose);

// 		_debug = System.err;	// XXX
// 		debug(_cms_imark_pattern.pattern()); debug();
// 		debug(_cms_rmark_pattern.pattern()); debug();
// 		debug(_cms_concurrent_phase_pattern.pattern()); debug();
	}

	/*
	** Would like to have values for og_used_end, og_commit_end,
	** th_used_end, and th_commit at the end of the cms cycle (end of
	** reset), but they're not available.
	*/
	public boolean parse(String filename, int line, String s)
	{
		Matcher m;

		m = _cms_phase_beg_pattern.matcher(s);
		if (m.find())
		{
			_cms_phase_beg_matcher = m;
			_cms_phase_beg_timestamp = get_ts(m,
				_cms_phase_beg_timestamp_grp);
			return true;
		}

		m = _cms_concurrent_phase_pattern.matcher(s);
		if (m.find())
		{
			save_match_state(filename, line, s);

			concurrent_phase_end(m);
			return true;
		}

		m = _cms_imark_pattern.matcher(s);
		if (m.find())
		{
			save_match_state(filename, line, s);
			debug_imark(s, m);

			// These values are available at the start of the cms
			// cycle (initial mark), but not at the end (reset).
			// Recording them here makes the stats confusing, so
			// they are skipped.
// 			add_sz(GCMetric.og_used_beg, m,
// 				_cms_imark_og_used_beg_grp);
// 			add_sz(GCMetric.th_used_beg, m,
// 				_cms_imark_th_used_beg_grp);

			double timestamp = get_ts(m, _cms_imark_timestamp_grp);
			String str = m.group(_cms_imark_time_grp);
			double pause_time = Double.parseDouble(str);

			add_pt(GCMetric.cms_im_time, timestamp, pause_time);

			if (!gcstats().has_generated_timestamps())
			{
				TimingWindowData tw = gcstats().timing_window();
				tw.add_pause_time(timestamp, pause_time);
			}

			return true;
		}

		m = _cms_rmark_pattern.matcher(s);
		if (m.find())
		{
			save_match_state(filename, line, s);
// 			add_sz(GCMetric.og_used_beg, m,
// 				_cms_rmark_og_used_beg_grp);
// 			add_sz(GCMetric.th_used_beg, m,
// 				_cms_rmark_th_used_beg_grp);

			double timestamp = get_ts(m, _cms_rmark_timestamp_grp);
			String str = m.group(_cms_rmark_time_grp);
			double pause_time = Double.parseDouble(str);

			add_pt(GCMetric.cms_rm_time, timestamp, pause_time);

			if (!gcstats().has_generated_timestamps())
			{
				TimingWindowData tw = gcstats().timing_window();
				tw.add_pause_time(timestamp, pause_time);
			}

			return true;
		}

		return false;
	}

	private void concurrent_phase_end(Matcher m)
	{
		boolean is_reset = false;

// 		debug(_text); debug();
// 		debug(m, _cms_concurrent_phase_name_grp, "phase");
// 		debug(" ");
// 		debug(m, _cms_concurrent_phase_active_time_grp, "a");
// 		debug(" ");
// 		debug(m, _cms_concurrent_phase_elapsed_time_grp, "e");
// 		debug();

		GCMetric active_metric = null;
		GCMetric elapsed_metric = null;
		String phase = m.group(_cms_concurrent_phase_name_grp);
		if (phase.equals("mark"))
		{
			active_metric = GCMetric.cms_cm_a_time;
			elapsed_metric = GCMetric.cms_cm_e_time;
		}
		else if (phase.indexOf("preclean") >= 0)
		{
			active_metric = GCMetric.cms_cp_a_time;
			elapsed_metric = GCMetric.cms_cp_e_time;
		}
		else if (phase.equals("sweep"))
		{
			active_metric = GCMetric.cms_cs_a_time;
			elapsed_metric = GCMetric.cms_cs_e_time;
		}
		else if (phase.equals("reset"))
		{
			is_reset = true;
			active_metric = GCMetric.cms_cr_a_time;
			elapsed_metric = GCMetric.cms_cr_e_time;
		}

		double active_time = Double.parseDouble(
			m.group(_cms_concurrent_phase_active_time_grp));
		add_dp(active_metric, active_time);
		add_dp(elapsed_metric, m,
			_cms_concurrent_phase_elapsed_time_grp);

		if (_cms_phase_beg_matcher == null) return;

		// Add just one timestamp even though there are two
		// metrics (active and elapsed) since the underlying
		// timestamp list is shared.
		double timestamp_end = get_ts(m,
			_cms_concurrent_phase_timestamp_grp);
		add_ts(active_metric, _cms_phase_beg_timestamp,
			timestamp_end);

		if (gcstats().has_generated_timestamps()) return;

		TimingWindowData tw = gcstats().timing_window();
		tw.add_concurrent_time(timestamp_end, active_time, 1);
		if (is_reset)
		{
			final double th_alloc_sum =
				gcstats().stats(GCMetric.th_alloc).sum();
			final double alloc = th_alloc_sum - tw.th_alloc();

			add_dp(GCMetric.th_alloc_cpu,
				alloc / tw.elapsed_cpu_time());
			add_dp(GCMetric.th_alloc_mut,
				alloc / tw.mutator_cpu_time());

			// Add a timestamp to just one of the metrics since the
			// underlying list is shared.
			add_ts(GCMetric.th_alloc_cpu, _cms_phase_beg_timestamp,
				timestamp_end);

			tw.reset(timestamp_end, th_alloc_sum);
		}
	}

	private void debug_imark(String s, Matcher m)
	{
		if (_debug == null) return;
		debug(s); debug();
		debug(m, _cms_imark_og_used_beg_grp,
			GCMetric.og_used_beg);
		debug(" ");
		debug(m, _cms_imark_th_used_beg_grp,
			GCMetric.th_used_beg);
		debug(" ");
		debug(m, _cms_imark_time_grp, GCMetric.cms_im_time);
		debug();
	}

	private Matcher _cms_phase_beg_matcher;
	private double  _cms_phase_beg_timestamp;

	private static Pattern _cms_imark_pattern = Pattern.compile(
		timestamp_re + "\\[GC \\[1 (AS)?CMS-initial-mark: " +
		cms_heap_size_re + "\\] " +
		cms_heap_report_re + "\\]");

	private static final int _cms_imark_timestamp_grp =
		timestamp_re_time_group;
	private static final int _cms_imark_og_used_beg_grp =
		timestamp_re_groups + 2;
	private static final int _cms_imark_og_commit_end_grp =
		_cms_imark_og_used_beg_grp + heap_size_re_groups;
	private static final int _cms_imark_th_used_beg_grp =
		_cms_imark_og_commit_end_grp + heap_size_paren_re_groups;
	private static final int _cms_imark_th_commit_end_grp =
		_cms_imark_th_used_beg_grp + heap_size_re_groups;
	private static final int _cms_imark_time_grp =
		timestamp_re_groups + 1 + cms_heap_size_re_groups +
		cms_heap_report_re_groups - 1;

	private static Pattern _cms_rmark_pattern = Pattern.compile(
		timestamp_re + "\\[GC.*\\[1 (AS)?CMS-remark: " +
		cms_heap_size_re + "\\] " +
		cms_heap_report_re + "\\]");
	private static final int _cms_rmark_timestamp_grp =
		timestamp_re_time_group;
	private static final int _cms_rmark_time_grp =
		timestamp_re_groups + 1 + cms_heap_size_re_groups +
		cms_heap_report_re_groups - 1;

	private static Pattern _cms_phase_beg_pattern = Pattern.compile(
		timestamp_re + "\\[" + cms_concurrent_phase_name_re +
		"-start\\]");
	private static int _cms_phase_beg_timestamp_grp =
		timestamp_re_time_group;

	private static Pattern _cms_concurrent_phase_pattern = Pattern.compile(
		timestamp_re + "\\[" + cms_concurrent_phase_name_re + ": " +
		gc_time_re + "/" + gc_time_secs_re + "\\]");
	private static final int _cms_concurrent_phase_timestamp_grp =
		timestamp_re_time_group;
	private static final int _cms_concurrent_phase_name_grp = 
		timestamp_re_groups + cms_concurrent_phase_name_group;
	private static final int _cms_concurrent_phase_active_time_grp = 
		timestamp_re_groups + cms_concurrent_phase_name_re_groups + 1;
	private static final int _cms_concurrent_phase_elapsed_time_grp = 
		timestamp_re_groups + cms_concurrent_phase_name_re_groups + 
		gc_time_re_groups + gc_time_secs_re_time_group;
}
