/*
 * JImageMosaic.java
 *
 * Created on 6 avril 2005, 22:58
 */

package com.photomail.gui;

import com.photomail.image.ImageInfo;
import com.photomail.image.ThumbnailsGenerator;
import com.photomail.setup.Resources;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.event.ListDataListener;

/**
 *
 * @author dcr
 */
public class JImageMosaic extends JComponent implements Scrollable, ListDataListener {
    private ImageDataModel model;
    private int maxUnitIncrement = 20;
    private int hgap=4;
    private int vgap=4;
    private boolean animateLast;
    private int animateRelPos=0;
    private int animateRelInc=1;
    private int animateMaxPos=8;
    private int animateMinPos=-8;
    private long animateTempo=50;
    private long animateLastTime=0;
    private Timer timer;

    
    /** Creates a new instance of JImageMosaic */
    public JImageMosaic(ImageDataModel model) {
        this(model, false);
    }
    public JImageMosaic(ImageDataModel model, boolean animateLast) {
        this.model = model;
        this.animateLast = animateLast;
        model.addListDataListener(this); // Pour recevoir des notifications de nouvelles images disponibles
        setPreferredSize(getPreferredScrollableViewportSize()); // Pour donner une petite taille par défaut 
        setOpaque(false);
        setAnimateLast(animateLast);
    }

    public void setAnimateLast(boolean animateLast) {
        this.animateLast = animateLast;
        if (timer != null) {
            timer.cancel();
            timer=null;
            repaint();
        }
        if (animateLast) {
            timer = new Timer(true);
            timer.schedule(new TimerTask() {
                public void run() {
                    if (animateRelPos+animateRelInc > animateMaxPos) animateRelInc = -animateRelInc;
                    if (animateRelPos+animateRelInc < animateMinPos) animateRelInc = -animateRelInc;
                    animateRelPos+=animateRelInc;
                    repaint();
                } }, 10, animateTempo);
        }
    }
    
    /**
     * Draw the image applying a scale, translate and rotate transform to fit
     * the view window and associated image view parameters
     * 
     */
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);

        // Taille de la zone d'affichage
        int w = getSize().width;
        int h = getSize().height;

        // Fond par défaut
//        g2d.setBackground(Color.BLACK);
//        g2d.clearRect(0,0,w,h);
        int x=hgap, y=vgap;
        for(int i=0; i<model.getSize(); i++) {
            Image toDraw;
            ImageInfo imageInfo = model.get(i);
            if (imageInfo.isThumbnailFileDefined()) {
                ImageIcon thumbnail = imageInfo.getThumbnail();
                toDraw = thumbnail.getImage();
            } else {
                String msg = Resources.getString("image.mosaic.loading.message");
                int tw= 8+g2d.getFontMetrics().charsWidth(msg.toCharArray(), 0, msg.length()); // TODO : Attention on considère qu'on a la même font dans g2d et dans le future tg
                int th=ThumbnailsGenerator.HEIGHT;
                toDraw = new BufferedImage(tw, th, BufferedImage.TYPE_INT_RGB);
                Graphics2D tg = (Graphics2D)toDraw.getGraphics();
                tg.setBackground(Color.WHITE);
                tg.clearRect(0, 0, tw, th);
                tg.setColor(Color.BLACK);
                tg.drawChars(
                        msg.toCharArray(),
                        0,
                        msg.length(),
                        (tw-tg.getFontMetrics().charsWidth(msg.toCharArray(), 0, msg.length()))/2,
                        (th-tg.getFontMetrics().getHeight())/2);
            }
            int iw = toDraw.getWidth(null);
            if (x+iw+hgap > w) {
                x=hgap;
                y+=ThumbnailsGenerator.HEIGHT+vgap;
            }
            if (i == model.getSize()-1 && animateLast) {
                g2d.drawImage(toDraw, x, y+animateRelPos, null);
            } else {
                g2d.drawImage(toDraw,x,y,null);
            }
            x+=iw+hgap;
        }
        // Hauteur requise
        int nh = (x==hgap)?y+vgap:y+ThumbnailsGenerator.HEIGHT+vgap;
        if (h != nh) {
            setPreferredSize(new Dimension(w, nh));
            revalidate();
        }
    }

    public Dimension getPreferredScrollableViewportSize() {
        Dimension d = new Dimension(ThumbnailsGenerator.HEIGHT*3, ThumbnailsGenerator.HEIGHT*3);
        return d;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL)
            return visibleRect.width - maxUnitIncrement;
        else
            return visibleRect.height - maxUnitIncrement;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        //Get the current position.
        int currentPosition = 0;
        if (orientation == SwingConstants.HORIZONTAL) {
            currentPosition = visibleRect.x;
        } else {
            currentPosition = visibleRect.y;
        }

        //Return the number of pixels between currentPosition
        //and the nearest tick mark in the indicated direction.
        if (direction < 0) {
            int newPosition = currentPosition -
                             (currentPosition / maxUnitIncrement)
                              * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        } else {
            return ((currentPosition / maxUnitIncrement) + 1) * maxUnitIncrement - currentPosition;
        }
    }

    public void contentsChanged(javax.swing.event.ListDataEvent listDataEvent) {
        repaint();
    }
    public void intervalAdded(javax.swing.event.ListDataEvent listDataEvent) {
        repaint();
    }
    public void intervalRemoved(javax.swing.event.ListDataEvent listDataEvent) {
        repaint();
    }
}

