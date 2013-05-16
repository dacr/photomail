package com.photomail;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import com.photomail.setup.Setup;
import com.photomail.gui.GUI;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import com.photomail.tools.CompactFormatter;


public class Main {
    
    private static Logger log;
    private static String homeDir;

    
    public static void main(String[] args) throws Exception {
        Setup.initDirectories();
        
        LogManager lm = LogManager.getLogManager();
        lm.readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
        log = Logger.getLogger(Main.class.getName());
        try {
            String logFilename = new File(Setup.getWorkDirFile(), "logs/application.log").getAbsolutePath();
            Handler fh = new FileHandler(logFilename, 1000*1024, 5, true);
            fh.setFormatter(new CompactFormatter());
            Logger rootLog = Logger.getLogger("");
            rootLog.addHandler(fh);
            log = Logger.getLogger(Main.class.getName());
        } catch(IOException e) {
            e.printStackTrace();
        }

        
        log.info("Application started");
        // ----------------------------------------------------------------------------
        try {
            UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
        } catch (Exception e) {
            log.log(Level.WARNING, "Can't get Look&Feel", e);
        }
        // ----------------------------------------------------------------------------
        try {
            new GUI(Setup.load());
        } catch(IOException e) {
            log.log(Level.SEVERE, "Can't load setup", e);
        }
    }

}

