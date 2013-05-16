/*
 * ThumbnailsGeneratorProgress.java
 *
 * Created on 10 avril 2005, 11:12
 */

package com.photomail.image;

/**
 *
 * @author dcr
 */
public class ThumbnailsGeneratorProgress {
    private ImageInfo imageProcessed;
    private int       processedCount;
    private int       totalToProcess;
    private boolean   finished;
    
    /** Creates a new instance of ThumbnailsGeneratorProgress */
    public ThumbnailsGeneratorProgress(ImageInfo imageProcessed, int processedCount, int totalToProcess) {
        setImageProcessed(imageProcessed);
        setProcessedCount(processedCount);
        setTotalToProcess(totalToProcess);
        setFinished(false);
    }
    public ThumbnailsGeneratorProgress() {
        setFinished(true);
    }
    

    public ImageInfo getImageProcessed() {
        return imageProcessed;
    }

    public void setImageProcessed(ImageInfo imageProcessed) {
        this.imageProcessed = imageProcessed;
    }

    public int getProcessedCount() {
        return processedCount;
    }

    public void setProcessedCount(int processedCount) {
        this.processedCount = processedCount;
    }

    public int getTotalToProcess() {
        return totalToProcess;
    }

    public void setTotalToProcess(int totalToProcess) {
        this.totalToProcess = totalToProcess;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
