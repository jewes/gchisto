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
import java.util.HashMap;

public enum GCMetric
{
	/* gc pauses */
	ygc_time	(1),
	fgc_time	(1),
	cms_im_time	(1),
	cms_rm_time	(1),
	tgc_time	(1),

	/* concurrent gc activity */
	cms_cm_a_time	(1),	/* cms concurrent mark     'active'  time  */
	cms_cm_e_time	(2),	/* cms concurrent mark     'elapsed' time  */
	cms_cp_a_time	(1),	/* cms concurrent preclean 'active'  time  */
	cms_cp_e_time	(2),	/* cms concurrent preclean 'elapsed' time  */
	cms_cs_a_time	(1),	/* cms concurrent sweep    'active'  time  */
	cms_cs_e_time	(2),	/* cms concurrent sweep    'elapsed' time  */
	cms_cr_a_time	(1),	/* cms concurrent reset    'active'  time  */
	cms_cr_e_time	(2),	/* cms concurrent reset    'elapsed' time  */

	/* heap sizes */
	yg_used_beg	(1),	/* young gen sizes */
	yg_used_end	(2),
	yg_commit_beg	(2),
	yg_commit_end	(2),
	og_used_beg	(1),	/* old gen sizes */
	og_used_end	(2),
	og_commit_beg	(2),
	og_commit_end	(2),
	pg_used_beg	(1),	/* perm gen sizes */
	pg_used_end	(2),
	pg_commit_beg	(2),
	pg_commit_end	(2),
	th_used_beg	(1),	/* total heap sizes */
	th_used_end	(2),
	th_commit_beg	(2),
	th_commit_end	(2),

/*
Need good, terse convention for naming rates:

r_cpu		rate/sec of cpu time available
r_ela		rate/sec of elapsed time
r_mut		rate/sec of cpu time available to mutators
r_ygc		rate/sec of young gc time

Current name field is 13 chars.  th_alloc_r_cpu is 14.

*/

	th_alloc	(1),	/* allocated */
	th_alloc_ela    (2),	/* allocated/second of elapsed time */
	th_alloc_cpu    (1),	/* allocated/second of cpu time */
	th_alloc_mut    (2),	/* allocated/second of mutator cpu time */
	yg_promo	(1),	/* promoted */
	yg_promo_ela    (2),	/* promoted/second of elapsed time */
	yg_promo_ygc    (2),	/* promoted/second of young gc time */

	/* parallel compaction phases */
	pc_pre_comp	(1),
	pc_par_mark	(1),
	pc_mark_flush	(1),
	pc_ref_proc	(1),
	pc_cls_unload	(1),
	pc_mark		(1),
	pc_summary	(1),
	pc_adj_roots	(1),
	pc_perm_gen	(1),
	pc_drain_ts	(1),
	pc_dpre_ts	(1),
	pc_steal_ts	(1),
	pc_par_comp	(1),
	pc_deferred	(1),
	pc_compact	(1),
	pc_post_comp	(1);

	GCMetric(int timestamp_type, String long_name)
	{
		_timestamp_type = timestamp_type;
		_long_name = long_name;
	}

	GCMetric(int timestamp_type)
	{
		this(timestamp_type, null);
	}

	public int timestamp_type() { return _timestamp_type; }
	public String long_name() { return _long_name; }

	public static GCMetric metric(String name)
	{
		return _name_map.get(name);
	}

	public static void list(PrintStream s)
	{
		for (GCMetric metric:  values())
		{
			s.println(metric.name());
		}
	}

	private final int _timestamp_type;
	private final String _long_name;

	private static HashMap<String, GCMetric> _name_map;

	static
	{
		GCMetric metrics[] = values();
		int capacity = (int)(metrics.length / 0.75) + 1;
		_name_map = new HashMap<String, GCMetric>(capacity);
		for (int i = 0; i < metrics.length; ++i)
		{
			GCMetric m = metrics[i];
			_name_map.put(m.toString(), m);
			String s = m.long_name();
			if (s != null) 
			{
				_name_map.put(s, m);
			}
		}
	}
};
