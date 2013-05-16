/*
 * JImageView.java
 *
 * Created on 6 avril 2005, 15:17
 */

package com.photomail.gui;


import com.photomail.image.ImageInfo;
import com.photomail.image.ImageOperations;
import com.photomail.setup.Resources;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import static java.lang.Math.*;
import java.util.logging.Logger;
import javax.swing.JComponent;



public class JImageViewOld extends JComponent implements ImageObserver {
    private Logger log = Logger.getLogger(getClass().getName());
    private final static GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    
    private ImageInfo imageInfo, previousImageInfo;
    private Image     image, previousImage;
    private boolean fullyLoaded=false;
    private int loadedLinesCount;
    private long lastRepaintTime;
    private String progressFormatString = Resources.getString("wizard.viewer.loadingprogress");
    
    public JImageViewOld(ImageInfo imageInfo) throws IOException {
        super();
        setImageInfo(imageInfo);
    }

    public JImageViewOld() {
        super();
        try {
            setImageInfo(null);
        } catch(IOException e) {
            // Can be safely ignored
        }
    }
    
    
    /**
     * Modifying the viewed Image
     */
    public void setImageInfo(ImageInfo imageInfo) throws IOException {
        if (imageInfo == this.imageInfo) return;
       
        if (image != null) {
            if (!fullyLoaded) {
                image.flush();
            } else {
                if (previousImage != null) { 
                    previousImage.flush();
                }
                // Donc on est sur que cette image est complètement chargée...
                this.previousImageInfo = this.imageInfo;
                this.previousImage     = this.image;
            }
        }
        this.imageInfo         = imageInfo;
        if (imageInfo != null) {
            image = imageInfo.getImage();
        } else {
            image = null;
            previousImage = null;
            previousImageInfo = null;
        }
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(image, 0);
        lastRepaintTime  = 0;
        loadedLinesCount = 0;
        fullyLoaded      = ( (mt.statusID(0, false) & MediaTracker.COMPLETE) != 0);

        repaint();
    }

    public BufferedImage copyIntoCompatibleImage(Image image, int width, int height) {
        if (image == null)
            return null;
        BufferedImage compatibleImage = graphicsConfiguration.createCompatibleImage(width, height);
        Graphics2D g2d = compatibleImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return compatibleImage;
    }

    /**
     * Rotate left current image
     */
    public void rotateLeft() {
        if (imageInfo == null) return;
        imageInfo.setRotateViewAngle(imageInfo.getRotateViewAngle()-PI/2);
        repaint();
    }

    /**
     * Rotate right current image
     */
    public void rotateRight() {
        if (imageInfo == null) return;
        imageInfo.setRotateViewAngle(imageInfo.getRotateViewAngle()+PI/2);
        repaint();
    }
    
    /**
     * Receive image loading progress
     */
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        log.finest("x="+x+" y="+y+" w="+width+" h="+height+" ALLBITS="+(infoflags&ALLBITS)+" SOMEBITS="+(infoflags&SOMEBITS));

        if (img !=null && img != image) return false; // Car il s'agit d'un évênement sur une image anciennement présentée...
        
        if ((infoflags & (ERROR)) != 0) {
            //errored = true;
        }
        if ((infoflags & (WIDTH | HEIGHT)) != 0) {
            //positionImages();
        }
        boolean done = ((infoflags & (ERROR | FRAMEBITS | ALLBITS)) != 0);
        if (!done && System.currentTimeMillis() > lastRepaintTime + 500) {
            lastRepaintTime = System.currentTimeMillis();
            loadedLinesCount=y;
            fullyLoaded=false;
            repaint(10);
        }
        if (done == true) {
            loadedLinesCount=height;
            fullyLoaded=true;
            repaint(10);
        }
        return !done;
    }

    
    /**
     * Draw the image applying a scale, translate and rotate transform to fit
     * the view window and associated image view parameters
     * 
     */
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);

        // Taille de la zone d'affichage
        double w = getSize().width;
        double h = getSize().height;

        // Fond par défaut
        if (image == null) {
            g2d.setBackground(Color.BLACK);
            g2d.clearRect(0,0,(int)w,(int)h);
            return;
        }
        
        // Taille de l'image
        float iw = image.getWidth(this);
        float ih = image.getHeight(this);

        if (ih != -1 && !fullyLoaded) {
            if (previousImage != null && previousImageInfo != null) {
                drawImage(g2d, previousImage, previousImageInfo);
            } else {
                g2d.setBackground(Color.BLACK);
                g2d.clearRect(0,0,(int)w,(int)h);
            }
            int progress = loadedLinesCount*100/(int)ih;
            String message = String.format(progressFormatString, progress);
            int tx=20, ty=20;
            int tw=g2d.getFontMetrics().charsWidth(message.toCharArray(), 0, message.length());
            int th=g2d.getFontMetrics().getHeight();
            int ta=g2d.getFontMetrics().getAscent();
            g2d.setBackground(Color.BLACK);
            g2d.clearRect(tx, ty-ta, (int)tw,(int)th);
            g2d.setColor(Color.WHITE);
            g2d.drawString(message, tx, ty);
            return;
        }
        if (iw == -1 || ih == -1 || !fullyLoaded) {
            if (previousImage != null && previousImageInfo != null) {
                drawImage(g2d, previousImage, previousImageInfo);
            } else {
                g2d.setBackground(Color.BLACK);
                g2d.clearRect(0,0,(int)w,(int)h);
            }
            return;
        }
        drawImage(g2d, image, imageInfo);
    }

    private void drawImage(Graphics2D g2d, Image image, ImageInfo imageInfo) {
        // Taille de l'image
        float iw = image.getWidth(this);
        float ih = image.getHeight(this);

        // Taille de la zone d'affichage
        double w = getSize().width;
        double h = getSize().height;

        // Couleur de fond
        g2d.setBackground(Color.BLACK);
        g2d.clearRect(0,0,(int)w,(int)h);
                
        // Calcul du ratio requis pour que l'image avec rotation soit entièrement visible
        Dimension rdim = ImageOperations.computeRotatedDimension((double)iw, (double)ih, (double)imageInfo.getRotateViewAngle());
        double r = min(w/rdim.width, h/rdim.height);
        double a=(r-1d)/r; // Ratio pour recentrer l'image sur le point de rotation après changement d'échelle

        // Calcul des coordonnées de placement de l'image sur la zone d'affichage
        double x = (w-iw)/2.0f;
        double y = (h-ih)/2.0f;
        // Calcul des coordonnées du centre de rotation
        double xc = iw/2.0d;
        double yc = ih/2.0d;

        AffineTransform tx = new AffineTransform();
        tx.translate(x,y);
        tx.rotate(imageInfo.getRotateViewAngle(), iw/2.0d, ih/2.0d);
        tx.scale(r, r);
        tx.translate(-(iw/2.0d)*a, -(ih/2.0d)*a);

        //g2d.transform(tx);
        g2d.drawImage(image, tx, this);
    }
    
    
}
