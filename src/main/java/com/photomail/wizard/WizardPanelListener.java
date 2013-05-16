/*
 * WizardPanelListener.java
 *
 * Created on 22 février 2005, 17:02
 */

package com.photomail.wizard;



/**
 *
 * @author Administrateur
 */
public interface WizardPanelListener<DataType> {
    public void wizardPanelHasChanged(WizardPanel<DataType> wizardPanel);
}
