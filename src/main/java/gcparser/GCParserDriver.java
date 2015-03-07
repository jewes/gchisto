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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import java.util.regex.*;
import java.util.zip.GZIPInputStream;

public class GCParserDriver
{
	public static final int TERSE			= 0x01;
	public static final int VERBOSE			= 0x02;
	public static final int COLLECT_DATA		= 0x03;
	public static final int COMPARE_STATISTICS	= 0x04;
	public static final int PRINT_STATISTICS	= 0x05;
	public static final int SAVE_DATA		= 0x06;

	public static void main(String argv[]) throws IOException
	{
		GCParserDriver driver = new GCParserDriver(argv);
		driver.run(argv, driver.next_arg());
	}

	public
	GCParserDriver(BitSet actions, EnumMap<GCMetric, Boolean> enabled_map,
		String prefix, String suffix, int cpu_count)
	{
		boolean verbose = actions.get(VERBOSE);
		_actions = create_actions(actions);
		_enabled_map = enabled_map;
		_prefix = prefix;
		_suffix = suffix;
		_cpu_count = cpu_count;
		_next_arg = 0;
		_has_time_zero = true;
		_gc_stats = create_gc_stats(_actions, enabled_map, _cpu_count,
			_has_time_zero);
		_gc_parsers = create_gc_parsers(_gc_stats, verbose);
	}

	public GCParserDriver(BitSet actions)
	{
		this(actions, null, null, ".dat", 1);
	}

	public GCParserDriver(String argv[], int index)
	{
		boolean verbose = false;
		boolean enable_value = false;
		String enable_list = null;

		_actions = new BitSet();
		_suffix = ".dat";
		_cpu_count = 1;
		_has_time_zero = true;

		int i;
		final int n = argv.length;
		boolean matched = true;
		for (i = index; i < n && matched; ++i)
		{
			// -c 		# compare statistics
			// -d name ...	# disable metrics
			// -e name ...	# enable metrics
			// -h		# help
			// -l 		# list metrics and exit
			// -n		# number of cpus
			// -o pattern	# output file pattern (use %{metric})
			// -p		# print statistics (the default)
			// -s		# save data
			// -t		# terse
			// -v		# verbose
			// 
			// ???
			// -z		# each file includes time zero
			// +z		# each file does not include time zero

			String s = argv[i];
			if (s == null) { /* empty */ }
			else if (s.equals("-c") ||
				s.equals("--compare") ||
				s.equals("--comparestats"))
			{
				_actions.set(COMPARE_STATISTICS);
			}
			else if (i + 1 < n &&
				(s.equals("-d") ||
				s.equals("--disable") ||
				s.equals("--disablemetrics")))
			{
				enable_list = argv[++i];
				enable_value = false;
			}
			else if (i + 1 < n &&
				(s.equals("-e") ||
				s.equals("--enable") ||
				s.equals("--enablemetrics")))
			{
				enable_list = argv[++i];
				enable_value = true;
			}
			else if (s.equals("-h") ||
				s.equals("--help"))
			{
				help(System.out);
				System.exit(0);
			}
			else if (s.equals("-l") ||
				s.equals("--list") ||
				s.equals("--listmetrics"))
			{
				list_metrics(System.out);
				System.exit(0);
			}
			else if (i + 1 < n && (s.equals("-n") ||
				s.equals("--cpu") ||
				s.equals("--cpucount")))
			{
				_cpu_count = Integer.parseInt(argv[++i]);
			}
			else if (s.equals("-p") ||
				s.equals("--print") ||
				s.equals("--printstats"))
			{
				_actions.set(PRINT_STATISTICS);
			}
			else if (i + 1 < n && (s.equals("-o") ||
				s.equals("--ofile") ||
				s.equals("--outputfilepattern")))
			{
				parse_output_file_pattern(argv[++i]);
				// This option implies -s.
				_actions.set(COLLECT_DATA);
				_actions.set(SAVE_DATA);
			}
			else if (s.equals("-s") ||
				s.equals("--save") ||
				s.equals("--savedata"))
			{
				_actions.set(COLLECT_DATA);
				_actions.set(SAVE_DATA);
			}
			else if (s.equals("-t") || s.equals("--terse"))
			{
				_actions.set(TERSE);
			}
			else if (s.equals("-v") || s.equals("--verbose"))
			{
				_actions.set(VERBOSE);
				verbose = true;
			}
			else if (s.equals("-z") || s.equals("--time-zero"))
			{
				_has_time_zero = true;
			}
			else if (s.equals("+z") || s.equals("--no-time-zero"))
			{
				_has_time_zero = false;
			}
			else if (s.equals("--"))
			{
				/* empty */
				matched = false;
			}
			else if (s.startsWith("-") || s.startsWith("+"))
			{
				usage(System.err, s);
				System.exit(2);
			}
			else
			{
				matched = false;
				--i;
			}
		}
		_next_arg = i;

		if (_actions.isEmpty()) _actions.set(PRINT_STATISTICS);

		ArrayList<String> unknown = new ArrayList<String>();
		_enabled_map = create_enabled_map(enable_list, enable_value,
			unknown);
		if (unknown.size() != 0)
		{
			usage(System.err, unknown);
			System.exit(2);
		}

		_gc_stats = create_gc_stats(_actions, _enabled_map, _cpu_count,
			_has_time_zero);
		_gc_parsers = create_gc_parsers(_gc_stats, verbose);
	}

	public GCParserDriver(String argv[])
	{
		this(argv, 0);
	}

	public GCStats gc_stats() { return _gc_stats; }
	public BitSet actions() { return _actions; }

	public boolean should_collect()
	{
		return _actions.get(COLLECT_DATA);
	}

	public boolean should_compare()
	{
		return _actions.get(COMPARE_STATISTICS);
	}

	public boolean should_print()
	{
		return _actions.get(PRINT_STATISTICS);
	}

	public boolean should_save()
	{
		return _actions.get(SAVE_DATA);
	}

	public String prefix() { return _prefix; }
	public String suffix() { return _suffix; }

	/**
	 * Compare the files listed in argv starting at argv[index].  The first
	 * file (at argv[index]) is used as the baseline; the remaining files
	 * are compared against it one at a time.
	 *
	 * <p>
	 * If actions() specifies that the statistics should be printed or
	 * saved, those actions are performed first, before the comparisons.
	 * </p>
	 *
	 * <p>
	 * Each file should include gc logging information produced by the
	 * Java HotSpot(TM) VM with the option -XX:+PrintGCDetails.
	 * </p>
	 * 
	 * @param argv	an array of strings, with file names at the end.
	 * @param index	the index of the first file name in argv.
	 */
	public void compare(String argv[], int index)
	throws IOException
	{
		final int driver_cnt = argv.length - index;
		GCParserDriver d[] = new GCParserDriver[driver_cnt];
		for (int i = 0; i < driver_cnt; ++i)
		{
			String new_name = argv[index + i];
			d[i] = new GCParserDriver(_actions, _enabled_map,
				new_name + ".", _suffix, _cpu_count);
			d[i].parse(new File(new_name));
			if (should_print())
			{
				d[i].print_statistics(System.out, new_name);
			}
			if (should_save()) d[i].save_data();
		}

		final boolean terse = _actions.get(TERSE);
		for (int i = 1; i < driver_cnt; ++i)
		{
			compare_statistics(System.out, argv[index], d[0],
				argv[index + i], d[i], terse);
		}
	}

	/**
	 * Parse the files listed in argv, starting at argv[index].  The data
	 * from all the files is combined into a single set of statistics.
	 *
	 * <p>
	 * Each file should include gc logging information produced by the
	 * Java HotSpot(TM) VM with the option -XX:+PrintGCDetails.
	 * </p>
	 * 
	 * @param argv	an array of strings, with file names at the end.
	 * @param index	the index of the first file name in argv.
	 */
	public void parse(String argv[], int index)
	throws IOException
	{
		if (argv.length == index)
		{
			parse(System.in, "-");
		}
		else
		{
			// If no prefix was specified and there is just one file
			// name argument, use the file name as the prefix.
			if (_prefix == null && index + 1 == argv.length)
			{
				_prefix = argv[index] + ".";
			}

			int i = index;
			do
			{
				parse(new File(argv[i]));
			} while(++i < argv.length);
		}

		if (should_print()) print_statistics(System.out);
		if (should_save()) save_data();
	}

	/**
	 * Process the files listed in argv, starting at argv[index].  Depending
	 * on the specified actions(), the files are either parsed and the data
	 * combined into a single set of statistics, or each file is compared.
	 *
	 * <p>
	 * Each file should include gc logging information produced by the
	 * Java HotSpot(TM) VM with the option -XX:+PrintGCDetails.
	 * </p>
	 * 
	 * @param argv	an array of strings, with file names at the end.
	 * @param index	the index of the first file name in argv.
	 */
	public void run(String argv[], int index)
	throws IOException
	{
		if (should_compare() && argv.length > index + 1)
		{
			compare(argv, index);
			return;
		}

		parse(argv, index);
	}

	public boolean
	parse(List<GCParser> parsers, String filename, int line, String s)
	{
		Iterator<GCParser> iterator = parsers.iterator();
		boolean matched = false;
		do {
			matched = iterator.next().parse(filename, line, s);
		} while (!matched && iterator.hasNext());
		return matched;
	}

	public void parse(BufferedReader r, String filename) throws IOException
	{
		int line = 0;
		long matches = 0;
		String s = r.readLine();
		while (s != null)
		{
			++line;
			if (parse(_gc_parsers, filename, line, s))
			{
				++matches;
				boolean should_sort = 
					matches == 128 ||
					matches == 512 ||
					(matches & 0x3ff) == 0;
				if (should_sort)
				{
					sort_gc_parsers(_gc_parsers);
				}
			}
			s = r.readLine();
		}
		_gc_stats.end_of_file();
	}

	public void parse(InputStream is, String filename) throws IOException
	{
		InputStreamReader ir = new InputStreamReader(is);
		parse(new BufferedReader(ir), filename);
	}

	public void parse(File file) throws IOException
	{
		InputStream inputStreamForFile = streamForFile(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStreamForFile));
		parse(reader, file.getName());
		reader.close();
	}
	
	private InputStream streamForFile(File logFile) throws IOException, FileNotFoundException {
		FileInputStream uncompressedFileStream = new FileInputStream(logFile);
		if (logFile.getName().endsWith(".gz")) {
			return new GZIPInputStream(uncompressedFileStream);
		}
		return uncompressedFileStream;
	}

	public static void
	compare_statistics(PrintStream s, String ref_name, GCStats ref_stats,
		String new_name, GCStats new_stats, boolean terse)
	{
		s.println(ref_name + " vs. " + new_name);
		ref_stats.print_comparison(s, new_stats, terse);
	}

	public static void
	compare_statistics(PrintStream s,
		String ref_name, GCParserDriver ref_driver,
		String new_name, GCParserDriver new_driver,
		boolean terse)
	{
		compare_statistics(s, ref_name, ref_driver.gc_stats(),
			new_name, new_driver.gc_stats(), terse);
	}

	public void print_statistics(PrintStream s)
	{
		_gc_stats.print(s);
	}

	public void print_statistics(PrintStream s, String name)
	{
		s.println(name);
		print_statistics(s);
	}

	public void save_data(String prefix, String suffix) throws IOException
	{
		_gc_stats.save(prefix, suffix);
	}

	public void save_data() throws IOException
	{
		save_data(_prefix, _suffix);
	}

	public void list_metrics(PrintStream s)
	{
		if (_actions.get(TERSE))
		{
			GCMetric.list(s);
			return;
		}

		describe_metrics(s);
	}

	public void describe_metrics(PrintStream s)
	{
		ResourceBundle b = ResourceBundle.getBundle("GCMetricHelp");

		if (b.containsKey("intro")) s.println(b.getString("intro"));
		for (GCMetric metric:  GCMetric.values())
		{
			String name = metric.name();
			s.println(name + '\t' + b.getString(name));
		}
		if (b.containsKey("closing")) s.println(b.getString("closing"));
	}

	public void parse_output_file_pattern(String s)
	{
		final int pattern_length = 9;
		int pos = s.indexOf("%{metric}");
		if (pos < 0)
		{
			_prefix = s;
			_suffix = "";
			return;
		}

		_prefix = s.substring(0, pos);
		_suffix = s.substring(pos + pattern_length);
	}

	public static void help(PrintStream s)
	{
		ResourceBundle b;
		b = ResourceBundle.getBundle("Messages");
		String prog_nm = b.getString("usage.program");
		String fmt_str = b.getString("usage.synopsis");

		s.println(MessageFormat.format(fmt_str, prog_nm));
		s.println();
		s.println(b.getString("usage.summary"));
		s.println();
		s.println(b.getString("usage.option.detail"));
	}

	public static void usage(PrintStream s, String option)
	{
		ResourceBundle b = ResourceBundle.getBundle("Messages");
		String prog_nm = b.getString("usage.program");
		String fmt_str = b.getString("usage.option.unknown");
		s.println(MessageFormat.format(fmt_str, prog_nm, option));
	}

	public static void usage(PrintStream s, Collection<String> metrics)
	{
		ResourceBundle b = ResourceBundle.getBundle("Messages");
		String prog_nm = b.getString("usage.program");
		String fmt_str = b.getString("usage.metric.unknown");

		Iterator<String> iter = metrics.iterator();
		while (iter.hasNext())
		{
			String msg = MessageFormat.format(fmt_str, prog_nm,
				iter.next());
			s.println(msg);
		}
	}

	protected int next_arg() { return _next_arg; }

	protected static BitSet create_actions(BitSet actions)
	{
		BitSet b;
		b = actions == null ? new BitSet() : (BitSet) actions.clone();
		if (b.isEmpty())
		{
			b.set(PRINT_STATISTICS);
		}
		return b;
	}

	public static GCMetric[]
	parse_metric_names(String names[], Collection<String> unrecognized)
	{
		if (names == null || names.length == 0) return null;
		int errors = 0;

		ArrayList<GCMetric> metrics;
		metrics = new ArrayList<GCMetric>(names.length);
		for (int i = 0; i < names.length; ++i)
		{
			GCMetric metric = GCMetric.metric(names[i]);
			if (metric != null)
			{
				metrics.add(metric);
			}
			else if (unrecognized != null)
			{
				// Unrecognized name.
				unrecognized.add(names[i]);
			}
		}

		if (metrics.size() == 0) return null;
		return metrics.toArray(new GCMetric[metrics.size()]);
	}

	public static GCMetric[] parse_metric_names(String names[])
	{
		return parse_metric_names(names, null);
	}

	public static GCMetric[]
	parse_metric_names(String list, Collection<String> unrecognized)
	{
		if (list == null) return null;
		return parse_metric_names(list.split("[ \t,:]+"), unrecognized);
	}

	public static GCMetric[] parse_metric_names(String list)
	{
		return parse_metric_names(list, null);
	}

	/**
	 * Creates and returns a map that indicates whether each GCMetric is
	 * enabled or disabled.
	 *
	 * <p>
	 * The enabled status of the remaining metrics (those <b>not</b> listed
	 * in metrics[]) is set to !value.
	 * </p>
	 * 
	 * @param metrics[] an array of metrics, or null.
	 * 
	 * @param value the enabled status stored for each metric in metrics[].
	 *
	 * @returns a map giving the enabled status of each GCMetric, or null.
	 */
	public static EnumMap<GCMetric, Boolean>
	create_enabled_map(GCMetric metrics[], boolean value)
	{
		if (metrics == null) return null;

		EnumMap<GCMetric, Boolean> map;
		map = new EnumMap<GCMetric, Boolean>(GCMetric.class);
		for (GCMetric metric:  GCMetric.values())
		{
			map.put(metric, !value);
		}

		for (int i = 0; i < metrics.length; ++i)
		{
			map.put(metrics[i], value);
		}

		return map;
	}

	public static EnumMap<GCMetric, Boolean>
	create_enabled_map(String metrics, boolean value,
		Collection<String> unrecognized)
	{
		if (metrics == null) return null;
		GCMetric m[] = parse_metric_names(metrics, unrecognized);
		return create_enabled_map(m, value);
	}

	protected GCStats
	create_gc_stats(BitSet actions, EnumMap<GCMetric, Boolean> enabled_map,
		int cpu_count, boolean input_has_time_zero)
	{
		if (should_collect())
		{
			return new GCDataStore(enabled_map, cpu_count,
				input_has_time_zero);
		}
		return new GCStats(enabled_map, cpu_count, input_has_time_zero);
	}

	/**
	 * Create the set of GCParsers.  Subclasses wishing to add a new parser
	 * should override this method.
	 */
	protected ArrayList<GCParser>
	create_gc_parsers(GCStats gc_stats, boolean verbose)
	{
		ArrayList<GCParser> parsers = new ArrayList<GCParser>(7);
		parsers.add(new ParGCYoungGCParser(gc_stats, verbose));
		parsers.add(new FWYoungGCParser(gc_stats, verbose));
		parsers.add(new ParGCFullGCParser(gc_stats, verbose));
		parsers.add(new CMSGCParser(gc_stats, verbose));
		parsers.add(new ParCompactPhaseGCParser(gc_stats, verbose));
		parsers.add(new FWOldGCParser(gc_stats, verbose));
		parsers.add(new FWFullGCParser(gc_stats, verbose));
		parsers.add(new VerboseGCParser(gc_stats, verbose));
		return parsers;
	}

	/**
	 * Sort the GCParsers in descending order by match_count.
	 */
	protected ArrayList<GCParser>
	sort_gc_parsers(ArrayList<GCParser> parsers)
	{
		final int n = parsers.size();

		// Insertion sort.
		for (int i = 1; i < n; ++i)
		{
			GCParser parser_i = parsers.get(i);
			long value = parser_i.match_count();
			int j = i - 1;
			while (j >= 0 && parsers.get(j).match_count() < value)
			{
				parsers.set(j + 1, parsers.get(j));
				--j;
			}
			parsers.set(j + 1, parser_i);

// 			System.out.println("i = " + i + ":");
// 			for (int x = 0; x < n; ++x) {
// 				GCParser px = parsers.get(x);
// 				System.out.println(px + "=" + px.match_count());
// 			}
		}

		// Bubble sort.
// 		for (int i = 0; i < n; ++i)
// 		{
// 			GCParser pi = parsers.get(i);
// 			for (int j = i + 1; j < n; ++j)
// 			{
// 				GCParser pj = parsers.get(j);
// 				if (pj.match_count() > pi.match_count())
// 				{
// 					parsers.set(i, pj);
// 					parsers.set(j, pi);
// 					pi = pj;
// 				}
// 			}
// 			System.out.println("i = " + i + ":");
// 			for (int x = 0; x < n; ++x) {
// 				GCParser px = parsers.get(x);
// 				System.out.println(px + "=" + px.match_count());
// 			}
// 		}

		return parsers;
	}

	private final EnumMap<GCMetric, Boolean> _enabled_map;
	private final GCStats _gc_stats;
	private final int _next_arg;

	private ArrayList<GCParser> _gc_parsers;
	private BitSet _actions;
	private String _prefix;
	private String _suffix;
	private int _cpu_count;
	private boolean _has_time_zero;
}
