/*
 * WizardData.java
 *
 * Created on 24 mars 2005, 16:06
 */

package com.photomail.gui;

import com.photomail.setup.Setup;
import javax.swing.JFrame;

/**
 *
 * @author dcr
 */
public class WizardData {
    
    private Setup      setup;
    private Activities activities;
    private JFrame     frame;
    
    /** Creates a new instance of WizardData */
    public WizardData() {
    }

    public Setup getSetup() {
        return setup;
    }

    public void setSetup(Setup setup) {
        this.setup = setup;
    }

    public Activities getActivities() {
        return activities;
    }

    public void setActivities(Activities activities) {
        this.activities = activities;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }
    
}
