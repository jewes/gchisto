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
package gchisto.gui.panels;

import gchisto.gui.*;
import java.io.File;

/**
 * The interface that the trace management panel has to implement. This is
 * a special panel that does all the GC trace set manipulation (GC trace
 * loading, removal, updating, etc.).
 *
 * @author Tony Printezis
 * @see    gchisto.gui.MainFrame
 */
public interface TraceManagementPanel extends VisualizationPanel {
    
    /**
     * Load a GC trace after prompting the user for its file name.
     *
     * TODO
     */
    public void addGCTrace();
    
    /**
     * TODO
     */
    public void addGCTrace(File file);
    
}
