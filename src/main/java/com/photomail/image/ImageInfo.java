/*
 * ImageInfo.java
 *
 * Created on 5 avril 2005, 11:43
 */

package com.photomail.image;

import java.awt.Image;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.photomail.setup.ImageInfoUserData;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author dcr
 */
public class ImageInfo extends File {
    private List<ImageInfoListener> imageInfoListeners = Collections.synchronizedList(new ArrayList<ImageInfoListener>());
    private File      thumbnailFile;
    private ImageIcon thumbnail;

    ImageInfoUserData userData;
    
    public ImageInfo(File file) {
        super(file.getPath());
    }
    public ImageInfo(File parent, String child) {
        super(parent, child);
    }
    public ImageInfo(String pathname) {
        super(pathname);
    }
    public ImageInfo(String parent, String child) {
        super(parent, child);
    }
    public ImageInfo(URI uri) {
        super(uri);
    }
    
    public void addImageInfoListener(ImageInfoListener listener) {
        imageInfoListeners.add(listener);
    }
    public void removeImageInfoListener(ImageInfoListener listener) {
        imageInfoListeners.remove(listener);
    }
    public void notifyImageInfoListeners(ImageInfoEvent event) {
        synchronized(imageInfoListeners) {
            for(ImageInfoListener listener : imageInfoListeners) {
                listener.changeOccured(event);
            }
        }
    }

    public boolean isThumbnailFileDefined() {
        return getThumbnailFile() != null;
    }

    protected File getThumbnailFile() {
        return thumbnailFile;
    }

    public void setThumbnailFile(File thumbnailFile) {
        this.thumbnailFile = thumbnailFile;
        notifyImageInfoListeners(new ImageInfoEvent(this, true));
    }

    public void setThumbnailFile(File thumbnailFile, BufferedImage thumbnailImage) {
        if (thumbnailImage != null) {
            thumbnail = new ImageIcon(optimizeThumbnail(thumbnailImage));
        }
        setThumbnailFile(thumbnailFile);
    }


    private BufferedImage optimizeThumbnail(BufferedImage in) {
        return ImageOperations.resize(in, -1, ThumbnailsGenerator.HEIGHT);
    }
    private ImageIcon loadThumbnail() {
        if (!isThumbnailFileDefined()) return null;
        //ImageIcon tmp = new ImageIcon(Toolkit.getDefaultToolkit().createImage(getThumbnailFile().getPath()));
        //Image image = tmp.getImage();
        try {
            BufferedImage image = ImageIO.read(getThumbnailFile());
            initUserData();
            image = (userData.getRotateViewAngle()==0.0d)?image:ImageOperations.rotate(image, userData.getRotateViewAngle());
            image = optimizeThumbnail(image);
            return new ImageIcon(image);
        } catch(IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Can't read Thumbnail image file", e);
            return null;
        }
    }

    public void setThumbnail(ImageIcon thumbnail) {
        this.thumbnail = thumbnail;
        notifyImageInfoListeners(new ImageInfoEvent(this, true));
    }

    /**
     * Thumbnail is loaded if needed
     * This is a synchronous load operation (Performance Issue)
     */
    public ImageIcon getThumbnail() {
        if (thumbnail==null && isThumbnailFileDefined()) {
            setThumbnail(loadThumbnail());
        }
        return thumbnail;
    }
    
    /**
     * Image is loaded through Toolkit (for performances)
     * This is an asynchronous load operation
     */
    public BufferedImage getImage() {
        try {
            BufferedImage img = ImageIO.read(this);
            return img;
        } catch(IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Can't read image "+getPath(), e);
            return null;
        }
    }


    
    public Date getDate() {
        return new Date(lastModified());
    }


    /* *********************************************************************** */
    private void initUserData() {
        if (userData==null) {
            userData=ImageInfoUserData.load(this);
        }
    }

    public double getRotateViewAngle() {
        initUserData();
        return userData.getRotateViewAngle();
    }
    public void setRotateViewAngle(double rotateViewAngle) {
        initUserData();
        userData.setRotateViewAngle(rotateViewAngle);
        setThumbnail(loadThumbnail());
        userData.save();
    }


    public String getComments() {
        initUserData();        
        return userData.getComments();
    }
    public void setComments(String comments) {
        initUserData();
        userData.setComments(comments);
        userData.save();
    }


    public Date getLastSent() {
        initUserData();
        return userData.getLastSent();
    }
    public void setLastSent(Date lastSent) {
        initUserData();
        userData.setLastSent(lastSent);
        userData.save();
    }
    
    public String getKeywords() {
        initUserData();        
        return userData.getKeywords();
    }
    public void setKeywords(String keywords) {
        initUserData();
        userData.setKeywords(keywords);
        userData.save();
    }
    
}


