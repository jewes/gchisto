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

import javax.swing.JTextField;

/**
 *
 * @author  tony
 */
public class PlaybackFrame extends javax.swing.JFrame {
    
    static private final int DEFAULT_SPEED = 100;
    static private final int MIN_SPEED = 10;
    static private final int MAX_SPEED = 100000;
    
    static private final int DEFAULT_EVENT_NUM = 10;
    static private final int MIN_EVENT_NUM = 1;
    static private final int MAX_EVENT_NUM = 10000;
    
    static private final double DEFAULT_DURATION_SEC = 1.0;
    static private final double MIN_DURATION_SEC     = 0.01;
    static private final double MAX_DURATION_SEC     = 60.0;
    
    private int speed;
    private int eventNum;
    private double durationSec;
    
    private DynamicFileGCTrace gcTrace;
    
    private double validateTextField(JTextField textField,
            double def, double min, double max) {
        double value = def;
        String text = textField.getText();
        if (!text.equals("") && !text.equals(" ")) {
            try {
                value = Double.parseDouble(text);
                if (value < min) {
                    value = min;
                }
                if (value > max) {
                    value = max;
                }
            } catch (NumberFormatException e) {
            }
        }
        textField.setText(String.format("%1.2f", value));
        return value;
    }
    
    private int validateTextField(JTextField textField,
            int def, int min, int max) {
        int value = def;
        String text = textField.getText();
        if (!text.equals("") && !text.equals(" ")) {
            try {
                value = Integer.parseInt(text);
                if (value < min) {
                    value = min;
                }
                if (value > max) {
                    value = max;
                }
            } catch (NumberFormatException e) {
            }
        }
        textField.setText(String.format("%d", value));
        return value;
    }
    
    private void enableReal() {
        realCheckBox.setSelected(true);
        enableReal(true);
        
        fastCheckBox.setSelected(false);
        enableFast(false);
        
        validateAllTextFields();
    }
    
    private void enableFast() {
        realCheckBox.setSelected(false);
        enableReal(false);
        
        fastCheckBox.setSelected(true);
        enableFast(true);
        
        validateAllTextFields();
    }
    
    private int validateSpeed() {
        return validateTextField(speedTextField,
                DEFAULT_SPEED, MIN_SPEED, MAX_SPEED);
    }
    
    private int validateEventNum() {
        return validateTextField(eventNumTextField,
                DEFAULT_EVENT_NUM, MIN_EVENT_NUM, MAX_EVENT_NUM);
    }
    
    private double validateDurationSec() {
        return validateTextField(durationTextField,
                DEFAULT_DURATION_SEC, MIN_DURATION_SEC, MAX_DURATION_SEC);
    }
    
    private void validateAllTextFields() {
        speed = validateSpeed();
        eventNum = validateEventNum();
        durationSec = validateDurationSec();
    }
    
    private void enableReal(boolean b) {
        speedTextField.setEnabled(b);
    }
    
    private void enableFast(boolean b) {
        eventNumTextField.setEnabled(b);
        durationTextField.setEnabled(b);
    }
    
    private void enableAllComponents(boolean b) {
        realCheckBox.setEnabled(b);
        enableReal(b);
        
        fastCheckBox.setEnabled(b);
        enableFast(b);
    }
    
    private void startPlaying() {
        validateAllTextFields();

        if (realCheckBox.isSelected()) {
            assert !fastCheckBox.isSelected();
            gcTrace.playReal(speed);
        } else {
            assert fastCheckBox.isSelected();
            gcTrace.playFast(eventNum, durationSec);
        }
    }
    
    void setStatus(String statusText) {
        statusLabel.setText(statusText);
    }
    
    void setPlaying() {
        assert gcTrace.playing();
        assert !gcTrace.paused();
        
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        closeButton.setEnabled(false);
    }
    
    void setPaused() {
        assert gcTrace.playing();
        assert gcTrace.paused();
        
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(true);        
    }
    
    void setStopped() {
        assert !gcTrace.playing();
        assert !gcTrace.paused();
        
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false); 
        closeButton.setEnabled(true);
    }
    
    private String truncateTitle(String str) {
        int length = str.length();
        if (length < 50) {
            return str;
        } else {
            return str.substring(0, 20) +
                    " ... " +
                    str.substring(length - 20, length);
        }
    }
    
    public void dispose() {
        gcTrace = null;
        super.dispose();
    }
    
    public PlaybackFrame(DynamicFileGCTrace gcTrace) {
        initComponents();
        
        this.gcTrace = gcTrace;
        setTitle("Trace File Playback");
        titleLabel.setText(truncateTitle(gcTrace.getFile().getAbsolutePath()));
        validateAllTextFields();
        enableFast();
        pack();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playButton = new javax.swing.JButton();
        realPanel = new javax.swing.JPanel();
        realCheckBox = new javax.swing.JCheckBox();
        speedLabel = new javax.swing.JLabel();
        speedTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        fastPanel = new javax.swing.JPanel();
        fastCheckBox = new javax.swing.JCheckBox();
        eventNumTextField = new javax.swing.JTextField();
        eventsPerLabel = new javax.swing.JLabel();
        durationTextField = new javax.swing.JTextField();
        secondsLabel = new javax.swing.JLabel();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        statusPanel = new javax.swing.JPanel();
        statusTitleLabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        pauseButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        playButton.setText("Play");
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });

        realPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        realCheckBox.setText("Real Playback");
        realCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        realCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                realCheckBoxActionPerformed(evt);
            }
        });

        speedLabel.setText("Speed");

        speedTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel1.setText("%");

        org.jdesktop.layout.GroupLayout realPanelLayout = new org.jdesktop.layout.GroupLayout(realPanel);
        realPanel.setLayout(realPanelLayout);
        realPanelLayout.setHorizontalGroup(
            realPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(realPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(realPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(realPanelLayout.createSequentialGroup()
                        .add(speedLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(speedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 74, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel1))
                    .add(realCheckBox))
                .addContainerGap(147, Short.MAX_VALUE))
        );
        realPanelLayout.setVerticalGroup(
            realPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(realPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(realCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(realPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(speedLabel)
                    .add(jLabel1)
                    .add(speedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        fastPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        fastCheckBox.setText("Fast Playback");
        fastCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fastCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fastCheckBoxActionPerformed(evt);
            }
        });

        eventNumTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        eventNumTextField.setText(" ");

        eventsPerLabel.setText("Events Per");

        durationTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        durationTextField.setText(" ");

        secondsLabel.setText("Seconds");

        org.jdesktop.layout.GroupLayout fastPanelLayout = new org.jdesktop.layout.GroupLayout(fastPanel);
        fastPanel.setLayout(fastPanelLayout);
        fastPanelLayout.setHorizontalGroup(
            fastPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fastPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(fastPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, fastPanelLayout.createSequentialGroup()
                        .add(eventNumTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(eventsPerLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(durationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(secondsLabel)
                        .add(21, 21, 21))
                    .add(fastPanelLayout.createSequentialGroup()
                        .add(fastCheckBox)
                        .addContainerGap(189, Short.MAX_VALUE))))
        );
        fastPanelLayout.setVerticalGroup(
            fastPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fastPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(fastCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fastPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(secondsLabel)
                    .add(durationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(eventsPerLabel)
                    .add(eventNumTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        titlePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        titleLabel.setText(" ");

        org.jdesktop.layout.GroupLayout titlePanelLayout = new org.jdesktop.layout.GroupLayout(titlePanel);
        titlePanel.setLayout(titlePanelLayout);
        titlePanelLayout.setHorizontalGroup(
            titlePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(titlePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(titleLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );
        titlePanelLayout.setVerticalGroup(
            titlePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(titlePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(titleLabel)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        statusPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        statusTitleLabel.setText("Status:");

        statusLabel.setText(" ");

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusTitleLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statusTitleLabel)
                    .add(statusLabel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pauseButton.setText("Pause");
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed(evt);
            }
        });

        stopButton.setText("Stop");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(statusPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, fastPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(realPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(titlePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(playButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pauseButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(stopButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(closeButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(titlePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(realPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fastPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(playButton)
                    .add(pauseButton)
                    .add(stopButton)
                    .add(closeButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
// TODO add your handling code here:
        if (!gcTrace.paused()) {
            enableAllComponents(false);
            startPlaying();
        } else {
            gcTrace.unpause();
        }
}//GEN-LAST:event_playButtonActionPerformed
    
    private void fastCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fastCheckBoxActionPerformed
// TODO add your handling code here:
        if (fastCheckBox.isSelected()) {
            enableFast();
        } else {
            enableReal();
        }
    }//GEN-LAST:event_fastCheckBoxActionPerformed
    
    private void realCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_realCheckBoxActionPerformed
// TODO add your handling code here:
        if (realCheckBox.isSelected()) {
            enableReal();
        } else {
            enableFast();
        }
    }//GEN-LAST:event_realCheckBoxActionPerformed

    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseButtonActionPerformed
        // TODO add your handling code here:
        assert !gcTrace.paused();

        gcTrace.shouldPause();
}//GEN-LAST:event_pauseButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        // TODO add your handling code here:
        assert gcTrace.playing();
        
        gcTrace.shouldFinish();
}//GEN-LAST:event_stopButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        // TODO add your handling code here:
        assert !gcTrace.playing();
        
        setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JTextField durationTextField;
    private javax.swing.JTextField eventNumTextField;
    private javax.swing.JLabel eventsPerLabel;
    private javax.swing.JCheckBox fastCheckBox;
    private javax.swing.JPanel fastPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton pauseButton;
    private javax.swing.JButton playButton;
    private javax.swing.JCheckBox realCheckBox;
    private javax.swing.JPanel realPanel;
    private javax.swing.JLabel secondsLabel;
    private javax.swing.JLabel speedLabel;
    private javax.swing.JTextField speedTextField;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JLabel statusTitleLabel;
    private javax.swing.JButton stopButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
    
}
