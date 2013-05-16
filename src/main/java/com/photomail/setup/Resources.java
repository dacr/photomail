/*
 * Ressources.java
 *
 * Created on 14 avril 2005, 10:16
 */

package com.photomail.setup;

import java.util.ResourceBundle;

import javax.swing.ImageIcon;

/**
 *
 * @author dcr
 */
public class Resources {
    
    /** Creates a new instance of Ressources */
    private Resources() {
    }
    
    static public ImageIcon createIcon(String name) {
        String path = "/icons/"+name;
        return new ImageIcon(Resources.class.getResource(path));
    }

    static public ImageIcon createIconLogo() {
        return createIcon("logo.png");
    }
    static public ImageIcon createIconLogo32() {
        return createIcon("logo32.png");
    }
    static public ImageIcon createIconSend() {
        return createIcon("logo.png");
    }
    static public ImageIcon createIconStop() {
        return createIcon("stop.png");
    }
    static public ImageIcon createIconConfig() {
        return createIcon("config.png");
    }
    static public ImageIcon createIconRotateRight() {
        return createIcon("turnright.png");
    }
    static public ImageIcon createIconRotateLeft() {
        return createIcon("turnleft.png");
    }
    static public ImageIcon createIconPrevious() {
        return createIcon("left.png");
    }
    static public ImageIcon createIconNext() {
        return createIcon("right.png");
    }
    static public ImageIcon createIconFirst() {
        return createIcon("first.png");
    }
    static public ImageIcon createIconLast() {
        return createIcon("last.png");
    }
    static public ImageIcon createDirAdd() {
        return createIcon("diradd.png");
    }
    static public ImageIcon createDirRemove() {
        return createIcon("dirremove.png");
    }
    static public ImageIcon createUserAdd() {
        return createIcon("useradd.png");
    }
    static public ImageIcon createUserRemove() {
        return createIcon("userremove.png");
    }
    static public ImageIcon createUserEdit() {
        return createIcon("useredit.png");
    }
    static public ImageIcon createUserDelete() {
        return createIcon("userdelete.png");
    }
    
    static public ResourceBundle getBundle() {
        return ResourceBundle.getBundle("i18n/resources");
    }
    static public String getString(String key) {
        return getBundle().getString(key);
    }
    static public char getChar(String key) {
        return getBundle().getString(key).charAt(0);
    }
}
