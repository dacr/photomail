/*
 * JImageComboBox.java
 *
 * Created on 6 avril 2005, 12:07
 */

package com.photomail.gui;

import com.photomail.image.ImageInfo;
import com.photomail.image.ThumbnailsGenerator;
import com.photomail.setup.Resources;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author dcr
 */
public class JImageComboBox extends JComboBox {
    
    /** Creates a new instance of JImageComboBox */
    public JImageComboBox(ComboBoxModel model) {
        setPreferredSize(new Dimension(160, ThumbnailsGenerator.HEIGHT));
        setModel(model);
        setRenderer(new JImageComboBoxCellRenderer());
        setFocusable(false);
    }
}



class JImageComboBoxCellRenderer extends JLabel implements ListCellRenderer {
    public JImageComboBoxCellRenderer() {
        super();
        setOpaque(true);
    }
    public Component getListCellRendererComponent(JList list, Object obj, int index, boolean isSelected, boolean cellHasFocus) {
        if (obj==null) {
            setText(Resources.getString("image.combobox.empty.message"));
            setIcon(null);
            setToolTipText(null);
            return this;
        }
        ImageInfo ii = (ImageInfo)obj;
        setText(ii.getName());
        setIcon(ii.getThumbnail());
        setToolTipText(ii.getAbsolutePath());
        
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
