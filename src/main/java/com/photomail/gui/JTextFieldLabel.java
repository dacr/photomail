/*
 * JTextFieldLabel.java
 *
 * Created on 15 avril 2005, 09:54
 */

package com.photomail.gui;

import java.awt.Font;
import javax.swing.JTextField;

/**
 *
 * @author dcr
 */
public class JTextFieldLabel extends JTextField {
    
    /** Creates a new instance of JTextFieldLabel */
    public JTextFieldLabel(String message) {
        super(message, message.length());
        setEditable(false);
        setBorder(null);
        setFont(getFont().deriveFont(Font.BOLD));
    }
    
}
