/*
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public Copyright version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public Copyright
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public Copyright version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 *
 */
package gchisto.utils;

/**
 *
 * @author Tony Printezis
 */
public class Copyright {

    static final private String COPYRIGHT_TEXT =
            "Copyright (c) 2007 Sun Microsystems, Inc., 4150 Network Circle, \n" +
            "Santa Clara, California 95054, USA All rights reserved.\n" +
            "\n" +
            "U.S. Government Rights - Commercial software. Government users are\n" +
            "subject to the Sun Microsystems, Inc. standard license agreement and\n" +
            "applicable provisions of the FAR and its supplements.\n" +
            "\n" +
            "Use is subject to license terms.\n" +
            "\n" +
            "Sun, Sun Microsystems, the Sun logo, Java and NetBeans are trademarks\n" +
            "or registered trademarks of Sun Microsystems, Inc. in the U.S. and\n" +
            "other countries.\n" +
            "\n" +
            "This product is covered and controlled by U.S. Export Control laws and\n" +
            "may be subject to the export or import laws in other\n" +
            "countries. Nuclear, missile, chemical biological weapons or nuclear\n" +
            "maritime end uses or end users, whether direct or indirect, are\n" +
            "strictly prohibited. Export or reexport to countries subject to\n" +
            "U.S. embargo or to entities identified on U.S. export exclusion lists,\n" +
            "including, but not limited to, the denied persons and specially\n" +
            "designated nationals lists is strictly prohibited.\n";
    
    static public String text() {
        return COPYRIGHT_TEXT;
    }
}
