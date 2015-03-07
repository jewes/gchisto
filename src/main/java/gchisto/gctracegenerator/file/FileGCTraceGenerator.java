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
package gchisto.gctracegenerator.file;

import gchisto.gctracegenerator.GCTraceGeneratorForFiles;
import gchisto.gctracegenerator.GCTraceGeneratorListener;
import gchisto.utils.errorchecking.ArgumentChecking;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

/**
 *
 * @author tony
 */
abstract public class FileGCTraceGenerator implements GCTraceGeneratorForFiles {

    static private File currDir = new File(".");
    
    protected File getFileFromDialog(JComponent component) {
        assert component != null;
        
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(currDir);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        int ret = chooser.showOpenDialog(component);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            return file;
        } else {
            return null;
        }
    }
    
    abstract protected FileGCTrace newFileGCTrace(File file);
    
    public void createNewGCTrace(File file,
            GCTraceGeneratorListener listener) {
        FileGCTrace gcTrace = newFileGCTrace(file);
        gcTrace.init(listener);
        currDir = file;
    }
    
    public void createNewGCTrace(JComponent component,
            GCTraceGeneratorListener listener) {
        ArgumentChecking.notNull(component, "component");
        
        File file = getFileFromDialog(component);
        if (file != null) {
            createNewGCTrace(file, listener);
        }
    }
    
    public FileGCTraceGenerator() {
    }
    
}
