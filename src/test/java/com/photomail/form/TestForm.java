/*
 * TestMail.java
 *
 * Created on 26 septembre 2004, 22:55
 */

package com.photomail.form;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author  dcr
 */
public class TestForm extends TestCase {
    
    /** Creates a new instance of TestMail */
    public TestForm(String name) {
        super(name);
    }
    
    public void setUp() throws Exception {
    }
    
    public void testBeanRead() throws Exception {
        TestBean tb = new TestBean();
        tb.setA1(true);
        tb.setA2(Boolean.FALSE);
        tb.setA3(new Short((short)9).shortValue());
        tb.setA4(new Short((short)9));
        tb.setA5("9");
        tb.setA6('9');
        tb.setA7(new Character('9'));
        tb.setA8(9.9f);
        tb.setA9(new Float(9.9f));
        tb.setA10(9);
        tb.setA11(new Integer(9));
        
        Form<TestBean> form = new Form<TestBean>(tb);
        
        assertTrue(form.getAttributeValueAsBoolean(tb, "a1"));
        assertFalse(form.getAttributeValueAsBoolean(tb, "a2"));
        
        assertTrue(form.getAttributeValueAsInt(tb, "a10") == 9);
        assertTrue(form.getAttributeValueAsInt(tb, "a11") == 9);
    }
    
    
    public void testBeanWrite() throws Exception {
        TestBean tb = new TestBean();
        Form<TestBean> form = new Form<TestBean>(tb);
        
        form.updateData(tb, "a1", true);
        assertTrue(tb.isA1());
        form.updateData(tb, "a1", "false");
        assertFalse(tb.isA1());

        
        form.updateData(tb, "a2", true);
        assertTrue(tb.getA2().booleanValue());
        form.updateData(tb, "a2", "false");
        assertFalse(tb.getA2().booleanValue());

        
        form.updateData(tb, "a3", new Short((short)1).shortValue());
        assertTrue(tb.getA3()==1);
        form.updateData(tb, "a3", 0xAA);
        assertTrue(tb.getA3()==0xAA);
        form.updateData(tb, "a3", 2);
        assertTrue(tb.getA3()==2);
        form.updateData(tb, "a3", "3");
        assertTrue(tb.getA3()==3);

        form.updateData(tb, "a4", new Short((short)1).shortValue());
        assertTrue(tb.getA4()==1);
        form.updateData(tb, "a4", 0xAA);
        assertTrue(tb.getA4()==0xAA);
        form.updateData(tb, "a4", 2);
        assertTrue(tb.getA4()==2);
        form.updateData(tb, "a4", "3");
        assertTrue(tb.getA4()==3);

        
        form.updateData(tb, "a5", "test");
        assertTrue(tb.getA5().equals("test"));
        
        form.updateData(tb, "a6", 'W');
        assertTrue(tb.getA6() == 'W');

        form.updateData(tb, "a7", 'W');
        assertTrue(tb.getA7() == 'W');

        form.updateData(tb, "a8", 32.12f);
        assertTrue(tb.getA8() == 32.12f);
        form.updateData(tb, "a8", "12.32");
        assertTrue(tb.getA8() == 12.32f);

        // TODO : FIXEME - I DON'T WORK
        //form.updateData(tb, "a8", 32.12);
        //assertTrue(tb.getA8() == 32.12f);

        form.updateData(tb, "a9", new Float(32.12f));
        assertTrue(tb.getA9() == 32.12f);
        form.updateData(tb, "a9", "12.32");
        assertTrue(tb.getA9() == 12.32f);   
    }
    
    public void TestEnum() throws Exception {
        TestBean tb = new TestBean();        
        tb.setA0(TestBeanEnum.R1024x768);

        Form<TestBean> form = new Form<TestBean>(tb);
        assertTrue(form.getAttributeValue(tb, "a0") == TestBeanEnum.R1024x768);
        
        form.updateData(tb, "a0", TestBeanEnum.R640x480);
        assertTrue(form.getAttributeValue(tb, "a0") == TestBeanEnum.R640x480);

        form.updateData(tb, "a0", "R1280x1024");
        assertTrue(form.getAttributeValue(tb, "a0") == TestBeanEnum.R1280x1024);

    }

}
