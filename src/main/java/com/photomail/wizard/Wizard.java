/*
 * Wizard.java
 *
 * Created on 22 février 2005, 11:47
 */

package com.photomail.wizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author Administrateur
 */
public class Wizard<DataType> extends JPanel implements ActionListener, WizardPanelListener<DataType> {
    DataType data;
    Class firstWizardPanel;
    LinkedList<Class> history;
    Map<Class, WizardPanel<DataType>> instances;
    JButton cancel, next, previous, finish;
    JPanel  content;
    CardLayout cardLayout;
    
    /** Creates a new instance of Wizard */
    public Wizard(DataType data, Class wizardPanelClass, Action cancelAction, Action previousAction, Action nextAction, Action finishAction) {
        this.data = data;
        this.history = new LinkedList<Class>();
        this.instances = new HashMap<Class, WizardPanel<DataType>>();
        this.firstWizardPanel = wizardPanelClass;
        initComponents(cancelAction, previousAction, nextAction, finishAction);
        showup(wizardPanelClass);
    }
    
    protected void initComponents(Action cancelAction, Action previousAction, Action nextAction, Action finishAction) {
        setLayout(new BorderLayout());

        cancel     = new JButton(cancelAction);
        previous   = new JButton(previousAction);
        next       = new JButton(nextAction);
        finish     = new JButton(finishAction);
        previous.addActionListener(this);
        next.addActionListener(this);
        cancel.addActionListener(this);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancel);
        buttons.add(previous);
        buttons.add(next);
        buttons.add(finish);
        add(buttons, BorderLayout.SOUTH);
        
        cardLayout = new CardLayout();
        content    = new JPanel(cardLayout);
        add(content, BorderLayout.CENTER);
    }
    protected void showup(Class wizardPanelClass) {
        history.add(wizardPanelClass);
        WizardPanel<DataType> wp;
        if (!instances.containsKey(wizardPanelClass)) {
            Constructor[] ctts = wizardPanelClass.getDeclaredConstructors();
            // TODO : Revoir test, surtout quand il n'y pas qu'un seul constructeur
            try {
                wp = (WizardPanel<DataType>)ctts[0].newInstance(data);
                wp.addListener(this);
            } catch(InstantiationException e) {
                e.printStackTrace(); // TODO : A traiter correctement
                throw new RuntimeException("TODO");
            } catch(IllegalAccessException e) {
                e.printStackTrace(); // TODO : A traiter correctement
                throw new RuntimeException("TODO");
            } catch(InvocationTargetException e) {
                e.printStackTrace(); // TODO : A traiter correctement
                throw new RuntimeException("TODO");
            }
            content.add(wp, Integer.toString(wp.hashCode()));
            instances.put(wizardPanelClass, wp);
        } else {
            wp = instances.get(wizardPanelClass);
            wp.activate();
        }
        cardLayout.show(content, Integer.toString(wp.hashCode()));
        wizardPanelHasChanged(wp);
    }

    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        if (actionEvent.getSource() == next) {
            WizardPanel wp = instances.get(history.getLast());
            wp.nextAsked();
            showup(instances.get(history.getLast()).getNextPanel());
        }
        if (actionEvent.getSource() == previous) {
            history.removeLast();
            showup(history.removeLast());
        }
        if (actionEvent.getSource() == cancel) {
            // sur le panneau en cours on exécute le cancelAsked (pour stopper d'éventuel traitement en cours)
            WizardPanel wp = instances.get(history.getLast());
            wp.cancelAsked();
        }
    }

    public void wizardPanelHasChanged(WizardPanel<DataType> wizardPanel) {
        if (wizardPanel != instances.get(history.getLast())) return;
            
        if (wizardPanel.canFinish()) finish.setEnabled(true);
        else finish.setEnabled(false);

        if (wizardPanel.canGoNext()) next.setEnabled(true);
        else next.setEnabled(false);

        if (history.size()==1 || !wizardPanel.canGoPrevious()) previous.setEnabled(false);
        else previous.setEnabled(true);        
    }
}
