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
import com.photomail.setup.SetupSearch;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import com.photomail.wizard.WizardPanel;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;


/**
 *
 * @author Administrateur
 */
public class WizardPanelSearch extends WizardPanel<WizardData> {
    private boolean firstStart=true;
    private Form<SetupSearch> form;
    StringListModel listModel;

    public WizardPanelSearch(WizardData wd) {
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
        return WizardPanelSelect.class;
    }

    public void nextAsked() {
        if (firstStart || form.getChangeOccuredStatus() || listModel.getChangeOccuredStatus()) {
            getData().getActivities().startImageFinder();
            firstStart=false;
        }
    }

    public void initComponents() {
        setPreferredSize(new Dimension(640,480));
        
        setLayout(new BorderLayout());
        FormLayout formlayout = new FormLayout(
                "4dlu, right:pref, 4dlu, left:pref:grow, 4dlu",
                "min, 2dlu, pref, 2dlu, fill:pref, 2dlu, pref, 4dlu,"+
                "pref,    2dlu, pref, pref,    2dlu, pref, pref,    2dlu, pref, pref,    2dlu, pref, pref,    2dlu, pref, pref,   2dlu, pref, pref,   2dlu, pref, pref,   pref");
        
        SetupSearch bean = getData().getSetup().getSearch();

        form = new Form<SetupSearch>(bean, formlayout);
        form.setBorder(Borders.DIALOG_BORDER);

        CellConstraints cc = new CellConstraints();
        int r=1;

        JTitleLabel title = new JTitleLabel(Resources.getString("wizard.search.title"));
        form.add(title, cc.xyw(1,r,5));
        r+=2;

        Separator separator = new Separator(Resources.getString("wizard.search.dirlist.title"));
        form.add(separator, cc.xyw(1,r,4));
        r+=2;

        listModel = new StringListModel<List<String>>(getData().getSetup().getSearch().getDirectories());
        JList list = new JList(listModel);
        list.setName("directories");
        list.setVisibleRowCount(4);
        JScrollPane sa = new JScrollPane(list);
        form.add(sa, cc.xy(4,r, cc.FILL, cc.FILL));
        r+=2;
        
        GridLayout layout = new GridLayout(1,2);
        layout.setHgap(4);
        JPanel listButtons = new JPanel(layout);
        JButton add    = new JButton(new DirectoriesAddAction(list, this));
        JButton remove = new JButton(new DirectoriesRemoveAction(list));
        listButtons.add(add);
        listButtons.add(remove);
        form.add(listButtons, cc.xy(4, r));
        r+=2;
        
        separator = new Separator(Resources.getString("wizard.search.filter.title"));
        form.add(separator, cc.xyw(1,r,4));
        r+=2;

        JCheckBox startWithFlag = new JCheckBox();
        JLabel startWithLabel = new JLabel(Resources.getString("wizard.search.filter.startwith.label"));
        JTextField startWith = new JTextField(16);
        startWithFlag.setAction(new JCheckBoxAction(startWithFlag, startWith));
        startWith.setName("startWith");
        startWithFlag.setName("startWithFlag");
        startWithLabel.setLabelFor(startWithFlag);
        form.add(startWithFlag, cc.xy(2,r));
        form.add(startWithLabel, cc.xy(4,r));
        r+=1;
        form.add(startWith, cc.xy(4,r));
        r+=2;

        JCheckBox keywordsFlag = new JCheckBox();
        JLabel keywordsLabel = new JLabel(Resources.getString("wizard.search.filter.keywords.label"));
        JTextField keywords = new JTextField(48);
        keywordsFlag.setAction(new JCheckBoxAction(keywordsFlag, keywords));
        keywords.setName("keywords");
        keywordsFlag.setName("keywordsFlag");
        keywordsLabel.setLabelFor(keywordsFlag);
        form.add(keywordsFlag, cc.xy(2,r));
        form.add(keywordsLabel, cc.xy(4,r));
        r+=1;
        form.add(keywords, cc.xy(4,r));
        r+=2;

        
        JCheckBox commentsKeywordsFlag = new JCheckBox();
        JLabel commentsKeywordsLabel = new JLabel(Resources.getString("wizard.search.filter.commentsKeywords.label"));
        JTextField commentsKeywords = new JTextField(48);
        commentsKeywordsFlag.setAction(new JCheckBoxAction(commentsKeywordsFlag, commentsKeywords));
        commentsKeywords.setName("commentsKeywords");
        commentsKeywordsFlag.setName("commentsKeywordsFlag");
        commentsKeywordsLabel.setLabelFor(commentsKeywordsFlag);
        form.add(commentsKeywordsFlag, cc.xy(2,r));
        form.add(commentsKeywordsLabel, cc.xy(4,r));
        r+=1;
        form.add(commentsKeywords, cc.xy(4,r));
        r+=2;

        
        JCheckBox notOlderFlag = new JCheckBox();
        JLabel notOlderLabel = new JLabel(Resources.getString("wizard.search.filter.notOlder.label"));
        JComboBox notOlder = new JI18NComboBox("enum.notolder.");
        notOlderFlag.setAction(new JCheckBoxAction(notOlderFlag, notOlder));
        notOlder.setName("notOlder");
        notOlderFlag.setName("notOlderFlag");
        notOlderLabel.setLabelFor(startWithFlag);
        form.add(notOlderFlag, cc.xy(2,r));
        form.add(notOlderLabel, cc.xy(4,r));
        r+=1;
        form.add(notOlder, cc.xy(4,r));
        r+=2;


        JCheckBox alreadySentFlag = new JCheckBox();
        JLabel alreadySentLabel = new JLabel(Resources.getString("wizard.search.filter.alreadysent.label"));
        JComboBox alreadySent = new JI18NComboBox("enum.notolder.");
        alreadySentFlag.setAction(new JCheckBoxAction(alreadySentFlag, alreadySent));
        alreadySent.setName("alreadySent");
        alreadySentFlag.setName("alreadySentFlag");
        alreadySentLabel.setLabelFor(startWithFlag);
        form.add(alreadySentFlag, cc.xy(2,r));
        form.add(alreadySentLabel, cc.xy(4,r));
        r+=1;
        form.add(alreadySent, cc.xy(4,r));
        r+=2;

        
        JCheckBox addedAfterFlag = new JCheckBox();
        JLabel addedAfterLabel = new JLabel(Resources.getString("wizard.search.filter.addedafter.label"));
        JFormattedTextField addedAfter = new JFormattedTextField(new SimpleDateFormat(Resources.getString("wizard.search.dateformat")));
        //addedAfter.setInputVerifier(new FormattedTextFieldVerifier());
        addedAfterFlag.setAction(new JCheckBoxAction(addedAfterFlag, addedAfter));
        addedAfter.setColumns(12);
        addedAfter.setName("addedAfter");
        addedAfterFlag.setName("addedAfterFlag");
        addedAfterLabel.setLabelFor(addedAfterFlag);
        form.add(addedAfterFlag, cc.xy(2,r));
        form.add(addedAfterLabel, cc.xy(4,r));
        r+=1;
        form.add(addedAfter, cc.xy(4,r));
        r+=2;


        JCheckBox addedBeforeFlag = new JCheckBox();
        JLabel addedBeforeLabel = new JLabel(Resources.getString("wizard.search.filter.addedbefore.label"));
        JFormattedTextField addedBefore = new JFormattedTextField(new SimpleDateFormat(Resources.getString("wizard.search.dateformat")));
        addedBeforeFlag.setAction(new JCheckBoxAction(addedBeforeFlag, addedBefore));
        addedBefore.setColumns(12);
        addedBefore.setName("addedBefore");
        addedBeforeFlag.setName("addedBeforeFlag");
        addedBeforeLabel.setLabelFor(addedBeforeFlag);
        form.add(addedBeforeFlag, cc.xy(2,r));
        form.add(addedBeforeLabel, cc.xy(4,r));
        r+=1;
        form.add(addedBefore, cc.xy(4,r));
        r+=2;

        
        add(form, BorderLayout.CENTER);
    }
    
    public void activate() {
        form.resetChangeOccuredStatus();
        listModel.resetChangeOccuredStatus();
    }
}



class DirectoriesRemoveAction extends JButtonAction {
    private JList list;
    public DirectoriesRemoveAction(JList list) {
        super(Resources.getString("action.remove.label"));
        putValue(JButtonAction.SMALL_ICON, Resources.createDirRemove());
        putValue(JButtonAction.SHORT_DESCRIPTION, Resources.getString("wizard.search.dirlist.remove.hint"));
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



class DirectoriesAddAction extends JButtonAction {
    static File previousSelectedLastDirectory;
    private JList list;
    private JComponent component;
    public DirectoriesAddAction(JList list, JComponent component) {
        super(Resources.getString("action.add.label"));
        putValue(JButtonAction.SMALL_ICON, Resources.createDirAdd());
        putValue(JButtonAction.SHORT_DESCRIPTION, Resources.getString("wizard.search.dirlist.add.hint"));
        this.list=list;
        this.component=component;
    }
    public void actionPerformed(ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new DirectoriesAddActionFileFilter());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (previousSelectedLastDirectory != null) {
            fileChooser.setCurrentDirectory(previousSelectedLastDirectory);
        }
        int result = fileChooser.showOpenDialog(component);

        if (result == JFileChooser.APPROVE_OPTION) {
            StringListModel dataModel = (StringListModel)list.getModel();
            for(File selectedFile:fileChooser.getSelectedFiles()) {
                dataModel.add(selectedFile.getAbsolutePath());
            }
        }
        previousSelectedLastDirectory = fileChooser.getCurrentDirectory();
    }
}


class DirectoriesAddActionFileFilter extends FileFilter {
    public DirectoriesAddActionFileFilter() {
    }

    public boolean accept(File file) {
        return file.isDirectory();
    }

    public String getDescription() {
        return Resources.getString("wizard.search.dirlist.filter.label");
    }
}








/*
class FormattedTextFieldVerifier extends InputVerifier {
     public boolean verify(JComponent input) {
         if (input instanceof JFormattedTextField) {
             JFormattedTextField ftf = (JFormattedTextField)input;
             AbstractFormatter formatter = ftf.getFormatter();
             if (formatter != null) {
                 String text = ftf.getText();
                 try {
                      formatter.stringToValue(text);
                      ftf.setForeground(Color.BLACK);
                      return true;
                  } catch (ParseException pe) {
                      ftf.setForeground(Color.RED);
                      return false;
                  }
              }
          }
          return true;
      }
      public boolean shouldYieldFocus(JComponent input) {
          return verify(input);
      }
  }
*/