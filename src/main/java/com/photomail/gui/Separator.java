/*
 * Separator.java
 *
 * Created on 11 mars 2005, 23:03
 */

package com.photomail.gui;

import com.jgoodies.forms.layout.FormLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 *
 * @author dcr
 */

class Separator extends JPanel {
    public Separator(String text) {
        setLayout(new FormLayout("pref,4dlu,fill:pref:grow", "pref"));
        JLabel label = new JLabel(text);
        JSeparator separator = new JSeparator();
        label.setForeground(new Color(100,100,200));
        add(label,     "1,1");
        add(separator, "3,1");
    }
}
