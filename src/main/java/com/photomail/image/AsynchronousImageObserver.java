/*
 * AsynchronousImageObserver.java
 *
 * Created on 25 mai 2005, 11:42
 */

package com.photomail.image;

import java.awt.image.BufferedImage;

/**
 *
 * @author dcr
 */
public interface AsynchronousImageObserver {
    
    /** Creates a new instance of AsynchronousImageObserver */
    public void imageUpdate(BufferedImage image, int lineCount);
    public void imageComplete(BufferedImage image);
}
