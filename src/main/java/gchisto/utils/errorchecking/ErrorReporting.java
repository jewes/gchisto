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
package gchisto.utils.errorchecking;

/**
 * A convenience class that includes methods that check and/or report fatal
 * errors and warnings. All methods are static and this class cannot be
 * instantiated.
 *
 * @author Tony Printezis
 */
public class ErrorReporting {
    
    /**
     * If it is true, then warnings are reported.
     *
     * @see #setShowWarnings(boolean)
     */
    static private boolean showWarnings = false;
    
    /**
     * It unconditionally throws a <tt>FatalErrorException</tt> with the 
     * given message.
     *
     * @param str The message to be attached to the exception to be thrown.
     * @exception FatalErrorException The exception that is unconditionally
     * thrown.
     *
     * @see #fatalError(boolean, String)
     */
    static public void fatalError(String str) {
        fatalError(false, str);
    }
    
    /**
     * It throws a <tt>FatalErrorException</tt> with the given message if
     * the given assertion does not hold.
     *
     * @param assertion The assertion to be checked.
     * @param str The message to be attached to the exception to be thrown.
     * @exception FatalErrorException The exception that is conditionally
     * thrown.
     *
     * @see #fatalError(String)
     */
    static public void fatalError(boolean assertion, String str) {
        if (!assertion) {
            throw new FatalErrorException(str);
        }
    }
    
    /**
     * It sets whether warnings will be shown or not.
     *
     * @param showWarnings It determines whether warnings will be shown or not.
     * @see #warning(String)
     * @see #warning(boolean, String)
     */
    static public void setShowWarnings(boolean showWarnings) {
        ErrorReporting.showWarnings = showWarnings;
    }

    /**
     * It unconditionally shows a warning with the given message, as long 
     * as warnings are being shown.
     *
     * @param str The message of the warning to be shown.
     *
     * @see #setShowWarnings(boolean)
     * @see #warning(boolean, String)
     */
    static public void warning(String str) {
        warning(false, str);
    }
    
    /**
     * It shows a warning with the given message if the given assertion 
     * does not hold and as long as warnings are being shown.
     *
     * @param assertion The assertion to be checked.
     * @param str The message of the warning to be shown.
     *
     * @see #setShowWarnings(boolean)
     * @see #warning(String)
     */
    static public void warning(boolean assertion, String str) {
        if (showWarnings && !assertion) {
            System.out.println("warning : " + str);
        }
    }
    
    /**
     * Private constructor to avoid the instantiation of this class.
     */
    private ErrorReporting() {
    }
    
}
