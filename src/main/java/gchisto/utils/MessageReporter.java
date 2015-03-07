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
package gchisto.utils;

import gchisto.gui.utils.StatusLabelManager;
import gchisto.utils.errorchecking.ArgumentChecking;

/**
 * A class that is used to report messages and errors to the user. All methods
 * are static and this class cannot be instantiated.
 *
 * @author Tony Printezis
 */
public class MessageReporter {
    
    /**
     * The object that moanages the behavior of the status label.
     */
    static private StatusLabelManager statusLabel;

    /**
     * It sets the object that manages the behavior of the status label.
     *
     * @param statusLabel The object that manages the behavior of the status
     * label.
     */
    static public void setStatusLabel(StatusLabelManager statusLabel) {
        ArgumentChecking.notNull(statusLabel, "statusLabel");
        
        MessageReporter.statusLabel = statusLabel;
    }
    
    /**
     * It shows a message in the status label.
     *
     * @param str The message to be shown in the status label.
     */
    static public void showMessage(String str) {
    	if (statusLabel==null) {
    		System.out.println(str);
    		return;
    	}
        ArgumentChecking.notNull(str, "str");
        
        statusLabel.showMessage(str);
    }
    
    /**
     * It shows an error in the status label.
     *
     * @param str The error to be shown in the status label.
     */
    static public void showError(String str) {
    	if (statusLabel==null) {
    		System.out.println(str);
    		return;
    	}
        ArgumentChecking.notNull(str, "str");
        
        statusLabel.showError(str);
    }
    
    /**
     * Private constructor to avoid the instantiation of this class.
     */
    private MessageReporter() {
    }
    
}
