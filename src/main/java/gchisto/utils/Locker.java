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

import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author tony
 */
public class Locker {

    final private ReentrantLock lock = new ReentrantLock();
    private int count = 0;

    protected void lock() {
        lock.lock();
        assert count >= 0;
        count += 1;
    }

    protected void unlock() {
        assert lock.isHeldByCurrentThread();
        
        assert count > 0;
        count -= 1;
        lock.unlock();
    }

    public boolean isLockedByCurrentThread() {
        return lock.isHeldByCurrentThread();
    }

    public void doWhileLocked(Runnable runnable) {
        lock();
        try {
            runnable.run();
        } finally {
            unlock();
        }
    }

}
