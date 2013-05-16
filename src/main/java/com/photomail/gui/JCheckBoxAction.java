/*
 * JCheckBoxAction.java
 *
 * Created on 11 avril 2005, 10:56
 */

package com.photomail.gui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author dcr
 */
class JCheckBoxAction extends AbstractAction implements ChangeListener {
    private List<JComponent> components;
    private JCheckBox checkBox;

    public JCheckBoxAction(JCheckBox checkBox, JComponent component) {
        this.components = new ArrayList<JComponent>();
        this.components.add(component);
        this.checkBox = checkBox;
        checkBox.addChangeListener(this);
        refresh();
    }

    public JCheckBoxAction(JCheckBox checkBox, List<JComponent> components) {
        this.components = components;
        this.checkBox = checkBox;
        checkBox.addChangeListener(this);
        refresh();
    }

    public void actionPerformed(ActionEvent actionEvent) {
    }
    
    private void refresh() {
        for(JComponent component: components) {
            component.setEnabled(checkBox.isSelected());
        }
    }

    public void stateChanged(ChangeEvent changeEvent) {
        refresh();
    }
}
