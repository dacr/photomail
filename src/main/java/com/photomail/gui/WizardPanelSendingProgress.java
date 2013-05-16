/*
 * WizardPanelPhotoParams.java
 *
 * Created on 22 février 2005, 15:30
 */

package com.photomail.gui;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.photomail.image.ImageInfo;
import com.photomail.mail.MailActivity;
import com.photomail.mail.MailActivityEvent;
import com.photomail.mail.MailActivityListener;
import com.photomail.setup.ApplicationProperties;
import com.photomail.setup.Resources;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import com.photomail.wizard.WizardPanel;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;


/**
 *
 * @author Administrateur
 */
public class WizardPanelSendingProgress extends WizardPanel<WizardData> implements MailActivityListener {
    MailActivity activity;
    JTextComponent message;
    JProgressBar preparingProgressBar;
    JProgressBar sendingProgressBar;
    JImageMosaic   mosaic;
    ImageDataModel mosaicDataModel;
    JButton startButton, stopButton, expertButton;

    private boolean success=false;
    
    public WizardPanelSendingProgress(WizardData wd) {
        super(wd);
    }

    public boolean canGoPrevious() {
        return true;
    }

    public boolean canGoNext() {
        return false;
    }

    public boolean canFinish() {
        return success;
    }

    public Class getNextPanel() {
        return null;
    }

    public void initComponents() {

        FormLayout formlayout = new FormLayout(
                "4dlu, right:pref, 4dlu, fill:pref:grow",
                "min, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu,"+
                "fill:pref:grow, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu");
        formlayout.setRowGroups(new int[][]{{3}});

        setBorder(Borders.DIALOG_BORDER);
        setLayout(formlayout);

        CellConstraints cc = new CellConstraints();
        int r=1;

        JTitleLabel title = new JTitleLabel(Resources.getString("wizard.sending.title"));
        add(title, cc.xyw(1,r,4));
        r+=2;

        Separator separator1 = new Separator(Resources.getString("wizard.sending.ctrl.title"));
        add(separator1, cc.xyw(1, r, 4));
        r+=2;
        
        GridLayout layout = new GridLayout(1,3);
        layout.setHgap(4);
        JPanel buttons = new JPanel(layout);
        buttons.add(startButton  = new JButton(new PanelSendingStartAction(this)));
        buttons.add(stopButton   = new JButton(new PanelSendingStopAction(this)));
        buttons.add(expertButton = new JButton(new PanelSendingExpertAction(this)));
        add(buttons, cc.xy(4,r, "left, fill"));
        r+=2;

        Separator separator2 = new Separator(Resources.getString("wizard.sending.progress.title"));
        add(separator2, cc.xyw(1, r, 4));
        r+=2;

        preparingProgressBar = new JProgressBar(0, 100);
        preparingProgressBar.setStringPainted(true);
        JLabel preparingProgressBarLabel = new JLabel(Resources.getString("wizard.sending.progress.prepare.label"));
        preparingProgressBarLabel.setLabelFor(preparingProgressBar);
        add(preparingProgressBarLabel,      cc.xy(2,r));
        add(preparingProgressBar,           cc.xy(4,r));
        r+=2;

        sendingProgressBar = new JProgressBar(0, 100);
        sendingProgressBar.setStringPainted(true);
        JLabel sendingProgressBarLabel = new JLabel(Resources.getString("wizard.sending.progress.sending.label"));
        sendingProgressBarLabel.setLabelFor(sendingProgressBar);
        add(sendingProgressBarLabel,      cc.xy(2,r));
        add(sendingProgressBar,      cc.xy(4,r));
        r+=2;
        
        JTextArea message = new JTextArea(3, 20);
        this.message = message;
        message.setEditable(false);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        JScrollPane sa = new JScrollPane(message, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        message.setText(Resources.getString("wizard.sending.msg.wait"));
        JLabel messageLabel = new JLabel(Resources.getString("wizard.sending.progress.msg.label"));
        add(messageLabel, cc.xy(2,r));
        add(sa, cc.xy(4,r));
        r+=2;
        
        JLabel mosaicLabel = new JLabel(Resources.getString("wizard.sending.progress.details.label"));
        mosaicDataModel = new ImageDataModel();
        mosaic = new JImageMosaic(mosaicDataModel, true);
        JScrollPane sa2 = new JScrollPane(mosaic);
        add(mosaicLabel, cc.xy(2,r));
        add(sa2, cc.xy(4, r));
        r+=2;

        Separator separator3 = new Separator(Resources.getString("wizard.sending.about.label"));
        add(separator3, cc.xyw(1, r, 4));
        r+=2;

        JTextFieldLabel version = new JTextFieldLabel(Resources.getString("wizard.sending.about.version"));
        JLabel versionLabel = new JLabel(Resources.getString("wizard.sending.about.version.label"));
        add(versionLabel, cc.xy(2,r));
        add(version, cc.xy(4, r));
        r+=2;

        JTextFieldLabel copyright = new JTextFieldLabel(Resources.getString("wizard.sending.about.copyright"));
        JLabel copyrightLabel = new JLabel(Resources.getString("wizard.sending.about.copyright.label"));
        add(copyrightLabel, cc.xy(2,r));
        add(copyright, cc.xy(4, r));
        r+=2;

        ApplicationProperties app = ApplicationProperties.getInstance();
        String url = app.getProductUrl();
        JTextFieldLabel homepage = new JTextFieldLabel(url);
        JLabel homepageLabel = new JLabel(Resources.getString("wizard.sending.about.site.label"));
        add(homepageLabel, cc.xy(2,r));
        add(homepage, cc.xy(4, r));
        r+=2;

        String email = app.getProductEmail();
        JTextFieldLabel contact = new JTextFieldLabel(email);
        JLabel contactLabel = new JLabel(Resources.getString("wizard.sending.about.contact.label"));
        add(contactLabel, cc.xy(2,r));
        add(contact, cc.xy(4, r));
        r+=2;

        stopButtonImpacts();
    }
    
    public void activate() {
    }

    public void startActivity() {
        if (activity != null && activity.isAlive()) return;
        startButtonImpacts();
        activity = new MailActivity(getData().getSetup(), getData().getActivities().getSelectedImageDataModel().getData());
        activity.addListener(this);
        activity.start();
    }
    
    public void stopActivity() {
        if (activity  != null) {
            while(activity.isAlive()) {
                activity.interrupt();
                try {
                    Thread.sleep(500);
                } catch(InterruptedException e) {
                    break;
                }
            }
        }
    }
    
    public void cancelAsked() {
        if (activity  != null) {
            activity.interrupt();
        }
    }

    public void startButtonImpacts() {
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        expertButton.setEnabled(false);        
    }
    
    public void stopButtonImpacts() {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        expertButton.setEnabled(true);                
    }
    


    public void reset() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                preparingProgressBar.setValue(0);
                sendingProgressBar.setValue(0);
                success=false;
                notifyListeners();
                mosaic.setAnimateLast(true);
            }
        });
    }

    public void setPreparingProgress(final int i) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                preparingProgressBar.setValue(i);
            }
        });
    }
    public void clearMosaic() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                List<ImageInfo> tmp = new ArrayList<ImageInfo>();
                tmp.addAll(mosaicDataModel.getData());
                for(ImageInfo ii: tmp) {
                    mosaicDataModel.remove(ii);
                }
            }
        });
    }
    public void addImage2Mosaic(final ImageInfo imageInfo) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mosaicDataModel.add(imageInfo);
            }
        });
    }

    public void setSendingProgress(final int i) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                sendingProgressBar.setValue(i);
            }
        });
    }
    public void setMessage(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                message.setForeground(Color.BLACK);
                message.setText(msg);
            }
        });
    }
    public void setErrorMessage(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                message.setForeground(Color.RED);
                message.setText(msg);
            }
        });
    }
    public void threadEnd() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                stopButtonImpacts();
                mosaic.setAnimateLast(false);
            }
        });
    }
    public void advertise() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                notifyListeners();
            }
        });        
    }

    public void progressMade(MailActivityEvent event) {
        switch(event.getType()) {
            case STARTING:
                clearMosaic();
                reset();
                setMessage(Resources.getString("wizard.sending.msg.start"));
                break;
            case PREPARING_IMAGE:
                addImage2Mosaic(event.getImageInfo());
                setMessage(String.format(Resources.getString("wizard.sending.msg.preparingimage"), event.getImageInfo().getName()));
                break;
            case PREPARING_IN_PROGRESS:
                setPreparingProgress((event.getPrepareImageCount()-1)*100/event.getPrepareImageTotal());
                break;
            case PREPARING_FINISHED:
                setPreparingProgress(100);
                clearMosaic();
                break;
            case SENDING_IMAGE:
                addImage2Mosaic(event.getImageInfo());
                break;
            case SENDING_IN_PROGRESS:
                setSendingProgress((event.getMailCount()-1)*100/event.getMailTotal());
                setMessage(String.format(Resources.getString("wizard.sending.msg.sendingprogress"), event.getMailCount(), event.getMailTotal()));
                break;
            case ERROR_CONNECTION:
            case ERROR_LOGIN:
            case ERROR:
                setErrorMessage(event.getError());
                break;
            case INTERRUPTED:
                reset();
                setErrorMessage(Resources.getString("wizard.sending.msg.interrupted")); // TODO
                break;
            case SUCCESS:
                setSendingProgress(100);
                setMessage(Resources.getString("wizard.sending.msg.success")); // TODO
                success=true;
                advertise();
                break;
            case THREAD_END:
                threadEnd();
                break;
        }
    }
}






class PanelSendingStartAction extends JButtonAction {
    private WizardPanelSendingProgress wizard;
    public PanelSendingStartAction(WizardPanelSendingProgress wizard) {
        super(Resources.getString("wizard.sending.action.send.label"));
        putValue(Action.SHORT_DESCRIPTION, Resources.getString("wizard.sending.action.send.hint"));
        putValue(Action.SMALL_ICON, Resources.createIconSend());
        this.wizard=wizard;
    }
    public void actionPerformed(ActionEvent actionEvent) {
        wizard.startActivity();
    }
}



class PanelSendingStopAction extends JButtonAction {
    private WizardPanelSendingProgress wizard;
    public PanelSendingStopAction(WizardPanelSendingProgress wizard) {
        super(Resources.getString("wizard.sending.action.stop.label"));
        putValue(Action.SHORT_DESCRIPTION, Resources.getString("wizard.sending.action.stop.hint"));
        putValue(Action.SMALL_ICON, Resources.createIconStop());
        this.wizard=wizard;
    }
    public void actionPerformed(ActionEvent actionEvent) {
        wizard.clearMosaic();
        wizard.stopActivity();
    }
}



class PanelSendingExpertAction extends JButtonAction {
    private WizardPanelSendingProgress wizard;
    public PanelSendingExpertAction(WizardPanelSendingProgress wizard) {
        super(Resources.getString("wizard.sending.action.servercfg.label"));
        putValue(Action.SHORT_DESCRIPTION, Resources.getString("wizard.sending.action.servercfg.hint"));
        putValue(Action.SMALL_ICON, Resources.createIconConfig());
        this.wizard=wizard;
    }
    public void actionPerformed(ActionEvent actionEvent) {
        JPanel panel = new PanelMailServerParameters(wizard.getData());
        JDialog dialog = new WizardDialog(wizard.getData().getFrame(), Resources.getString("wizard.servercfg.title"), panel);
        dialog.setVisible(true);
    }
}

