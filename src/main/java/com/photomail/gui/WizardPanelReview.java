/*
 * WizardPanelIntro.java
 *
 * Created on 22 février 2005, 15:30
 */

package com.photomail.gui;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.photomail.setup.Resources;
import com.photomail.wizard.WizardPanel;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Administrateur
 */
public class WizardPanelReview extends WizardPanel<WizardData> {
    JImageBrowser browser;

    public WizardPanelReview(WizardData wd) {
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
        return WizardPanelPhotoParams.class;
    }

    public void initComponents() {
        
        FormLayout formlayout = new FormLayout(
                "4dlu, fill:pref:grow, 4dlu",
                "min, 2dlu, pref, 2dlu, fill:pref:grow, 2dlu, pref, 2dlu, pref");
        //formlayout.setRowGroups(new int[][]{{3, 5, 7, 9}});

        setBorder(Borders.DIALOG_BORDER);
        setLayout(formlayout);
        CellConstraints cc = new CellConstraints();
        int r=1;

        JTitleLabel title = new JTitleLabel(Resources.getString("wizard.review.title"));
        add(title, cc.xyw(1,r,3));
        r+=2;

        Separator separator1 = new Separator(Resources.getString("wizard.review.view.title"));
        add(separator1, cc.xyw(1, r, 3));
        r+=2;

        try {
            browser = new JImageBrowser(getData().getActivities().getSelectedImageDataModel());
            add(browser, cc.xy(2, r));
            r+=2;
        } catch(IOException e) {
            JPanel error = new JPanel();
            error.add(new JLabel("CAN'T INITIALIZE PHOTO VIEWER"));
            add(error, cc.xy(2, r));
            r+=2;
        }
        
    }
    
    public void activate() {
        if (browser != null) browser.refreshImageView();
    }
}

