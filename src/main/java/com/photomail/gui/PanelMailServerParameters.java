/*
 * PanelMailServerParameters.java
 *
 * Created on 11 avril 2005, 14:34
 */

package com.photomail.gui;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.photomail.form.Form;
import com.photomail.setup.Resources;
import com.photomail.setup.Setup;
import com.photomail.setup.SetupMailServer;
import com.photomail.tools.MailAccount;
import com.photomail.tools.MailConfigExtractor;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author dcr
 */
public class PanelMailServerParameters extends JPanel  implements WizardDialogSpecificButtons {
    private SetupMailServer setupMailServer;
    private List<JButton> dialogButtons;
    
    public Form<SetupMailServer> form;
    
    public JFrame     frame;
    public JTextField mailFrom;
    public JTextField server;
    public JTextField port;
    public JCheckBox  auth;
    public JTextField login;
    public JTextField password;
    
    /** Creates a new instance of PanelMailServerParameters */
    public PanelMailServerParameters(WizardData wd) {
        this.setupMailServer = wd.getSetup().getMailServer();
        this.frame           = wd.getFrame();
        setLayout(new BorderLayout());

        FormLayout formlayout = new FormLayout(
                "4dlu, right:pref, 4dlu, left:pref:grow, 4dlu",
                "min, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu");

        form = new Form<SetupMailServer>(setupMailServer, formlayout);
        form.setBorder(Borders.DIALOG_BORDER);

        CellConstraints cc = new CellConstraints();
        int r=1;

        JTitleLabel title = new JTitleLabel(Resources.getString("wizard.servercfg.title"));
        form.add(title, cc.xyw(1,r,5));
        r+=2;
        
        Separator separator = new Separator(Resources.getString("wizard.servercfg.server.title"));
        form.add(separator, cc.xyw(1,r,4));
        r+=2;

        mailFrom = new JTextField(40);
        JLabel mailFromLabel = new JLabel(Resources.getString("wizard.servercfg.server.from.label"));
        mailFromLabel.setLabelFor(mailFrom);
        mailFrom.setName("mailFrom");
        form.add(mailFromLabel, cc.xy(2,r));
        form.add(mailFrom     , cc.xy(4,r));
        r+=2;
        
        server = new JTextField(40);
        JLabel serverLabel = new JLabel(Resources.getString("wizard.servercfg.server.address.label"));
        serverLabel.setLabelFor(server);
        server.setName("server");
        form.add(serverLabel, cc.xy(2,r));
        form.add(server     , cc.xy(4,r));
        r+=2;

        port = new JTextField(8);
        JLabel portLabel = new JLabel(Resources.getString("wizard.servercfg.server.port.label"));
        portLabel.setLabelFor(port);
        port.setName("port");
        form.add(portLabel, cc.xy(2,r));
        form.add(port     , cc.xy(4,r));
        r+=2;

        auth = new JCheckBox();
        JLabel authLabel = new JLabel(Resources.getString("wizard.servercfg.server.auth.label"));
        authLabel.setLabelFor(auth);
        auth.setName("auth");
        form.add(auth     , cc.xy(2,r));
        form.add(authLabel, cc.xy(4,r));
        r+=2;
        
        login = new JTextField(16);
        JLabel loginLabel = new JLabel(Resources.getString("wizard.servercfg.server.login.label"));
        loginLabel.setLabelFor(login);
        login.setName("login");
        form.add(loginLabel, cc.xy(2,r));
        form.add(login     , cc.xy(4,r));
        r+=2;

        password = new JPasswordField(16);
        JLabel passwordLabel = new JLabel(Resources.getString("wizard.servercfg.server.password.label"));
        passwordLabel.setLabelFor(password);
        password.setName("password");
        form.add(passwordLabel, cc.xy(2,r));
        form.add(password     , cc.xy(4,r));
        r+=2;

        dialogButtons = new ArrayList<JButton>();
        dialogButtons.add(new JButton(new PanelMailServerAutoConfigAction(this)));
        
        List<JComponent> comps = new ArrayList<JComponent>();
        comps.add(login);
        comps.add(password);
        auth.setAction(new JCheckBoxAction(auth, comps));
        
        add(form, BorderLayout.CENTER);
    }

    public List<JButton> getAdditionalButtonsFromPanel() {
        return dialogButtons;
    }
}


class PanelMailServerAutoConfigAction extends JButtonAction {
    private PanelMailServerParameters panel;
    public PanelMailServerAutoConfigAction(PanelMailServerParameters panel) {
        super(Resources.getString("wizard.servercfg.action.autocfg.label"));
        this.panel=panel;
    }
    public void actionPerformed(ActionEvent actionEvent) {
        Set<MailAccount> accounts;
        MailAccount account=null;
        try {
            accounts = MailConfigExtractor.findMailAccount();
        } catch(IOException e) {
            Logger log = Logger.getLogger(Setup.class.getName());
            log.log(Level.SEVERE, "Was unable to extract mail configs", e);
            return;
        }
        if (accounts.size()==0) {
            return;
        }
        if (accounts.size()==1) {
            account = accounts.iterator().next();
        }
        if (accounts.size() >1) {
            account = PanelMailServerAutoConfigSelect.choosePopup(panel.frame, accounts);
        }
        if (account == null) {
            Logger log = Logger.getLogger(getClass().getName());
            log.log(Level.SEVERE, "null account has been chosen !!!!!!!!");
            return;
        }
        panel.mailFrom.setText(account.getEmailAddress());
        panel.server.setText(account.getSmtpServer());
        panel.port.setText(Integer.toString(account.getSmtpPort()));
        panel.auth.setSelected(account.isSmtpUseAuth());
        panel.login.setText(account.getSmtpUsername());
        panel.password.setText("");
        
        panel.form.updateBean(); // Pour forcer une prise en compte dans le bean
    }
}

