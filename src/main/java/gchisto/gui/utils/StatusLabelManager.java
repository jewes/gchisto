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
package gchisto.gui.utils;

import gchisto.utils.errorchecking.ArgumentChecking;
import java.awt.Color;
import javax.swing.JLabel;

/**
 * A class that manages the behavior of a <tt>JLabel</tt>. It allows the
 * the label to show messages or errors, each of which update the label
 * with different colors. The text, opacity, and colors are then reverted
 * to the original values after some time has elapsed.
 *
 * @author Tony Printezis
 * @see    javax.swing.JLabel
 */
public class StatusLabelManager {
    
    /**
     * A class that implements the deamon loop that monitors the updates
     * to the label and resets, when necessary, the label.
     */
    public class Deamon implements Runnable {
        
        /**
         * The time period between the last time the label text was set
         * and the label is cleared.
         *
         * @see #run()
         */
        static final private long CLEAR_AFTER_TIME_MS =  7 * 1000;
        
        /**
         * The period that the deamon thread sleeps between checking
         * whether it is time to clear the label.
         *
         * @see #run()
         */
        static final private long SLEEP_PERIOD_MS     =  1 * 1000 + 50;
        
        /**
         * The monitor through which the deamon is notified that the time
         * stamp has been modified.
         */
        final private Object monitor;
        
        /**
         * The deamon loop that monitors the updates to the label and resets,
         * when necessary, the label.
         */
        public void run() {
            while (true) {
                long oldTimeStamp = timeStampMS;
                synchronized(monitor) {
                    while (oldTimeStamp == timeStampMS) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
                
                long currTimeStamp = timeStampMS;
                while (currTimeStamp < timeStampMS + CLEAR_AFTER_TIME_MS) {
                    try {
                        Thread.sleep(SLEEP_PERIOD_MS);
                    } catch (InterruptedException e) {
                    }
                    currTimeStamp = System.currentTimeMillis();
                }
                
                resetLabel();
            }
        }
        
        /**
         * Creates a new instance of the deamon.
         *
         * @param monitor The monitor through which the deamon is notified
         * that the time stamp has been modified.
         */
        public Deamon(Object monitor) {
            this.monitor = monitor;
        }
        
    }
    
    /**
     * The foreground of the label when a message is shown.
     *
     * @see #showMessage(String)
     */
    static final private Color MESSAGE_FG_COLOR = Color.DARK_GRAY;
    
    /**
     * The background of the label when a message is shown.
     *
     * @see #showMessage(String)
     */
    static final private Color MESSAGE_BG_COLOR = Color.LIGHT_GRAY;
    
    /**
     * The foreground of the label when an error is shown.
     *
     * @see #showError(String)
     */
    static final private Color ERROR_FG_COLOR = Color.WHITE;
    
    /**
     * The background of the label when an error is shown.
     *
     * @see #showError(String)
     */
    static final private Color ERROR_BG_COLOR = Color.RED;
    
    /**
     * The label managed by this object.
     */
    final private JLabel statusLabel;
    
    /**
     * The original text of the label.
     */
    final private String originalText;
    
    /**
     * The original opacity of the label.
     */
    final private boolean originalIsOpaque;
    
    /**
     * The original foreground color of the label.
     */
    final private Color originalFGColor;
    
    /**
     * The original background color of the label.
     */
    final private Color originalBGColor;
    
    /**
     * The time stamp, in ms, when the label was last set.
     */
    volatile private long timeStampMS;
    
    /**
     * It brings the time stamp up-to-date.
     */
    private void updateTimeStamp() {
        timeStampMS = System.currentTimeMillis();
    }
    
    /**
     * It updates the contents and look of the label.
     *
     * @param str The new text of the label.
     * @param opaque Whether the label will be opaque.
     * @param fgColor The foreground color of the label.
     * @param bgColor The background color of the label.
     */
    synchronized private void updateLabel(
            String str,
            boolean opaque,
            Color fgColor,
            Color bgColor) {
        assert str != null;
        assert fgColor != null;
        assert bgColor != null;
        
        statusLabel.setText(str);
        statusLabel.setOpaque(opaque);
        statusLabel.setForeground(fgColor);
        statusLabel.setBackground(bgColor);
        
        updateTimeStamp();
        notifyAll();
    }
    
    /**
     * It shows a message on the status label. The look of the status label
     * is updated accordingly.
     *
     * @param str The message to be shown.
     */
    public void showMessage(String str) {
        ArgumentChecking.notNull(str, "str");
        
        updateLabel(str, false, MESSAGE_FG_COLOR, MESSAGE_BG_COLOR);
    }
    
    /**
     * It shows an error on the status label. The look of the status label
     * is updated accordingly.
     *
     * @param str The error to be shown.
     */
    public void showError(String str) {
        ArgumentChecking.notNull(str, "str");
        
        updateLabel("ERROR: " + str, true, ERROR_FG_COLOR, ERROR_BG_COLOR);
    }
    
    /**
     * It resets the contents and look of the label to its original state.
     */
    public void resetLabel() {
        updateLabel(originalText, originalIsOpaque,
                originalFGColor, originalBGColor);
    }
    
    /**
     * It creates a new status label manager instance.
     *
     * @param statusLabel The label to be managed.
     */
    public StatusLabelManager(JLabel statusLabel) {
        ArgumentChecking.notNull(statusLabel, "statusLabel");
        
        this.statusLabel = statusLabel;
        this.originalText = statusLabel.getText();
        this.originalIsOpaque = statusLabel.isOpaque();
        this.originalFGColor = statusLabel.getForeground();
        this.originalBGColor = statusLabel.getBackground();
        updateTimeStamp();
        
        new Thread(new Deamon(this)).start();
    }
}
