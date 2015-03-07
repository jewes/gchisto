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

public class FWFullGCParser extends GCParser
{
	public FWFullGCParser(GCStats gcstats, boolean verbose)
	{
		super(gcstats, verbose);
		// _debug = System.err;	// XXX
	}

	public boolean parse(String filename, int line, String s)
	{
		Matcher m = _pattern.matcher(s);
		if (m.find())
		{
			save_match_state(filename, line, s);
			// debug(s, m);	// XXX
			add_full_gc(m, timestamp_grp,
				og_used_beg_grp, og_used_end_grp,
				og_commit_end_grp, th_used_beg_grp,
				th_used_end_grp, th_commit_end_grp,
				pg_used_beg_grp, pg_used_end_grp,
				pg_commit_end_grp, gc_time_grp);
			return true;
		}

		return false;
	}

	// XXX
	private void debug(String s, Matcher m)
	{
		if (_debug == null) return;
		debug(s); debug();
		debug(m, og_used_beg_grp,   "oub");
		debug(m, og_used_end_grp,   " oue");
		debug(m, og_commit_end_grp, " oco");
		debug();
		debug(m, th_used_beg_grp,   "tub");
		debug(m, th_used_end_grp,   " tue");
		debug(m, th_commit_end_grp, " tco");
		debug();
		debug(m, pg_heap_size_status_grp, "p heap size status");
		debug();
		debug(m, pg_used_beg_grp,   "pub");
		debug(m, pg_used_end_grp,   " pue");
		debug(m, pg_commit_end_grp, " pco");
		debug(m, gc_time_grp,       " gct");
		debug();
	}

	// The 'framework' collectors have two different 'Full GC' output
	// formats, one when the decision to collect the entire heap is made
	// after a young gen collection was attempted, and one when the decision
	// is made before attempting a young gen collection.
	// 
	// This parser recognizes the latter:
	// 
	// 2.228: [Full GC 2.228: [Tenured: 348940K->348940K(349568K), 0.1496737 secs] 488012K->488005K(506816K), [Perm: 1653K->1653K(16384K)], 0.1497941 secs]

	private static Pattern _pattern = Pattern.compile(
		timestamp_re + full_gc_re +
		timestamp_re + "\\[" + fw_old_gen_re + ": " +
		heap_report_re + "\\] " +
		heap_size_status_re + ", \\[" + fw_perm_gen_re + ": " +
		heap_size_status_re + "\\], " + gc_time_secs_re + "\\]");

	private static final int timestamp_grp = timestamp_re_time_group;

	private static final int og_heap_report_grp =
		timestamp_re_groups + full_gc_re_groups + timestamp_re_groups +
		fw_old_gen_re_groups;
	private static final int og_used_beg_grp =
		og_heap_report_grp + heap_report_re_size_beg_group;
	private static final int og_used_end_grp =
		og_heap_report_grp + heap_report_re_size_end_group;
	private static final int og_commit_end_grp =
		og_heap_report_grp + heap_report_re_commit_size_group;

	private static final int th_heap_size_status_grp =
		og_heap_report_grp + heap_report_re_groups;
	private static final int th_used_beg_grp =
		th_heap_size_status_grp + heap_size_status_re_size_beg_group;
	private static final int th_used_end_grp =
		th_heap_size_status_grp + heap_size_status_re_size_end_group;
	private static final int th_commit_end_grp =
		th_heap_size_status_grp + heap_size_status_re_commit_size_group;

	private static final int pg_heap_size_status_grp =
		th_heap_size_status_grp + heap_size_status_re_groups +
		fw_perm_gen_re_groups;
	private static final int pg_used_beg_grp =
		pg_heap_size_status_grp + heap_size_status_re_size_beg_group;
	private static final int pg_used_end_grp =
		pg_heap_size_status_grp + heap_size_status_re_size_end_group;
	private static final int pg_commit_end_grp =
		pg_heap_size_status_grp + heap_size_status_re_commit_size_group;

	private static final int gc_time_grp =
		pg_heap_size_status_grp + heap_size_status_re_groups +
		gc_time_secs_re_time_group;
}
