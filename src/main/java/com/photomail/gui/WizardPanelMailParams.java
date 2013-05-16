/*
 * WizardPanelIntro.java
 *
 * Created on 22 février 2005, 15:30
 */

package com.photomail.gui;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.photomail.form.Form;
import com.photomail.setup.Resources;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import com.photomail.wizard.WizardPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import com.photomail.setup.SetupMail;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Administrateur
 */
public class WizardPanelMailParams extends WizardPanel<WizardData> {
    public WizardPanelMailParams(WizardData wd) {
        super(wd);
    }

    public boolean canGoPrevious() {
        return true;
    }

    public boolean canGoNext() {
        return true;
    }

    public boolean canFinish() {
        return false;
    }

    public Class getNextPanel() {
        return WizardPanelSendingProgress.class;
    }

    public void initComponents() {
        setLayout(new BorderLayout());
        FormLayout formlayout = new FormLayout(
                "4dlu, right:pref, 4dlu, fill:pref:grow",
                "min, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, "+
                "pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu");
        
        SetupMail bean = getData().getSetup().getMail();
        
        Form<SetupMail> form = new Form<SetupMail>(bean, formlayout);
        form.setBorder(Borders.DIALOG_BORDER);

        CellConstraints cc = new CellConstraints();
        int r=1;

        JTitleLabel title = new JTitleLabel(Resources.getString("wizard.mailparams.title"));
        form.add(title, cc.xyw(1,r,4));
        r+=2;

        Separator separator = new Separator(Resources.getString("wizard.mailparams.to.title"));
        form.add(separator, cc.xyw(1,r,4));
        r+=2;

        StringListModel<List<String>> toModel = new StringListModel<List<String>>(bean.getTo());
        JList to = new JList(toModel);
        to.setName("to");
        to.setVisibleRowCount(4);
        JScrollPane tosa = new JScrollPane(to,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JLabel toLabel = new JLabel(Resources.getString("wizard.mailparams.to.list.label"));
        toLabel.setLabelFor(tosa);
        to.setName("to");
        form.add(toLabel, cc.xy(2,r));
        form.add(tosa, cc.xy(4,r));
        r+=2;

        FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
        layout.setHgap(4);
        JPanel listButtons = new JPanel(layout);
        listButtons.add(new JButton(new EmailsEditAction(to, toModel, this)));
        listButtons.add(new JButton(new EmailsRemoveAction(to)));
//        listButtons.add(new JButton(new EmailsListSelectAction(to, this)));
//        listButtons.add(new JButton(new EmailsListCreateAction(to,this)));
        form.add(listButtons, cc.xy(4, r, "left, fill"));
        r+=2;

        
        separator = new Separator(Resources.getString("wizard.mailparams.content.title"));
        form.add(separator, cc.xyw(1,r,4));
        r+=2;
        
        JTextField subject = new JTextField();
        JLabel subjectLabel = new JLabel(Resources.getString("wizard.mailparams.content.subject.label"));
        subjectLabel.setLabelFor(subject);
        subject.setName("subject");
        form.add(subjectLabel, cc.xy(2,r));
        form.add(subject     , cc.xy(4,r));
        r+=2;

        JTextArea message = new JTextArea(10,40);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        JLabel messageLabel = new JLabel(Resources.getString("wizard.mailparams.content.msg.label"));
        messageLabel.setLabelFor(message);
        message.setName("message");
        JScrollPane messagesa = new JScrollPane(message,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        form.add(messageLabel, cc.xy(2,r));
        form.add(messagesa, cc.xy(4,r));
        r+=2;

        separator = new Separator(Resources.getString("wizard.mailparams.other.title"));
        form.add(separator, cc.xyw(1,r,4));
        r+=2;
        
        JCheckBox comments = new JCheckBox();
        JLabel commentsLabel = new JLabel(Resources.getString("wizard.mailparams.other.comments.flag.label"));
        commentsLabel.setLabelFor(comments);
        comments.setName("insertComments");
        form.add(comments, cc.xy(2,r));
        form.add(commentsLabel, cc.xy(4,r));
        r+=2;
        
        JSlider photoCountByMail = new JSlider();
        photoCountByMail.setPaintTicks(true);
        photoCountByMail.setPaintLabels(true);
        photoCountByMail.setMinorTickSpacing(1);
        photoCountByMail.setMajorTickSpacing(2);
        photoCountByMail.setMinimum(1);
        photoCountByMail.setMaximum(20);
        JCheckBox severalMails   = new JCheckBox();
        severalMails.setName("severalMails");
        severalMails.setAction(new JCheckBoxAction(severalMails, photoCountByMail));
        JLabel photoCountByMailLabel = new JLabel(Resources.getString("wizard.mailparams.other.several.flag.label"));
        photoCountByMailLabel.setLabelFor(severalMails);
        photoCountByMail.setName("photoCountByMail");
        form.add(severalMails, cc.xy(2,r));
        form.add(photoCountByMailLabel, cc.xy(4,r));
        form.add(photoCountByMail,      cc.xy(4,r+2));
        r+=4;
        
        add(form, BorderLayout.CENTER);
    }

    public void activate() {
    }
}






class EmailsRemoveAction extends JButtonAction {
    private JList list;
    public EmailsRemoveAction(JList list) {
        super(Resources.getString("action.remove.label"));
        putValue(JButtonAction.SMALL_ICON, Resources.createUserRemove());
        putValue(JButtonAction.SHORT_DESCRIPTION, Resources.getString("wizard.mailparams.to.action.remove.hint"));
        this.list=list;
    }
    public void actionPerformed(ActionEvent actionEvent) {
        Object[] selectedValues = list.getSelectedValues();
        StringListModel dataModel = (StringListModel)list.getModel();
        for(Object selectedValue: selectedValues) {
            dataModel.remove((String)selectedValue);
        }
    }
}



class EmailsEditAction extends JButtonAction {
    StringListModel<List<String>> toModel;
    private JList list;
    private WizardPanelMailParams wizard;
    public EmailsEditAction(JList list, StringListModel<List<String>> toModel, WizardPanelMailParams wizard) {
        super(Resources.getString("action.edit.label"));
        putValue(JButtonAction.SMALL_ICON, Resources.createUserEdit());
        putValue(JButtonAction.SHORT_DESCRIPTION, Resources.getString("wizard.mailparams.to.action.edit.hint"));
        this.list=list;
        this.wizard=wizard;
        this.toModel=toModel;
    }
    public void actionPerformed(ActionEvent actionEvent) {
        JPanel panel = new PanelMailRecipients(wizard.getData().getSetup(), toModel);
        JDialog dialog = new WizardDialog(wizard.getData().getFrame(), Resources.getString("wizard.recipients.title"), panel);
        dialog.setVisible(true);
    }
}

/*
class EmailsListCreateAction extends JButtonAction {
    private JList list;
    private WizardPanelMailParams wizard;
    public EmailsListCreateAction(JList list, WizardPanelMailParams wizard) {
        super("Créer/modifier une liste...");;
        this.list=list;
        this.wizard=wizard;
    }
    public void actionPerformed(ActionEvent actionEvent) {
    }
}


class EmailsListSelectAction extends JButtonAction {
    private JList list;
    private WizardPanelMailParams wizard;
    public EmailsListSelectAction(JList list, WizardPanelMailParams wizard) {
        super("Sélectionner une liste...");;
        this.list=list;
        this.wizard=wizard;
    }
    public void actionPerformed(ActionEvent actionEvent) {
    }
}
*/

