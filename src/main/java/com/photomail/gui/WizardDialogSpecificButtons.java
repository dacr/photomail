/*
 * WizardDialogSpecificButtons.java
 *
 * Created on 30 juin 2005, 09:59
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.photomail.gui;

import java.util.List;
import javax.swing.JButton;

/**
 *
 * @author David Crosson
 */
public interface WizardDialogSpecificButtons {
    public List<JButton> getAdditionalButtonsFromPanel();
}
