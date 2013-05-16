/*
 * EnumPersistenceDelegate.java
 *
 * Created on 22 mars 2005, 22:45
 */

package com.photomail.setup;

import java.beans.BeanInfo;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.IntrospectionException;
import java.beans.Introspector;


// TODO - Remove this class when jdk1.5.0_0x x<=2 will be corrected : http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5015403
public class EnumPersistenceDelegate extends DefaultPersistenceDelegate {

    private static EnumPersistenceDelegate epd = new EnumPersistenceDelegate();

    public static void installFor(Class<? extends Enum> enumClass) {
        try {
            BeanInfo info = Introspector.getBeanInfo( enumClass );
            info.getBeanDescriptor().setValue( "persistenceDelegate", epd );
        } catch( IntrospectionException exception ) {
            // Do whatever you'd normally do with exceptions here
            exception.printStackTrace();
        }
    }
    
    protected Expression instantiate(Object oldInstance, Encoder out) {
        return new Expression(Enum.class,
                "valueOf",
                new Object[] { oldInstance.getClass(),
                        ((Enum) oldInstance).name() });
    }
    
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return oldInstance == newInstance;
    }
}