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
import com.photomail.wizard.WizardPanel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import javax.swing.JSlider;
import com.photomail.setup.SetupPhotoProcessing;
import javax.swing.JScrollPane;

/**
 *
 * @author Administrateur
 */
public class WizardPanelPhotoParams extends WizardPanel<WizardData> {
    public WizardPanelPhotoParams(WizardData wd) {
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
        return WizardPanelMailParams.class;
    }

    public void initComponents() {
        setLayout(new BorderLayout());
        FormLayout formlayout = new FormLayout(
                "4dlu, right:pref, 4dlu, fill:pref:grow, 4dlu",
                "min, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, fill:min:grow, min");
        //formlayout.setRowGroups(new int[][]{{3, 5, 7, 9, 11, 13}});
        
        SetupPhotoProcessing bean = getData().getSetup().getPhotoProcessing();
        
        Form<SetupPhotoProcessing> form = new Form<SetupPhotoProcessing>(bean, formlayout);
        form.setBorder(Borders.DIALOG_BORDER);

        CellConstraints cc = new CellConstraints();
        int r=1;

        JTitleLabel title = new JTitleLabel(Resources.getString("wizard.photoparams.title"));
        form.add(title, cc.xyw(1,r,4));
        r+=2;

        Separator separator = new Separator(Resources.getString("wizard.photoparams.compression.title"));
        form.add(separator, cc.xyw(1,r,4));
        r+=2;
    
        JSlider level = new JSlider();
        level.setToolTipText(Resources.getString("wizard.photoparams.compression.hint"));
        level.setPaintTicks(true);
        level.setPaintLabels(true);
        level.setMinorTickSpacing(5);
        level.setMajorTickSpacing(10);
        level.setMinimum(30);
        level.setMaximum(100);
        JLabel levelLabel = new JLabel(Resources.getString("wizard.photoparams.compression.level.label"));
        levelLabel.setLabelFor(level);
        level.setName("compression");
        form.add(levelLabel, cc.xy(2,r));
        form.add(level,      cc.xy(4,r));
        r+=2;
        
        separator = new Separator(Resources.getString("wizard.photoparams.resize.title"));
        form.add(separator, cc.xyw(1,r,4));
        r+=2;
        
        JComboBox size = new JI18NComboBox("enum.photosizes.");
        size.setToolTipText(Resources.getString("wizard.photoparams.resize.hint"));
        JLabel sizeLabel = new JLabel(Resources.getString("wizard.photoparams.resize.maxsize.label"));
        sizeLabel.setLabelFor(size);
        size.setName("size");
        form.add(sizeLabel, cc.xy(2,r));
        form.add(size     , cc.xy(4,r));
        r+=2;

        separator = new Separator(Resources.getString("wizard.photoparams.choice.title"));
        form.add(separator, cc.xyw(1,r,4));
        r+=2;

        JImageMosaic mosaic = new JImageMosaic(getData().getActivities().getSelectedImageDataModel());
        JLabel mosaicLabel = new JLabel(Resources.getString("wizard.photoparams.choice.mosaic.label"));
        JScrollPane sa = new JScrollPane(mosaic, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        form.add(mosaicLabel, cc.xy(2, r));
        form.add(sa, cc.xy(4, r));
        r+=2;
        
        add(form, BorderLayout.CENTER);
    }
    
    public void activate() {
        
    }
}
