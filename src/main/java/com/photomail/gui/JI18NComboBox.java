/*
 * JI18NComboBox.java
 *
 * Created on 19 avril 2005, 08:25
 */

package com.photomail.gui;

import com.photomail.setup.Resources;
import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author dcr
 */
public class JI18NComboBox extends JComboBox {
    String i18nBaseResourceName;
    
    /** Creates a new instance of JI18NComboBox */
    public JI18NComboBox(String i18nBaseResourceName) {
        super();
        this.i18nBaseResourceName = i18nBaseResourceName;
        setRenderer(new JI18NComboBoxRenderer(i18nBaseResourceName));
    }
    
}

class JI18NComboBoxRenderer extends JLabel implements ListCellRenderer {
    String i18nBaseResourceName;
    public JI18NComboBoxRenderer(String i18nBaseResourceName) {
        if (!i18nBaseResourceName.endsWith(".")) i18nBaseResourceName+=".";
        this.i18nBaseResourceName = i18nBaseResourceName;
        setOpaque(true);
    }
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value!= null) {
            setText(Resources.getString(i18nBaseResourceName + value.toString()));
        } else {
            setText("");
        }
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
    
}