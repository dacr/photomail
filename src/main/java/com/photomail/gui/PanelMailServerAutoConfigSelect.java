/*
 * PanelMailServerParameters.java
 *
 * Created on 11 avril 2005, 14:34
 */

package com.photomail.gui;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.photomail.setup.Resources;
import com.photomail.tools.MailAccount;
import java.awt.BorderLayout;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 *
 * @author dcr
 */
public class PanelMailServerAutoConfigSelect extends JPanel {
    private Set<MailAccount> accounts;
    private MailAccount selected=null;
    
    
    public static MailAccount choosePopup(JFrame parent, Set<MailAccount> accounts) {
        PanelMailServerAutoConfigSelect panel = new PanelMailServerAutoConfigSelect(accounts);
        JDialog dialog = new WizardDialog(parent, Resources.getString("wizard.serverautocfg.title"), panel);
        dialog.setVisible(true);
        return panel.getSelected();        
    }
    public static MailAccount choosePopup(Set<MailAccount> accounts) {
        return choosePopup(null, accounts);
    }
    
    /** Creates a new instance of PanelMailServerParameters */
    public PanelMailServerAutoConfigSelect(Set<MailAccount> accounts) {
        this.accounts = accounts;
        setLayout(new BorderLayout());

        FormLayout formlayout = new FormLayout(
                "4dlu, right:pref, 4dlu, left:pref:grow, 4dlu",
                "min,  2dlu, pref, 2dlu, pref, 2dlu, pref,  2dlu");

        JPanel content = new JPanel();
        content.setBorder(Borders.DIALOG_BORDER);
        content.setLayout(formlayout);

        CellConstraints cc = new CellConstraints();
        int r=1;

        JTitleLabel title = new JTitleLabel(Resources.getString("wizard.serverautocfg.title"));
        content.add(title, cc.xyw(1,r,5));
        r+=2;
        
        Separator separator = new Separator(Resources.getString("wizard.serverautocfg.select.title"));
        content.add(separator, cc.xyw(1,r,4));
        r+=2;


        
        JComboBox comboBox = new JComboBox(new DefaultComboBoxModel(accounts.toArray()) {
            public void setSelectedItem(Object item) {
                super.setSelectedItem(item);
                setSelected((MailAccount)item);
            }
        });
        MailAccount tmp = accounts.iterator().next();
        comboBox.setSelectedItem(tmp);
        setSelected(tmp);
        
        content.add(comboBox, cc.xy(4,r));
        r+=2;

        
        add(content, BorderLayout.CENTER);
    }

    public MailAccount getSelected() {
        return selected;
    }

    public void setSelected(MailAccount selected) {
        this.selected = selected;
    }

}

