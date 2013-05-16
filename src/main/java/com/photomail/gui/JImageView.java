/*
 * JImageView.java
 *
 * Created on 6 avril 2005, 15:17
 */

package com.photomail.gui;


import com.photomail.image.AsynchronousImageLoader;
import com.photomail.image.AsynchronousImageObserver;
import com.photomail.image.AsynchronousImageStatus;
import com.photomail.image.ImageInfo;
import com.photomail.image.ImageOperations;
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
import java.io.IOException;
import static java.lang.Math.*;
import java.util.logging.Logger;
import javax.swing.JComponent;



public class JImageView extends JComponent implements AsynchronousImageObserver {
    private Logger log = Logger.getLogger(getClass().getName());
    private final static GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    private final static int VIEW_PROGRESS_TEMPO = 200; // fréquence de refresh pour une image en cours de chargement
    
    private ImageInfo     imageInfo;
    private BufferedImage image;
    private AsynchronousImageLoader loader;

    private long lastRepaintTime;


    public JImageView(ImageInfo imageInfo) throws IOException {
        super();
        setImageInfo(imageInfo);
    }
    public JImageView() {
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
            image.flush();
            image=null;
        }
        if (loader!=null) {
            loader.abort();
        }
        this.loader = new AsynchronousImageLoader(imageInfo, this);
        this.imageInfo= imageInfo;
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
     * Draw the image applying a scale, translate and rotate transform to fit
     * the view window and associated image view parameters
     * 
     */
    public void paint(Graphics g) {
        
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
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
        
        drawImage(g2d, image, imageInfo);

    }


    private void drawImage(Graphics2D g2d, BufferedImage image, ImageInfo imageInfo) {
        // Taille de l'image
        float iw = image.getWidth();
        float ih = image.getHeight();

        // Taille de la zone d'affichage
        double w = getSize().width;
        double h = getSize().height;

        // Couleur de fond
        g2d.setBackground(Color.BLACK);
        g2d.clearRect(0,0,(int)w,(int)h);
        
        double angle = imageInfo.getRotateViewAngle();
        if (loader != null && loader.getStatus() == AsynchronousImageStatus.LOADING) {
            // On effectue pas de rotation pendant l'affichage progressive d'une image
            angle = 0;
        }
                
        // Calcul du ratio requis pour que l'image avec rotation soit entièrement visible
        Dimension rdim = ImageOperations.computeRotatedDimension((double)iw, (double)ih, (double)angle);
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
        tx.rotate(angle, iw/2.0d, ih/2.0d);
        tx.scale(r, r);
        tx.translate(-(iw/2.0d)*a, -(ih/2.0d)*a);

        //g2d.transform(tx);
        g2d.drawImage(image, tx, null);
    }







    public void imageComplete(BufferedImage image) {
        this.image = image;
        repaint();
    }

    public void imageUpdate(BufferedImage image, int lineCount) {
        this.image = image;
        long t = System.currentTimeMillis();
        long tempo;
        tempo = VIEW_PROGRESS_TEMPO;
        if (t-lastRepaintTime > tempo) {
            repaint();
            lastRepaintTime = t;
        }
    }
    
    
}
