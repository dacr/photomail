/*
 * Test.java
 *
 * Created on 26 septembre 2004, 22:55
 */

package com.photomail;

import com.photomail.image.TestImageLoading;
import com.photomail.image.TestImageOperations;
import com.photomail.setup.Setup;
import com.photomail.tools.CompactFormatter;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import junit.framework.TestSuite;




/**
 *
 * @author  dcr
 */
public class Test {
    
    public static void main(String args[]) throws Exception {
        Setup.initDirectories();
        
        LogManager lm = LogManager.getLogManager();
        lm.readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
        Logger log = Logger.getLogger(Main.class.getName());
        try {
            String logFilename = new File(Setup.getWorkDirFile(), "logs/application.log").getAbsolutePath();
            Handler fh = new FileHandler(logFilename, 1000*1024, 5, true);
            fh.setFormatter(new CompactFormatter());
            Logger rootLog = Logger.getLogger("");
            rootLog.addHandler(fh);
            log = Logger.getLogger(Test.class.getName());
        } catch(IOException e) {
            e.printStackTrace();
        }
        log.info("Starting tests");
        junit.textui.TestRunner.run(suite());
    }

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();
//        suite.addTestSuite(TestForm.class);
//        suite.addTestSuite(TestMail.class);
//        suite.addTestSuite(TestImageProcess.class);
//        suite.addTestSuite(TestGUI.class);
        suite.addTestSuite(TestImageOperations.class);
        suite.addTestSuite(TestImageLoading.class);
        return suite;
    }
    
}
