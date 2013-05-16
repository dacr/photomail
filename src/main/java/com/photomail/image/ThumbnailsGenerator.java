/*
 * ThumbnailsGenerator.java
 *
 * Created on 5 avril 2005, 12:16
 */

package com.photomail.image;

import com.photomail.setup.Setup;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


/**
 *
 * @author dcr
 */
public class ThumbnailsGenerator extends Thread implements ImageListener {
    private Logger log = Logger.getLogger(getClass().getName());

    private List<ThumbnailsGeneratorProgressListener> progressListeners = Collections.synchronizedList(new ArrayList<ThumbnailsGeneratorProgressListener>());
    private List<ImageInfo> imageInfoList;
    private File dataDirectory;
    private int     count;
    private int     total;
    private boolean imageFingerHasFinished;
    static final public int HEIGHT=64;
    static final public int SAVED_HEIGHT=64; // Higher on disk to avoid screen loss precisions
    
    /** Creates a new instance of ThumbnailsGenerator */
    public ThumbnailsGenerator(Setup setup) {
        super("ThumbnailsGenerator");
        this.imageInfoList = Collections.synchronizedList(new LinkedList<ImageInfo>());
        this.dataDirectory = setup.getDataDir();
    }
    public ThumbnailsGenerator(File cacheDirectory) {
        super("ThumbnailsGenerator");
        this.imageInfoList = Collections.synchronizedList(new LinkedList<ImageInfo>());
        this.dataDirectory = cacheDirectory;
    }

    public void imageFound(ImageInfo imageInfo) {
        if (imageInfo == null) {
            imageFingerHasFinished=true;
        } else {
            imageInfoList.add(imageInfo);
            total++;
        }
    }
    /*
    private void rdelete(File dir) {
        File[] content = dir.listFiles();
        for(File f: content) {
            if (f.isDirectory()) rdelete(f);
            else f.delete();
        }
        dir.delete();
    }*/
    private void rdeleteThumbnails(File dir) {
        File[] content = dir.listFiles(new ImageFilenameFilter());
        for(File f: content) {
            if (f.isDirectory()) rdeleteThumbnails(f);
            else f.delete();
        }
    }
    public void purge() {
        if (dataDirectory.exists()) {
            rdeleteThumbnails(dataDirectory);
        }
    }
    
    public void run() {
        log.fine("Thumbnails Generator started");

        count=0;
        total=0;
        imageFingerHasFinished=false;
        
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }
        while(true) {
            try {
                if (imageInfoList.size() == 0) {
                    if (imageFingerHasFinished) break;
                    Thread.sleep(100);
                    continue;
                } else {
                    Thread.sleep(1);
                }
                Image ii=null;
                ImageInfo imageInfo = imageInfoList.remove(0);

                // TODO : A Factoriser avec ce qu'il il y a dans ImageInfoUserData
                String cachedRelativeDirname = imageInfo.getAbsoluteFile().getParent();
                cachedRelativeDirname = cachedRelativeDirname.replaceFirst(":",  ""); // TODO : Bad hack pour contourner le C:\ problème ... c:\temp\c:\ , n'est pas valide...
                File thumbnailDir  = new File(dataDirectory, cachedRelativeDirname);
                
                if (!thumbnailDir.exists()) {
                    thumbnailDir.mkdirs();
                }
                count++;
                notifyProgressListeners(new ThumbnailsGeneratorProgress(imageInfo, count, total));
                File thumbnailFile = new File(thumbnailDir, imageInfo.getName()+"."+"png");

                if (thumbnailFile.exists()) {
                    // Vérification de la date de création
                    if (imageInfo.lastModified() > thumbnailFile.lastModified()) {
                        thumbnailFile.delete();
                    } else {
                        // Vérification de la hauteur
                        Image image;
                        try {
                            image = ImageIO.read(thumbnailFile);
                        } catch(IOException e) {
                            log.log(Level.SEVERE, "Error while loading thumbnail", e);
                            break;
                        }
                        if (image.getHeight(null) != SAVED_HEIGHT) {
                            thumbnailFile.delete();
                        } else {
                            ii = image;
                        }
                    }
                }
                if (!thumbnailFile.exists()) {
                    try {
                        ii = createThumbnailFile(imageInfo, thumbnailFile);
                    } catch(InterruptedException e) {
                        log.fine("Thumbnails Generator interrupted during writing");
                        thumbnailFile.delete();
                        break;
                    } catch(IOException e) {
                        log.log(Level.SEVERE, "Fail to generate thumbnail "+thumbnailFile, e);
                        thumbnailFile.delete();
                    }
                }
                if (ii != null) { // On a bien pu créer la miniature associée à l'image
                    imageInfo.setThumbnailFile(thumbnailFile); // On ne préinitialise plus le thumbnail pour des raisons de consommation mémoire
                }
            } catch(InterruptedException e) {
                log.fine("Thumbnails Generator interrupted");
                break;
            }
        }
        notifyProgressListeners(new ThumbnailsGeneratorProgress());
        imageInfoList.clear();
        log.fine("Thumbnails Generator finished");
    }
    

    private Image createThumbnailFile(ImageInfo imageInfo, File thumbnailFile) throws InterruptedException, IOException {
        BufferedImage image = imageInfo.getImage();
        if (image == null) {
            log.warning(String.format("Couldn't generate Thumbnail for %s as image is null",  imageInfo.getAbsolutePath()));
            return null;
        }
        BufferedImage tmp = ImageOperations.resize(image, -1, SAVED_HEIGHT);
        int w = tmp.getWidth();
        int h = tmp.getHeight();
        if (w<=0 || h<=0) {
            log.log(Level.SEVERE,String.format("Can't generate Thumnail file for %s, width (%d) or height (%d) has invalid value (<=0)",imageInfo.getPath(),w,h));
            return null;
        }
        ImageIO.write(tmp, "png", thumbnailFile);
        return tmp;
    }
    
    public void addProgressListener(ThumbnailsGeneratorProgressListener listener) {
        progressListeners.add(listener);
    }
    public void removeProgressListener(ThumbnailsGeneratorProgressListener listener) {
        progressListeners.remove(listener);
    }
    public void notifyProgressListeners(ThumbnailsGeneratorProgress progress) {
        synchronized(progressListeners) {
            for(ThumbnailsGeneratorProgressListener listener: progressListeners) {
                listener.progressMade(progress);
            }
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}

