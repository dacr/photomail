/*
 * ImageDirectoryListener.java
 *
 * Created on 5 avril 2005, 11:34
 */

package com.photomail.image;

/**
 *
 * @author dcr
 */
public interface ImageListener {
    /**
     * null is received when finished
     */
    public void imageFound(ImageInfo event);
}
