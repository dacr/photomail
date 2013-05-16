/*
 * ImageList.java
 *
 * Created on 5 avril 2005, 15:10
 */

package com.photomail.gui;

import com.photomail.image.ImageInfo;
import com.photomail.image.ThumbnailsGenerator;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

/**
 *
 * @author dcr
 */
public class JImageList extends JList {
    
    /** Creates a new instance of ImageList */
    public JImageList(ListModel model) {
        setCellRenderer(new ImageListCellRenderer());
        setModel(model);
    }
}


class ImageListCellRenderer extends JLabel implements ListCellRenderer {
    
    /** Creates a new instance of ImageListCellRenderer */
    public ImageListCellRenderer() {
        setPreferredSize(new Dimension(128,ThumbnailsGenerator.HEIGHT));
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        ImageInfo ii = (ImageInfo)value;
        setText(ii.getName());
        setIcon(ii.getThumbnail());
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
