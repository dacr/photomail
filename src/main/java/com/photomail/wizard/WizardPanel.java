/*
 * WizardPanel.java
 *
 * Created on 22 février 2005, 11:48
 */

package com.photomail.wizard;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author Administrateur
 */
public abstract class WizardPanel<DataType> extends JPanel {
    private DataType data;
    private List<WizardPanelListener<DataType>> listeners;
    /** Creates a new instance of WizardPanel */
    public WizardPanel(DataType data) {
        this.data = data;
        this.listeners = new ArrayList<WizardPanelListener<DataType>>();
        initComponents();
        activate();
    }
    public DataType getData() {
        return data;
    }
    public void addListener(WizardPanelListener<DataType> wizardPanelListener) {
        listeners.add(wizardPanelListener);
    }
    public boolean removeListener(WizardPanelListener<DataType> wizardPanelListener) {
        return listeners.remove(wizardPanelListener);
    }
    protected void notifyListeners() {
        for(WizardPanelListener<DataType> listener : listeners) {
            listener.wizardPanelHasChanged(this);
        }
    }
    public void cancelAsked() {
    }
    public void nextAsked() {
    }
    public abstract void initComponents();
    public abstract void activate();
    public abstract boolean canGoPrevious();
    public abstract boolean canGoNext();
    public abstract boolean canFinish();
    public abstract Class getNextPanel();
}
