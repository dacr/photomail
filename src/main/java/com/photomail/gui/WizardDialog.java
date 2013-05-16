/*
 * WizardDialog.java
 *
 * Created on 11 avril 2005, 17:01
 */

package com.photomail.gui;

import com.photomail.setup.Resources;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author dcr
 */
public class WizardDialog extends JDialog {

    /** Creates a new instance of WizardDialog */
    public WizardDialog(JFrame frame, String title, JPanel panel) {
        super(frame, title, true);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (panel instanceof WizardDialogSpecificButtons) {
            WizardDialogSpecificButtons wdsb = (WizardDialogSpecificButtons)panel;
            for(JButton button: wdsb.getAdditionalButtonsFromPanel()) {
                buttons.add(button);
            }
        }
        buttons.add(new JButton(new WizardDialogCloseAction(this)));
        add(buttons, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
        
        pack();
        if (frame != null) {
            Rectangle parent = frame.getBounds();
            int x=parent.x+(parent.width-getWidth())/2;
            int y=parent.y+(parent.height-getHeight())/2;
            setLocation(x, y);
        } else {
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            int x= 0 +(screen.width-getWidth())/2;
            int y= 0 +(screen.height-getHeight())/2;
            setLocation(x, y);
        }
    }
}


class WizardDialogCloseAction extends JButtonAction {
    private JDialog dialog;
    public WizardDialogCloseAction(JDialog dialog) {
        super(Resources.getString("action.close.label"));
        this.dialog=dialog;
    }
    public void actionPerformed(ActionEvent actionEvent) {
        dialog.dispose();
    }
}

