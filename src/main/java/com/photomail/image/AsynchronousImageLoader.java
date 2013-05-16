/*
 * AsynchronousImage.java
 *
 * Created on 23 mai 2005, 14:53
 */

package com.photomail.image;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.*;
import javax.imageio.stream.FileImageInputStream;
import static com.photomail.image.AsynchronousImageStatus.*;
import java.io.InterruptedIOException;
import javax.imageio.ImageReadParam;

/**
 *
 * @author dcr
 */
public class AsynchronousImageLoader implements Runnable, IIOReadProgressListener, IIOReadWarningListener, IIOReadUpdateListener {
    private Logger log = Logger.getLogger(getClass().getName());
    
    private File  imageFile;
    private BufferedImage image;
    private Thread thread;
    private AsynchronousImageStatus status;
    private ImageReader reader;
    private AsynchronousImageObserver observer;
    
    private int   availableLines=0;
    private float progress=0.0f;

    public AsynchronousImageLoader(File imageFile) {
        this(imageFile, null);
    }
    /** Creates a new instance of AsynchronousImage */
    public AsynchronousImageLoader(File imageFile, AsynchronousImageObserver observer) {
        this.imageFile = imageFile;
        this.observer  = observer;
        status = LOADING;

        String suffix = getFileSuffix(imageFile);
        if (suffix == null) {
            log.severe("Given file has no suffix : "+imageFile);
            status=ERROR;
            return;
        }
        Iterator<ImageReader> i = ImageIO.getImageReadersBySuffix(suffix);
        if (!i.hasNext()) {
            log.severe("Can't find image reader for suffix "+suffix+" for file "+imageFile);
            status=ERROR;
            return;
        }
        reader = i.next();
        thread = new Thread(this);
        thread.start();
    }

    
    
    
    public void waitImageComplete() throws InterruptedException {
        if (thread != null) {
            while(thread.isAlive()) {
                Thread.sleep(10);
            }
        }
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    public void abort() {
        try {
            observer=null;
            if (reader != null) reader.abort();
        } catch(IllegalStateException e) {
            //TOTO : peut-être mieux tester si un read est vraiment en cours
            // pour éviter cette exception
        }
        if (thread != null) thread.interrupt();
    }
    
    public Thread getThread() {
        return thread;
    }
    
    public AsynchronousImageStatus getStatus() {
        return status;
    }
    

    private String getFileSuffix(File in) {
        if (in == null) return null;
        String[] suffix = in.getName().split("\\.");
        if (suffix.length<=1) return null;
        return suffix[suffix.length-1];
    }

    public void run() {
        try {
            reader.addIIOReadProgressListener(this);
            reader.addIIOReadWarningListener(this);
            reader.addIIOReadUpdateListener(this);
            reader.setInput(new FileImageInputStream(imageFile));
            ImageReadParam p = reader.getDefaultReadParam();
            // Pour gérer un arrêt avant même d'entrer dans le read
            // car le reader.abort ne fonctionne qu'une fois entrer dans le read
            Thread.sleep(1);
            image = reader.read(0, p);
        } catch(IllegalStateException e) {
            log.log(Level.SEVERE, "Internal error",e);
            status=ERROR;
        } catch(IndexOutOfBoundsException e) {
            log.log(Level.SEVERE, "No image at index 0",e);
            status=ERROR;
        } catch(IOException e) {
            log.log(Level.SEVERE, "Can't read image",e);
            status=ERROR;
        } catch(InterruptedException e) {
            log.log(Level.WARNING, "Image loading has been interrupted");
            status=INTERRUPTED;            
        } finally {
            reader.removeAllIIOReadProgressListeners();
            reader.removeAllIIOReadWarningListeners();
            reader.dispose();
        }
        if (status==LOADED) {
            if (observer!=null) observer.imageComplete(image);
        }
    }


    public void imageComplete(ImageReader imageReader) {
        status = LOADED;
    }

    public void imageProgress(ImageReader imageReader, float param) {
    }

    public void imageStarted(ImageReader imageReader, int param) {
    }

    
    
    public void readAborted(ImageReader imageReader) {
        log.log(Level.WARNING, "Image loading has been interrupted");
        status=INTERRUPTED;
    }

    public void sequenceComplete(ImageReader imageReader) {
    }

    public void sequenceStarted(ImageReader imageReader, int param) {
    }

    
    
    public void thumbnailComplete(ImageReader imageReader) {
    }

    public void thumbnailProgress(ImageReader imageReader, float param) {
    }

    public void thumbnailStarted(ImageReader imageReader, int param, int param2) {
    }

    public void warningOccurred(ImageReader imageReader, String str) {
        log.warning(str);
    }

    
    
    
    
    
    
    
    public void imageUpdate(ImageReader imageReader, BufferedImage bufferedImage, int minX, int minY, int width, int height, int periodX, int periodY, int[] bands) {
        if (image == null) {
            image  = bufferedImage;
        }
        availableLines = minY+height;
        if (observer!=null) observer.imageUpdate(bufferedImage, availableLines);
    }

    public void passComplete(ImageReader imageReader, BufferedImage bufferedImage) {
    }

    public void passStarted(ImageReader imageReader, BufferedImage bufferedImage, int pass, int minPass, int maxPass, int minX, int minY, int periodX, int periodY, int[] bands) {
    }

    public void thumbnailPassComplete(ImageReader imageReader, BufferedImage bufferedImage) {
    }

    public void thumbnailPassStarted(ImageReader imageReader, BufferedImage bufferedImage, int pass, int minPass, int maxPass, int minX, int minY, int periodX, int periodY, int[] bands) {
    }

    public void thumbnailUpdate(ImageReader imageReader, BufferedImage bufferedImage, int minX, int minY, int width, int height, int periodX, int periodY, int[] bands) {
    }

    
    
    
}
