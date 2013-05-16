/*
 * ImageInfoEvent.java
 *
 * Created on 5 avril 2005, 12:43
 */

package com.photomail.image;

/**
 *
 * @author dcr
 */
public class ImageInfoEvent {
    private ImageInfo imageInfo;
    private boolean thumbnail;

    public ImageInfoEvent(ImageInfo imageInfo, boolean thumbnail) {
        this.setImageInfo(imageInfo);
        this.thumbnail = thumbnail;
    }

    public boolean isThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(boolean thumbnail) {
        this.thumbnail = thumbnail;
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    public void setImageInfo(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }
    
}
