/*
 * Main.java
 *
 * Created on 18 février 2005, 15:01
 */

package com.photomail.gui;

import com.photomail.setup.Resources;
import javax.swing.JFrame;

import com.photomail.wizard.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.photomail.setup.Setup;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;



/**
 *
 * @author Administrateur
 */
public class GUI extends JFrame implements WindowListener {
    private Setup setup;
    private WizardData data;

    public GUI(Setup setup) {
        super(Resources.getString("wizard.title"));
        setSetup(setup);
        setData(new WizardData());
        getData().setSetup(setup);
        getData().setActivities(new Activities(setup));
        getData().setFrame(this);

        Wizard<WizardData> wizard = new Wizard<WizardData>(
                getData(),
                WizardPanelSearch.class,
                new WizardCancelAction(this, getData()),
                new WizardPreviousAction(this, getData()),
                new WizardNextAction(this, getData()),
                new WizardFinishAction(this, getData()));
        add(wizard);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x=(screen.width-getWidth())/2;
        int y=(screen.height-getHeight())/2;

        setIconImage(Resources.createIconLogo().getImage());
        setLocation(x, y);
        setVisible(true);
    }

    public Setup getSetup() {
        return setup;
    }

    public void setSetup(Setup setup) {
        this.setup = setup;
    }
    
    public void exit() {
        try {
            getSetup().save();
        } catch(IOException e) {
            Logger log = Logger.getLogger(getClass().getName());
            log.log(Level.SEVERE, "Can't save setup", e);
        } catch(Exception e) {
            Logger log = Logger.getLogger(getClass().getName());
            log.log(Level.SEVERE, "Can't save setup", e);            
        }
        getData().getActivities().stopAll();
        dispose();
        System.exit(0);
    }

    public WizardData getData() {
        return data;
    }

    public void setData(WizardData data) {
        this.data = data;
    }

    public void windowActivated(WindowEvent windowEvent) {
    }

    public void windowClosed(WindowEvent windowEvent) {
    }

    public void windowClosing(WindowEvent windowEvent) {
        exit();
    }

    public void windowDeactivated(WindowEvent windowEvent) {
    }

    public void windowDeiconified(WindowEvent windowEvent) {
    }

    public void windowIconified(WindowEvent windowEvent) {
    }

    public void windowOpened(WindowEvent windowEvent) {
    }
}


abstract class WizardAction extends JButtonAction {
    protected GUI frame;
    protected WizardData wd;
    public WizardAction(String name, GUI frame, WizardData wd) {
        super(name);
        this.frame = frame;
        this.wd = wd;
    }
}
class WizardFinishAction extends WizardAction {
    public WizardFinishAction(GUI frame, WizardData wd) {
        super(Resources.getString("action.finish.label"), frame, wd);
        putValue(JButtonAction.SHORT_DESCRIPTION, Resources.getString("wizard.action.finish.hint"));
    }
    public void actionPerformed(ActionEvent actionEvent) {
        frame.exit();
    }
}
class WizardCancelAction extends WizardAction {
    public WizardCancelAction(GUI frame, WizardData wd) {
        super(Resources.getString("action.close.label"), frame, wd);
        putValue(JButtonAction.SHORT_DESCRIPTION, Resources.getString("wizard.action.close.hint"));
    }
    public void actionPerformed(ActionEvent actionEvent) {
        frame.exit();
    }
}
class WizardPreviousAction extends WizardAction {
    public WizardPreviousAction(GUI frame, WizardData wd) {
        super(Resources.getString("action.previous.label"), frame, wd);
        putValue(JButtonAction.SHORT_DESCRIPTION, Resources.getString("wizard.action.previous.hint"));
    }
    public void actionPerformed(ActionEvent actionEvent) {
    }
}
class WizardNextAction extends WizardAction {
    public WizardNextAction(GUI frame, WizardData wd) {
        super(Resources.getString("action.next.label"), frame, wd);
        putValue(JButtonAction.SHORT_DESCRIPTION, Resources.getString("wizard.action.next.hint"));
    }
    public void actionPerformed(ActionEvent actionEvent) {
    }
}
