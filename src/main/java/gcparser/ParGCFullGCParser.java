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

public class ParGCFullGCParser extends GCParser
{
	public ParGCFullGCParser(GCStats gcstats, boolean verbose)
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
			debug(s, m);	// XXX
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
		debug(m, yg_used_beg_grp,   "yub");
		debug(m, yg_used_end_grp,   " yue");
		debug(m, yg_commit_end_grp, " yco");
		debug();
		debug(m, og_used_beg_grp,   "oub");
		debug(m, og_used_end_grp,   " oue");
		debug(m, og_commit_end_grp, " oco");
		debug();
		debug(m, th_used_beg_grp,   "tub");
		debug(m, th_used_end_grp,   " tue");
		debug(m, th_commit_end_grp, " tco");
		debug();
	}

	private static Pattern _pattern = Pattern.compile(
		timestamp_re + full_gc_re +
		"\\[" + pargc_young_gen_name_re + ": +" +
		heap_size_status_re + "\\] " +
		"\\[" + pargc_old_gen_name_re + ": +" +
		heap_size_status_re + "\\] " +
		heap_size_status_re + " " +
		"\\[" + pargc_perm_gen_name_re + ": +" +
		heap_size_status_re + "\\], " + 
		gc_time_secs_re + "\\]");

	private static final int timestamp_grp = timestamp_re_time_group;

	private static final int yg_used_beg_grp =
		timestamp_re_groups + full_gc_re_groups +
		pargc_young_gen_name_re_groups + 1;
	private static final int yg_used_end_grp =
		yg_used_beg_grp + heap_size_re_groups;
	private static final int yg_commit_end_grp =
		yg_used_end_grp + heap_size_re_groups;

	private static final int og_used_beg_grp =
		yg_commit_end_grp + heap_size_re_groups +
		pargc_old_gen_name_re_groups;
	private static final int og_used_end_grp =
		og_used_beg_grp + heap_size_re_groups;
	private static final int og_commit_end_grp =
		og_used_end_grp + heap_size_re_groups;

	private static final int th_used_beg_grp =
		og_commit_end_grp + heap_size_re_groups;
	private static final int th_used_end_grp =
		th_used_beg_grp + heap_size_re_groups;
	private static final int th_commit_end_grp =
		th_used_end_grp + heap_size_re_groups;

	private static final int pg_used_beg_grp =
		th_commit_end_grp + heap_size_re_groups +
		pargc_perm_gen_name_re_groups;
	private static final int pg_used_end_grp =
		pg_used_beg_grp + heap_size_re_groups;
	private static final int pg_commit_end_grp =
		pg_used_end_grp + heap_size_re_groups;

	private static final int gc_time_grp =
		pg_commit_end_grp + heap_size_re_groups;
}
