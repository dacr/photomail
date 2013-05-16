/*
 * Activities.java
 *
 * Created on 5 avril 2005, 22:39
 */

package com.photomail.gui;

import com.photomail.image.ImageFinder;
import com.photomail.image.ThumbnailsGenerator;

import com.photomail.setup.Setup;

/**
 *
 * @author dcr
 */
public class Activities {
    private Setup                setup;
    private ImageFinder          imageFinder;
    private ThumbnailsGenerator  thumbnailsGenerator;
    private Thread               imageFinderThread;
    private Thread               thumbnailsGeneratorThread;
    private ImageDataModel       availableImageDataModel;
    private ImageDataModel       selectedImageDataModel;
    
    /** Creates a new instance of Activities */
    public Activities(Setup setup) {
        this.setup                   = setup;
        this.imageFinder             = new ImageFinder(setup);
        this.thumbnailsGenerator     = new ThumbnailsGenerator(setup);
        this.selectedImageDataModel  = new ImageDataModel();
        // Bad hack to avoid duplicate photos between chosen and available photo when a new search occured
        this.availableImageDataModel = new ImageDataModel(selectedImageDataModel);
        this.imageFinder.addImageListener(thumbnailsGenerator);
        this.imageFinder.addImageListener(availableImageDataModel);
    }

    boolean imageFinderStarted=false;

    /*
     * Démarre ou redémarre...
     */
    public void startImageFinder() {
        if (imageFinderStarted==true) {
            if (!stopImageFinder()) {
                return;
            }
        }
        thumbnailsGeneratorThread = new Thread(thumbnailsGenerator);
        thumbnailsGeneratorThread.start();

        imageFinderThread = new Thread(imageFinder);
        imageFinderThread.start();

        imageFinderStarted=true;
    }
    
    /**
     * returns true  : On a demandé à ce qu'ils soient arrêté et ils le sont
     *         false : On a demandé à ce qu'ils soient arrêté mais on ne sait pas ils le sont
     *                 car on nous a interrompu
     */
    public boolean stopImageFinder() {
        if (imageFinderThread  != null && imageFinderThread.isAlive())
            imageFinderThread.interrupt();
        if (thumbnailsGeneratorThread  != null && thumbnailsGeneratorThread.isAlive())
            thumbnailsGeneratorThread.interrupt();
        // Attendre qu'ils s'arrêtent proprement
        imageFinderStarted=false;
        while( (imageFinderThread != null && imageFinderThread.isAlive()) || 
               (thumbnailsGeneratorThread != null && thumbnailsGeneratorThread.isAlive())) {
            try {
                Thread.sleep(50);
            } catch(InterruptedException e) {
                return false;
            }
        }
        availableImageDataModel.reset();
        return true;
        
    }
    public void stopAll() {
        stopImageFinder();
    }

    public ImageFinder getImageFinder() {
        return imageFinder;
    }

    public void setImageFinder(ImageFinder imageFinder) {
        this.imageFinder = imageFinder;
    }

    public ThumbnailsGenerator getThumbnailsGenerator() {
        return thumbnailsGenerator;
    }

    public void setThumbnailsGenerator(ThumbnailsGenerator thumbnailsGenerator) {
        this.thumbnailsGenerator = thumbnailsGenerator;
    }

    public ImageDataModel getAvailableImageDataModel() {
        return availableImageDataModel;
    }

    public void setAvailableImageDataModel(ImageDataModel availableImageDataModel) {
        this.availableImageDataModel = availableImageDataModel;
    }

    public ImageDataModel getSelectedImageDataModel() {
        return selectedImageDataModel;
    }

    public void setSelectedImageDataModel(ImageDataModel selectedImageDataModel) {
        this.selectedImageDataModel = selectedImageDataModel;
    }
}
