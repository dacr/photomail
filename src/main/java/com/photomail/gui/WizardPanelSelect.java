/*
 * WizardPanelIntro.java
 *
 * Created on 22 février 2005, 15:30
 */

package com.photomail.gui;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.photomail.image.ImageFinderProgress;
import com.photomail.image.ImageFinderProgressListener;
import com.photomail.image.ImageInfo;
import com.photomail.image.ThumbnailsGeneratorProgress;
import com.photomail.image.ThumbnailsGeneratorProgressListener;
import com.photomail.setup.Resources;
import com.photomail.wizard.WizardPanel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;


/**
 *
 * @author Administrateur
 */
public class WizardPanelSelect extends WizardPanel<WizardData> implements ImageFinderProgressListener, ThumbnailsGeneratorProgressListener {
    static final private long REFRESH_TEMPO=150l;
    private JLabel searchProgress;
    private JLabel thumbnailProgress;
    
    public WizardPanelSelect(WizardData wd) {
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
        return WizardPanelReview.class;
    }

    public void initComponents() {
        FormLayout formlayout = new FormLayout(
                "4dlu, fill:100dlu:grow, 4dlu, center:pref, 4dlu, fill:100dlu:grow, 4dlu",
                "min, 2dlu, pref, 2dlu, fill:pref:grow, center:pref, fill:pref:grow, 2dlu, fill:pref, 2dlu, fill:pref, 2dlu, fill:pref, min");

        setBorder(Borders.DIALOG_BORDER);
        setLayout(formlayout);
        CellConstraints cc = new CellConstraints();
        int r=1;

        JTitleLabel title = new JTitleLabel(Resources.getString("wizard.select.title"));
        add(title, cc.xyw(1,r,7));
        r+=2;

        Separator sub1 = new Separator(Resources.getString("wizard.select.available.label"));
        add(sub1, cc.xyw(2, r, 1));
        Separator sub2 = new Separator(Resources.getString("wizard.select.selected.label"));
        add(sub2, cc.xyw(6, r, 1));
        r+=2;
        
        JImageTable availableImages = new JImageTable(getData().getActivities().getAvailableImageDataModel());
        JScrollPane ailjsa = new JScrollPane(availableImages);
        add(ailjsa, cc.xywh(2, r, 1, 3));
        
        JImageTable selectedImages = new JImageTable(getData().getActivities().getSelectedImageDataModel());
        JScrollPane silsa = new JScrollPane(selectedImages);
        add(silsa, cc.xywh(6, r, 1, 3));

        GridLayout buttonsLayout = new GridLayout(4,1);
        buttonsLayout.setVgap(4);
        JPanel buttons = new JPanel(buttonsLayout);
        buttons.add(new JButton(new AddAllAction(this, availableImages, selectedImages)));
        buttons.add(new JButton(new AddAction(this, availableImages, selectedImages)));
        buttons.add(new JButton(new RemoveAction(this, availableImages, selectedImages)));
        buttons.add(new JButton(new RemoveAllAction(this, availableImages, selectedImages)));
        add(buttons, cc.xy(4, r+1));
        r+=6;
        
        searchProgress = new JLabel();
        add(searchProgress, cc.xyw(2,r, 5));
        getData().getActivities().getImageFinder().addProgressListener(this);
        r+=2;
        
        thumbnailProgress = new JLabel();
        add(thumbnailProgress, cc.xyw(2,r, 5));
        getData().getActivities().getThumbnailsGenerator().addProgressListener(this);
        r+=2;
     
    }
    
    public void activate() {
    }

    private long lastImageFinderProgressRefreshDone = 0;
    public void progressMade(ImageFinderProgress progress) {
        final ImageFinderProgress fprogress = progress;
        if (progress.isFinished()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String msg = String.format(Resources.getString("wizard.select.search.finish"), fprogress.getCount());
                    searchProgress.setText(msg);                    
                }
            });
        } else {
            if (System.currentTimeMillis() - lastImageFinderProgressRefreshDone < REFRESH_TEMPO) {
                return;
            } else {
                lastImageFinderProgressRefreshDone = System.currentTimeMillis();
            }
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String msg = String.format(Resources.getString("wizard.select.search.inprogress"), fprogress.getCount(), fprogress.getDirectoryBeingExplored());
                    searchProgress.setText(msg);
                }
            });
        }
    }

    private long lastThumbnailsGeneratorProgressRefreshDone = 0;
    public void progressMade(ThumbnailsGeneratorProgress progress) {
        final ThumbnailsGeneratorProgress fprogress = progress;
        if (progress.isFinished()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String msg = String.format(Resources.getString("wizard.select.thumnails.finish"));
                    thumbnailProgress.setText(msg);
                }
            });
        } else {
            if (System.currentTimeMillis() - lastThumbnailsGeneratorProgressRefreshDone < REFRESH_TEMPO) {
                return;
            } else {
                lastThumbnailsGeneratorProgressRefreshDone = System.currentTimeMillis();
            }
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String msg = String.format(Resources.getString("wizard.select.thumnails.inprogress"), fprogress.getProcessedCount(), fprogress.getTotalToProcess(), fprogress.getImageProcessed().getName());
                    thumbnailProgress.setText(msg);
                }
            });
        }
    }
}




class AddAllAction extends AddAction {
    public AddAllAction(WizardPanel<WizardData> wizard, JTable source, JTable destination) {
        super(wizard, Resources.getString("wizard.select.action.selectall.label"), source, destination);
        putValue(JButtonAction.SHORT_DESCRIPTION, Resources.getString("wizard.select.action.selectall.hint"));
    }
    public void actionPerformed(ActionEvent actionEvent) {
        ImageDataModel sourceModel = wizard.getData().getActivities().getAvailableImageDataModel();
        ImageDataModel destinationModel = wizard.getData().getActivities().getSelectedImageDataModel();
        if (sourceModel.getSize()==0) return;
        source.getSelectionModel().setSelectionInterval(0, sourceModel.getSize()-1);
        super.actionPerformed(actionEvent);
    }
}


class AddAction extends JButtonAction {
    protected JTable source, destination;
    protected WizardPanel<WizardData> wizard;
    public AddAction(WizardPanel<WizardData> wizard, String name, JTable source, JTable destination) {
        super(name);
        this.source = source;
        this.destination = destination;
        this.wizard = wizard;
    }
    public AddAction(WizardPanel<WizardData> wizard, JTable source, JTable destination) {
        this(wizard, Resources.getString("wizard.select.action.select.label"), source, destination);
        putValue(JButtonAction.SHORT_DESCRIPTION, Resources.getString("wizard.select.action.select.hint"));
    }
    protected void operate(int[] selection) {
        if (selection.length==0) return;
        ImageDataModel sourceModel = wizard.getData().getActivities().getAvailableImageDataModel();
        ImageDataModel destinationModel = wizard.getData().getActivities().getSelectedImageDataModel();
        List<ImageInfo> toRemove = new ArrayList<ImageInfo>();
        final int first = destinationModel.getSize();
        final int last = first+selection.length-1;
        for(int index:selection) {
            ImageInfo o = sourceModel.get(index);
            toRemove.add(o);
            destinationModel.add(o);
        }
        for(ImageInfo o : toRemove) {
            sourceModel.remove(o);
        }
        // Obligatoire afin que le changement de sélection se fasse sur les tables mises à jour
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                source.getSelectionModel().clearSelection();
                destination.getSelectionModel().clearSelection();
                destination.getSelectionModel().setSelectionInterval(first, last);
            }
        });
    }
    public void actionPerformed(ActionEvent actionEvent) {
        operate(source.getSelectedRows());
    }
}

class RemoveAction extends JButtonAction {
    protected JTable source, destination;
    protected WizardPanel<WizardData> wizard;
    public RemoveAction(WizardPanel<WizardData> wizard, String name, JTable source, JTable destination) {
        super(name);
        this.source = source;
        this.destination = destination;
        this.wizard = wizard;
    }
    public RemoveAction(WizardPanel<WizardData> wizard, JTable source, JTable destination) {
        this(wizard, Resources.getString("wizard.select.action.unselect.label"), source, destination);
        putValue(JButtonAction.SHORT_DESCRIPTION, Resources.getString("wizard.select.action.unselect.hint"));
    }
    protected void operate(int[] selection) {
        if (selection.length==0) return;
        ImageDataModel sourceModel = wizard.getData().getActivities().getAvailableImageDataModel();
        ImageDataModel destinationModel = wizard.getData().getActivities().getSelectedImageDataModel();
        List<ImageInfo> toRemove = new ArrayList<ImageInfo>();
        final int first = sourceModel.getSize();
        final int last = first+selection.length-1;
        for(int index:selection) {
            ImageInfo o = destinationModel.get(index);
            toRemove.add(o);
            sourceModel.add(o);
        }
        for(ImageInfo o : toRemove) {
            destinationModel.remove(o);
        }
        // Obligatoire afin que le changement de sélection se fasse sur les tables mises à jour
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                source.getSelectionModel().clearSelection();
                destination.getSelectionModel().clearSelection();
                source.getSelectionModel().setSelectionInterval(first, last);
            }
        });        
    }
    public void actionPerformed(ActionEvent actionEvent) {
        operate(destination.getSelectedRows());
    }
}

class RemoveAllAction extends RemoveAction {
    public RemoveAllAction(WizardPanel<WizardData> wizard, JTable source, JTable destination) {
        super(wizard, Resources.getString("wizard.select.action.unselectall.label"), source, destination);
        putValue(JButtonAction.SHORT_DESCRIPTION, Resources.getString("wizard.select.action.unselectall.hint"));
    }
    public void actionPerformed(ActionEvent actionEvent) {
        ImageDataModel sourceModel = wizard.getData().getActivities().getAvailableImageDataModel();
        ImageDataModel destinationModel = wizard.getData().getActivities().getSelectedImageDataModel();
        if (destinationModel.getSize()==0) return;
        destination.getSelectionModel().setSelectionInterval(0, destinationModel.getSize()-1);
        super.actionPerformed(actionEvent);
    }
}


