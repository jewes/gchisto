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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tony
 */
public class AbstractDatasetWithGroups extends AbstractChangingDataset
        implements DatasetWithGroups {

    /**
     * A list that contains the names of all the GC activites in all the
     * loaded traces, as well as the aggregate GC activity.
     */
    final private List<String> groupNames = new ArrayList<String>();
    /**
     * An array with one entry per GC activity in the GC trace that
     * dictates whether that GC activity is groupActive (i.e., whether
     * it will be displayed in the chart).
     */
    final private List<Boolean> groupActive = new ArrayList<Boolean>();
    
    private int groupNum;

    public int getGroupCount() {
        return groupNum;
    }

    public String getGroupName(int group) {
        assert 0 <= group && group < groupNum;

        return groupNames.get(group);
    }

    protected int indexOfGroupName(String groupName) {
        return groupNames.indexOf(groupName);
    }
    
    public boolean isGroupActive(int group) {
        assert 0 <= group && group < groupNum;

        return groupActive.get(group);
    }

    public void setGroupActive(int group, boolean active) {
        assert 0 <= group && group < groupNames.size();

        groupActive.set(group, active);
        datasetChanged();
    }
    
    synchronized public void addGroup(int id, String groupName) {
        groupNames.add(id, groupName);
        groupActive.add(id, true);
        groupNum += 1;
        
        assert groupNames.size() == groupNum;
        assert groupActive.size() == groupNum;
    }
    
    public AbstractDatasetWithGroups() {
        groupNum = 0;
    }
    
    public AbstractDatasetWithGroups(String[] groupNames) {
        this();
        for (int i = 0; i < groupNames.length; ++i) {
            addGroup(i, groupNames[i]);
        }
    }
}
