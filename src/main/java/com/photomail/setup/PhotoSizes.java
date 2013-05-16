/*
 * TestBeanEnum.java
 *
 * Created on 20 mars 2005, 00:32
 */

package com.photomail.setup;

import java.io.Serializable;

/**
 *
 * @author dcr
 */
public enum PhotoSizes {
    R160x100,
    R320x200,
    R640x480,
    R800x600,
    R1024x768,
    R1280x1024,
    R1600x1200;
    
     // TODO - Remove next line when jdk1.5.0_0x x<=2 will be corrected : http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5015403
    static { EnumPersistenceDelegate.installFor(values()[0].getClass()); }
}

