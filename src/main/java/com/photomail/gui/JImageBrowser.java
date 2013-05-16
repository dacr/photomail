/*
 * BasicViewer.java
 *
 * Created on 15 février 2005, 09:19
 */

package com.photomail.gui;

import com.photomail.image.ImageInfo;
import com.photomail.setup.Resources;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.*;
import java.text.ParseException;
import javax.swing.text.DefaultFormatter;




public class JImageBrowser extends JPanel implements KeyEventDispatcher, PropertyChangeListener {
    JImageView viewer;
    ImageDataModel imageDataModel;
    
    int index;
    JLabel infoLabel;
    JLabel dimLabel;
    JLabel sizeLabel;
    JFormattedTextField commentsField, keywordsField;
    JFrame frame;
    JComboBox imageSelector;

    public JImageBrowser(ImageDataModel imageDataModel) throws IOException {
        this.imageDataModel = imageDataModel;
        this.index = 0;
        viewer = new JImageView();

        dimLabel  = new JLabel();
        infoLabel  = new JLabel();
        sizeLabel = new JLabel();
        JPanel ctrl = new JPanel();
        JPanel tool = new JPanel();

        imageSelector = new JImageComboBox(imageDataModel);
        imageSelector.addActionListener(new ComboAction(imageSelector, this));

        viewer.getInputMap().put(KeyStroke.getKeyStroke("pressed down"), "doSomething");
        viewer.getActionMap().put("doSomething", new NextAction(this));
        

        ctrl.setLayout(new GridBagLayout());
        GridBagConstraints cc = new GridBagConstraints();
        cc.insets = new Insets(4,4,0,0);
        
        cc.weightx = 1.0;
        cc.fill = GridBagConstraints.BOTH;
        cc.gridwidth  = 1;
        cc.gridheight = 3;
        cc.weighty    = 1.0;
        ctrl.add(imageSelector, cc);

        cc.weighty = 0.0;
        cc.gridheight = 1;
        ctrl.add(new JButton(new FirstAction(this)), cc);
        ctrl.add(new JButton(new PreviousAction(this)), cc);
        ctrl.add(new JButton(new NextAction(this)), cc);
        ctrl.add(new JButton(new LastAction(this)), cc);
        ctrl.add(new JButton(new RotateLeftAction(this)), cc);
        ctrl.add(new JButton(new RotateRightAction(this)), cc);
        ctrl.add(infoLabel, cc);
        ctrl.add(new JLabel("-"), cc);
        cc.gridwidth = GridBagConstraints.REMAINDER;
        ctrl.add(sizeLabel, cc);
        
        cc.weightx=1.0;
        cc.gridwidth = 1;
        cc.fill = GridBagConstraints.NONE;
        cc.gridwidth = 1;
        ctrl.add(new JLabel(Resources.getString("wizard.browser.comments.label")), cc);

        commentsField = new JFormattedTextField(new DefaultFormatter() {
                        public boolean getOverwriteMode()  {
                            return false;
                        } });
        commentsField.setColumns(20);
        commentsField.addPropertyChangeListener("value", this);
        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.gridwidth = GridBagConstraints.REMAINDER;
        ctrl.add(commentsField,cc);


        cc.weightx=1.0;
        cc.gridwidth = 1;
        cc.fill = GridBagConstraints.NONE;
        cc.gridwidth = 1;
        ctrl.add(new JLabel(Resources.getString("wizard.browser.keywords.label")), cc);
        keywordsField = new JFormattedTextField(new DefaultFormatter() {
                        public boolean getOverwriteMode()  {
                            return false;
                        } });
        keywordsField.setColumns(20);
        keywordsField.addPropertyChangeListener("value", this);
        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.gridwidth = GridBagConstraints.REMAINDER;
        ctrl.add(keywordsField,cc);


        setLayout(new BorderLayout());
        add(viewer, BorderLayout.CENTER);
        add(ctrl, BorderLayout.SOUTH);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }
    private int getIndex() {
        return index;
    }
    private int getCount() {
        return imageDataModel.getSize();
    }
    
    public void refreshImageView() {
        if (getCount()==0) {
            try {
                viewer.setImageInfo(null);
            } catch(IOException e) {
                // Can be safely ignored
            }
            return;
        }
        if (index >= getCount()) index = getCount()-1;
        if (index <0) index = 0;
        try {
            ImageInfo imageInfo = imageDataModel.get(index);
            viewer.setImageInfo(imageInfo);
            infoLabel.setText(""+(index+1)+"/"+(getCount()));
            sizeLabel.setText(""+(imageInfo.length()/1024)+"Ko");
            imageSelector.setToolTipText(imageInfo.getAbsolutePath());
            imageSelector.getModel().setSelectedItem(imageInfo);
            commentsField.setValue(imageInfo.getComments());
            keywordsField.setValue(imageInfo.getKeywords());
            if (imageInfo.getComments() != null)
                commentsField.setCaretPosition(imageInfo.getComments().length());
            if (imageInfo.getKeywords() != null) 
                keywordsField.setCaretPosition(imageInfo.getKeywords().length());
        } catch(IOException e) {
            System.out.println("IOException :"+e.getMessage());
        }
    }

    
    public void first() {
        index = 0;
        refreshImageView();                
    }
    public void next() {
        //index = (index+1)%getCount();
        if (index < getCount()-1) {
        index++;
            refreshImageView();        
        }
    }
    public void previous() {
        //index = (index==0)?(getCount()-1):(index-1);
        if (index>0) {
            index--;
            refreshImageView();
        }
    }
    public void last() {
        index = getCount()-1;
        refreshImageView();        
    }
    public void gotoindex(int index) {
        if (index == this.index) return; // TODO : Faire mieux car on passe systématiquement ici à cause de refreshImageView...
        this.index = index;
        refreshImageView();
    }
    public void rotateLeft() {
        viewer.rotateLeft();
    }
    public void rotateRight() {
        viewer.rotateRight();
    }

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (imageDataModel.getSize() ==0) return;
        if (propertyChangeEvent.getSource() == commentsField) {
            String comments = (String)propertyChangeEvent.getNewValue();
            ImageInfo imageInfo = imageDataModel.get(index);
            if ( (imageInfo.getComments()!=null && !imageInfo.getComments().equals(comments)) ||
                 (imageInfo.getComments()==null && comments!=null)) {
                imageInfo.setComments(comments);
            }
        }
        if (propertyChangeEvent.getSource() == keywordsField) {
            String keywords = (String)propertyChangeEvent.getNewValue();
            ImageInfo imageInfo = imageDataModel.get(index);
            if ( (imageInfo.getKeywords()!=null && !imageInfo.getKeywords().equals(keywords)) ||
                 (imageInfo.getKeywords()==null && keywords!=null)) {
                imageInfo.setKeywords(keywords);
            }
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getID() == KeyEvent.KEY_RELEASED)
            switch(keyEvent.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_PAGE_UP:
                    try {
                        commentsField.commitEdit();
                        keywordsField.commitEdit();
                    } catch(ParseException e) {}
                    if (keyEvent.isShiftDown())        first();
                    else if (keyEvent.isControlDown()) rotateLeft();
                    else                               previous();
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_PAGE_DOWN:
                    try {
                        commentsField.commitEdit();
                        keywordsField.commitEdit();
                    } catch(ParseException e) {}
                    if (keyEvent.isShiftDown())        last();
                    else if (keyEvent.isControlDown()) rotateRight();
                    else                               next();
                    break;
                default:
                    //showKeyCode(keyEvent);
                    return true;
            }
        return false;
    }

    /**
     * This method extract the symbolic keycode associated with the keyevent.
     * It helps developpers to find the keycode is looking for
     */
    public void showKeyCode(KeyEvent ke) {
        Field[] fields = KeyEvent.class.getFields();
        for(Field field:fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) && Modifier.isPublic(mod) && field.getName().startsWith("VK_")) {
                try {
                    int code = field.getInt(null);
                    if (code == ke.getKeyCode()) System.out.println("KEY IS :"+field.getName());
                }catch(IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}





class FirstAction extends JButtonAction {
    JImageBrowser viewer;
    public FirstAction(JImageBrowser viewer) {
        super(Resources.getString("wizard.browser.action.first.label"));
        putValue(Action.SHORT_DESCRIPTION, Resources.getString("wizard.browser.action.first.hint"));
        putValue(Action.SMALL_ICON, Resources.createIconFirst());
        this.viewer=viewer;
    }
    public void actionPerformed(ActionEvent e) {
        viewer.first();
    }
}

class LastAction extends JButtonAction {
    JImageBrowser viewer;
    public LastAction(JImageBrowser viewer) {
        super(Resources.getString("wizard.browser.action.last.label"));
        putValue(Action.SHORT_DESCRIPTION, Resources.getString("wizard.browser.action.last.hint"));
        putValue(Action.SMALL_ICON, Resources.createIconLast());
        this.viewer=viewer;
    }
    public void actionPerformed(ActionEvent e) {
        viewer.last();
    }
}

class NextAction extends JButtonAction {
    JImageBrowser viewer;
    public NextAction(JImageBrowser viewer) {
        super(Resources.getString("wizard.browser.action.next.label"));
        putValue(Action.SHORT_DESCRIPTION, Resources.getString("wizard.browser.action.next.hint"));
        putValue(Action.SMALL_ICON, Resources.createIconNext());
        this.viewer=viewer;
    }
    public void actionPerformed(ActionEvent e) {
        viewer.next();
    }
}

class PreviousAction extends JButtonAction {
    JImageBrowser viewer;
    public PreviousAction(JImageBrowser viewer) {
        super(Resources.getString("wizard.browser.action.previous.label"));
        putValue(Action.SHORT_DESCRIPTION, Resources.getString("wizard.browser.action.previous.hint"));
        putValue(Action.SMALL_ICON, Resources.createIconPrevious());
        this.viewer=viewer;
    }
    public void actionPerformed(ActionEvent e) {
        viewer.previous();
    }
}


class RotateLeftAction extends JButtonAction {
    JImageBrowser viewer;
    public RotateLeftAction(JImageBrowser viewer) {
        super(Resources.getString("wizard.browser.action.rotateleft.label"));
        putValue(Action.SHORT_DESCRIPTION, Resources.getString("wizard.browser.action.rotateleft.hint"));
        putValue(Action.SMALL_ICON, Resources.createIconRotateLeft());
        this.viewer=viewer;
        
    }
    public void actionPerformed(ActionEvent e) {
        viewer.rotateLeft();
    }
}

class RotateRightAction extends JButtonAction {
    JImageBrowser viewer;
    public RotateRightAction(JImageBrowser viewer) {
        super(Resources.getString("wizard.browser.action.rotateright.label"));
        putValue(Action.SHORT_DESCRIPTION, Resources.getString("wizard.browser.action.rotateright.hint"));
        putValue(Action.SMALL_ICON, Resources.createIconRotateRight());
        this.viewer=viewer;
    }
    public void actionPerformed(ActionEvent e) {
        viewer.rotateRight();
    }
}



class ComboAction extends JButtonAction {
    JImageBrowser viewer;
    JComboBox combobox;
    public ComboAction(JComboBox combobox, JImageBrowser viewer) {
        super();
        this.viewer=viewer;
        this.combobox=combobox;
    }
    public void actionPerformed(ActionEvent e) {
        viewer.gotoindex(combobox.getSelectedIndex());
    }    
}

