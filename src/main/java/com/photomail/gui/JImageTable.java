/*
 * JImageTable.java
 *
 * Created on 6 avril 2005, 12:07
 */

package com.photomail.gui;

import com.photomail.image.ImageInfo;
import com.photomail.image.ThumbnailsGenerator;
import com.photomail.setup.Resources;
import java.awt.Component;
import java.text.SimpleDateFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author dcr
 */
public class JImageTable extends JTable {
    
    /** Creates a new instance of JImageTable */
    public JImageTable(TableModel model) {
        setModel(model);
        setDefaultRenderer(Object.class, new JImageTableDefaultRenderer());
        setRowHeight(ThumbnailsGenerator.HEIGHT);
        setOpaque(true);
    }
}

class JImageTableDefaultRenderer extends DefaultTableCellRenderer {
    SimpleDateFormat shortDate = new SimpleDateFormat(Resources.getString("image.table.column.date.format"));
    SimpleDateFormat longDate  = new SimpleDateFormat(Resources.getString("image.table.column.date.longformat"));
    public void JImageTableDefaultRenderer() {
        setOpaque(true);
    }
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
        ImageInfo ii = (ImageInfo) value;
        setIcon(null);
        setText(null);
        setToolTipText(null);
        switch(column) {
            case 0:
                setIcon(ii.getThumbnail());
                setToolTipText(ii.getAbsolutePath());
                setHorizontalAlignment(JLabel.RIGHT);
                break;
            case 1: 
                setText(shortDate.format(ii.getDate()));
                setToolTipText(longDate.format(ii.getDate()));
                setHorizontalAlignment(JLabel.LEFT);
                break;
            default:
        }
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }
        return this;
    }
}


