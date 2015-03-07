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
package gchisto.jfreechart.extensions;

/**
 * An interface that represents a dataset that can be partitioned into
 * groups and each group can be activated / de-activated. Groups can 
 * correpond to series that JFreeChart datasets have. However, they are
 * a more general abstraction to allow different dataset subdivisions.
 * It is up to the implementing class to keep track of whether each group
 * is active or not.
 *
 * @author Tony Printezis
 */
public interface DatasetWithGroups {
    
    /**
     * It returns the number of groups in the dataset.
     *
     * @return The number of groups in the dataset.
     */
    public int getGroupCount();
    
    /**
     * It returns the name of the group with the given index.
     *
     * @param group The index of the group to be returned.
     * @return The name of the group with the givan index.
     */
    public String getGroupName(int group);
    
    /**
     * It determines whether the group with the given index is active.
     *
     * @param group The index of the group to be determined whether it is
     * active.
     * @return Whether the group with the given index is active.
     */
    public boolean isGroupActive(int group);
    
    /**
     * It sets whether the group with the given index is active or not. 
     *
     * @param group The index of the group to be set as active or not active.
     * @param active Whether the group will be set as active or not.
     */
    public void setGroupActive(int group, boolean active);
    
}
