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
package gchisto.gctracegenerator;

import gchisto.utils.errorchecking.ErrorReporting;
import java.util.LinkedList;

public class GCTraceGeneratorSet extends LinkedList<GCTraceGenerator> {

    static private final int GCTRACE_GENERATOR_FOR_FILES_INDEX = 0;
    static private final String[] GCTRACE_GENERATOR_CLASS_NAMES = {
        "gchisto.gctracegenerator.file.hotspot.GCTraceGenerator",
        "gchisto.gctracegenerator.file.hotspot.DynamicGCTraceGenerator",
        "gchisto.gctracegenerator.file.simple.GCTraceGenerator",
        "gchisto.gctracegenerator.file.simple.DynamicGCTraceGenerator"
    };
    private GCTraceGeneratorForFiles gcTraceGeneratorForFiles;

    public GCTraceGeneratorForFiles gcTraceGeneratorForFiles() {
        return gcTraceGeneratorForFiles;
    }

    public GCTraceGeneratorSet() {
        for (int i = 0; i < GCTRACE_GENERATOR_CLASS_NAMES.length; ++i) {
            String className = GCTRACE_GENERATOR_CLASS_NAMES[i];
            try {
                Class c = Class.forName(className);
                Object s = c.newInstance();
                GCTraceGenerator gcTraceGenerator = (GCTraceGenerator) s;
                add(gcTraceGenerator);
            } catch (ClassNotFoundException e) {
                ErrorReporting.warning("could not instantiate " + className);
            } catch (InstantiationException e) {
                ErrorReporting.warning("could not instantiate " + className);
            } catch (IllegalAccessException e) {
                ErrorReporting.warning("could not access constructor of " + className);
            } catch (ClassCastException e) {
                ErrorReporting.warning("could not cast " + className + " to GCTraceGenerator");
            }
        }
        ErrorReporting.fatalError(size() > 0,
                "There must be at least one GC trace generator set up");

        try {
            gcTraceGeneratorForFiles =
                    (GCTraceGeneratorForFiles) get(GCTRACE_GENERATOR_FOR_FILES_INDEX);
        } catch (ClassCastException e) {
            ErrorReporting.fatalError("could not cast GC trace generator with index " +
                    GCTRACE_GENERATOR_FOR_FILES_INDEX + " to GCTraceGeneratorForFiles");
        }
        ErrorReporting.fatalError(gcTraceGeneratorForFiles != null,
                "The GC trace generator for files should not be null");
    }
}
