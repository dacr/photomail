/*
 * JTitleLabel.java
 *
 * Created on 15 avril 2005, 10:02
 */

package com.photomail.gui;

import com.photomail.setup.Resources;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author dcr
 */
public class JTitleLabel extends JLabel {
    
    /** Creates a new instance of JTitleLabel */
    public JTitleLabel(String title) {
        super("<html><body><h2>"+title+"</h2></body></html>", Resources.createIconLogo32(), JLabel.LEFT);
    }
    public JTitleLabel(String title, ImageIcon imageIcon) {
        super("<html><body><h2>"+title+"</h2></body></html>", imageIcon, JLabel.LEFT);
    }
}
