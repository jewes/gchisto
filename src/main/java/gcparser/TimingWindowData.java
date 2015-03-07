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

public class TimingWindowData
{
	public TimingWindowData(int cpu_count) { _cpu_count = cpu_count; }

	public double th_alloc() { return _th_alloc; }

	// The various times that are tracked.
	public double elapsed_time()    { return _end - _beg; }
	public double pause_time()      { return _pause_time; }
	public double concurrent_time() { return _concurrent_time; }
	public double mutator_time()
	{
		return elapsed_time() - _pause_time - _concurrent_time;
	}

	// The various times, scaled by the number of cpus.
	public double elapsed_cpu_time() { return elapsed_time() * _cpu_count; }
	public double concurrent_cpu_time() { return _concurrent_cpu_time; }
	public double pause_cpu_time() { return _pause_time * _cpu_count; }
	public double mutator_cpu_time()
	{
		return (elapsed_time() - _pause_time) * _cpu_count -
			_concurrent_cpu_time;
	}

	public void add_pause_time(double end_timestamp, double t)
	{
		_end = end_timestamp;
		_pause_time += t;
	}

	public void
	add_concurrent_time(double end_timestamp, double t, int thread_cnt)
	{
		_end = end_timestamp;
		_concurrent_time += t;
		_concurrent_cpu_time += t * thread_cnt;
	}

	public void reset(double timestamp, double th_alloc)
	{
		_pause_time = _concurrent_time = _concurrent_cpu_time = 0.0;
		_beg = _end = timestamp;
		_th_alloc = th_alloc;
	}

	private double _th_alloc;

	// Time accumulated over a short window during the run.
	private double _pause_time;
	private double _concurrent_time;
	private double _concurrent_cpu_time;
	private double _beg;
	private double _end;
	private final int _cpu_count;
}
