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
public enum TimePeriod {
    ONE_DAY,
    TWO_DAY,
    THREE_DAYS,
    ONE_WEEK,
    TWO_WEEKS,
    ONE_MONTH,
    TWO_MONTHS,
    THREE_MONTHS,
    SIX_MONTHS,
    ONE_YEAR;
    
     // TODO - Remove next line when jdk1.5.0_0x x<=2 will be corrected : http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5015403
    static { EnumPersistenceDelegate.installFor(values()[0].getClass()); }
}

