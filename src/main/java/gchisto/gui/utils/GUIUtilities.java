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
import java.awt.Component;
import java.awt.Font;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * A convenience class that includes methods the generate Swing components
 * with a uniform look, or set specific attributes of Swing components.
 * All methods are static and this class cannot be instantiated.
 *
 * @author Tony Printezis
 */
public class GUIUtilities {
    
    /**
     * The name of the font that will be used by methods in this class that
     * set the font of components.
     *
     * @see #setPlain(Component)
     * @see #setBold(Component)
     * @see #setItalic(Component)
     * @see #setBoldItalic(Component)
     */
    static final private String FONT_NAME = "Dialog";
    
    /**
     * The foreground color for the table components.
     *
     * @see #createJLabelForTable()
     * @see #createJLabelForTable(String)
     * @see #createJCheckBoxForTable(boolean)
     */
    static final public Color TABLE_COMPONENT_FG_COLOR = Color.BLACK;
    
    /**
     * The background color for the table components.
     *
     * @see #createJLabelForTable()
     * @see #createJLabelForTable(String)
     * @see #createJCheckBoxForTable(boolean)
     */
    static final public Color TABLE_COMPONENT_BG_COLOR = Color.WHITE;
    
    /**
     * The background color for the table header components (labels,
     * typically).
     * 
     * @see #setTableHeader(Component)
     */
    static final private Color TABLE_HEADER_BG_COLOR = new Color(238, 238, 238);
    
    /**
     * It creates a new label for inclusion in a table and sets the text
     * of the label to the given string.
     * 
     * @param str The text on the label to be created.
     * @return A new label for inclusion in a table.
     *
     * @see #createJLabelForTable(String)
     */
    static public JLabel createJLabelForTable(String str) {
        ArgumentChecking.notNull(str, "str");
        
        JLabel label = createJLabelForTable();
        label.setText(str);
        return label;
    }
    
    /**
     * It creates a new label for inclusion in a table.
     * 
     * @return A new label for inclusion in a table.
     *
     * @see #createJLabelForTable()
     */
    static public JLabel createJLabelForTable() {
        JLabel label = new JLabel();
        setPlain(label);
        label.setOpaque(true);
        label.setForeground(TABLE_COMPONENT_FG_COLOR);
        label.setBackground(TABLE_COMPONENT_BG_COLOR);
        return label;
    }
    
    /**
     * It creates a new check box for inclusion in a table and sets 
     * its "is selected" attribute to the given value.
     * 
     * @param selected It determines whether the newly-created check box
     * will be selected or not.
     * @return A new check box for inclusion in a table.
     */
    static public JCheckBox createJCheckBoxForTable(boolean selected) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setOpaque(true);
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);
        checkBox.setForeground(TABLE_COMPONENT_FG_COLOR);
        checkBox.setBackground(TABLE_COMPONENT_BG_COLOR);
        checkBox.setSelected(selected);
        return checkBox;
    }
    
    /**
     * It sets a component, typically a label, to have the look of a table
     * header.
     *
     * @param component The component whose look will be updated.
     */
    static public void setTableHeader(Component component) {
        ArgumentChecking.notNull(component, "component");
        
        setBold(component);
        component.setForeground(TABLE_COMPONENT_FG_COLOR);
        component.setBackground(TABLE_HEADER_BG_COLOR);
    }
    
    /**
     * It sets the font of the given component to the "Dialog" series and
     * with the plain style.
     *
     * @param component The component whose font will be set.
     *
     * @see #setBold(Component)
     * @see #setItalic(Component)
     * @see #setBoldItalic(Component)
     */
    static public void setPlain(Component component) {
        ArgumentChecking.notNull(component, "component");
        
        Font font = component.getFont();
        int style = Font.PLAIN;
        int size = font.getSize();
        Font newFont = new Font(FONT_NAME, style, size);
        component.setFont(newFont);
    }
    
    /**
     * It sets the font of the given component to the "Dialog" series and
     * with the bold style.
     *
     * @param component The component whose font will be set.
     *
     * @see #setPlain(Component)
     * @see #setItalic(Component)
     * @see #setBoldItalic(Component)
     */
    static public void setBold(Component component) {
        ArgumentChecking.notNull(component, "component");
        
        Font font = component.getFont();
        int style = Font.BOLD;
        int size = font.getSize();
        Font newFont = new Font(FONT_NAME, style, size);
        component.setFont(newFont);
    }
    
    /**
     * It sets the font of the given component to the "Dialog" series and
     * with the italic style.
     *
     * @param component The component whose font will be set.
     *
     * @see #setPlain(Component)
     * @see #setBold(Component)
     * @see #setBoldItalic(Component)
     */
    static public void setItalic(Component component) {
        ArgumentChecking.notNull(component, "component");
        
        Font font = component.getFont();
        int style = Font.ITALIC;
        int size = font.getSize();
        Font newFont = new Font(FONT_NAME, style, size);
        component.setFont(newFont);
    }
    
    /**
     * It sets the font of the given component to the "Dialog" series and
     * with the bold/italic style.
     *
     * @param component The component whose font will be set.
     *
     * @see #setPlain(Component)
     * @see #setBold(Component)
     * @see #setItalic(Component)
     */
    static public void setBoldItalic(Component component) {
        ArgumentChecking.notNull(component, "component");
        
        Font font = component.getFont();
        int style = Font.ITALIC | Font.BOLD;
        int size = font.getSize();
        Font newFont = new Font(FONT_NAME, style, size);
        component.setFont(newFont);
    }
    
    /**
     * Private constructor to avoid the instantiation of this class.
     */
    private GUIUtilities() {
    }
}
