/*
 * ImageFinderProgress.java
 *
 * Created on 10 avril 2005, 11:04
 */

package com.photomail.image;

/**
 *
 * @author dcr
 */
public class ImageFinderProgress {
    private String  directoryBeingExplored;
    private int     count;
    private boolean finished;
    
    /** Creates a new instance of ImageFinderProgress */
    public ImageFinderProgress(String directoryBeingExplored, int count) {
        setDirectoryBeingExplored(directoryBeingExplored);
        setCount(count);
        setFinished(false);
    }
    public ImageFinderProgress(int count) {
        setCount(count);
        setFinished(true);
    }

    public String getDirectoryBeingExplored() {
        return directoryBeingExplored;
    }

    public void setDirectoryBeingExplored(String directoryBeingExplored) {
        this.directoryBeingExplored = directoryBeingExplored;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

}
