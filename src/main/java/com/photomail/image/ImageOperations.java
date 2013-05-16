/*
 * ImageOperations.java
 *
 * Created on 26 septembre 2004, 00:05
 */

package com.photomail.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic image operations through a simple API
 * @author dcr
 */
public class ImageOperations {
    
    /** Creates a new instance of ImageOperations */
    private ImageOperations() {
    }
 
    /**
     * Load an image
     * @param file file to load
     * @throws java.io.IOException Image read file exception
     * @return image object
     */
    public static BufferedImage read(File file) throws IOException {
        return ImageIO.read(file);
    }

    /**
     * Save an image using JPEG format and a custom compression level
     * @param image Object containing the image
     * @param file Save the image into this file
     * @param compression Compression level to use for the image
     * @throws java.io.IOException image write file exception
     */
    public static void write(BufferedImage image, File file, float compression) throws IOException {
        ImageWriter writer = (ImageWriter) ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        IIOImage iio_img = new IIOImage(image, null, null);
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(compression);
        FileImageOutputStream out = new FileImageOutputStream(file);
        writer.setOutput(out);
        writer.write(null, iio_img, param);
        writer.dispose();
        out.flush();
        out.close();
    }

    
    /**
     * Resize an image buffer object. It keeps image with and height ratio.
     * @param image The image to resize
     * @param width The new width 
     * @param height The new height 
     * @return The resized image buffer object
     */
    public static BufferedImage resize(BufferedImage image, int width, int height) {
        double scale;
        if (width != -1 && height != -1) {
            scale = min( (double)width / (double)image.getWidth(), (double)height/ (double)image.getHeight());
        } else {
            if (width == -1)
                scale = (double)height / (double)image.getHeight();
            else
                scale = (double)width / (double)image.getWidth();
        }

        int scaledW = (int) (scale * image.getWidth(null));
        int scaledH = (int) (scale * image.getHeight(null));

        Image img = image.getScaledInstance(scaledW , scaledH, Image.SCALE_SMOOTH);
        BufferedImage outImage = new BufferedImage(scaledW , scaledH, BufferedImage.TYPE_INT_RGB);
        Graphics g = outImage.getGraphics();
        g.drawImage(img, 0, 0, null);

        return outImage;
    }

    /**
     * This routine compute rectangle dimension required to contain an image
     * rotated with the specified angle
     * @param width
     * @param height
     * @param angle
     * @return Rectangle dimension
     */
    public static Dimension computeRotatedDimension(double width, double height, double angle) {
        double r = sqrt(pow(width/2.0, 2.0)+pow(height/2.0, 2.0));

        double x1 = abs(r*cos(angle+acos(width/2.0/r)));
        double x2 = abs(r*cos(angle+acos(-width/2.0/r)));

        double y1 = abs(r*sin(angle+asin(height/2.0/r)));
        double y2 = abs(r*sin(angle+asin(-height/2.0/r)));
        
        double nw=max(x1, x2)*2.0;
        double nh=max(y1, y2)*2.0;
        return new Dimension((int)nw, (int)nh);
    }
    
    
    /**
     * Rotate given image buffer
     * @param image Image buffer to rotate
     * @param angle Rotation angle
     * @return the rotated image buffer
     */
    public static BufferedImage rotate(Image image, double angle) {
        // Calcul du rectangle nécessaire pour contenir l'image tournée.
        Dimension d = computeRotatedDimension(image.getWidth(null), image.getHeight(null), angle);
	BufferedImage rotated_image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);

        Graphics2D gr = rotated_image.createGraphics();
        
        AffineTransform tx = new AffineTransform();
	tx.rotate(angle, rotated_image.getWidth()/2, rotated_image.getHeight()/2);
        gr.transform(tx);

        gr.drawImage(image, (d.width-image.getWidth(null))/2, (d.height-image.getHeight(null))/2, null);

        return rotated_image;
    }
    

    /**
     * Add a comments to the given image
     * @param image Image buffer to rotate
     * @param comment Comment String
     * @return the commented image buffer
     */
    public static BufferedImage comment(Image image, String comment, Font font) {
        int iw = image.getWidth(null);
        int ih = image.getHeight(null);
        Graphics2D g2d;
        
        if (comment == null || comment.length()==0) {
            BufferedImage imaget = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
            g2d = (Graphics2D)imaget.getGraphics();
            g2d.drawImage(image, 0, 0, null);
            return imaget;
        }

        g2d = (Graphics2D)image.getGraphics();
        FontMetrics fm = g2d.getFontMetrics();
        int fh = fm.getHeight(); // Font Height
        int fb = fm.getAscent();
        
        List<String> lines = new ArrayList<String>();
        for(String line: comment.split("\\r?\\n")) {
            if (fm.charsWidth(line.toCharArray(), 0, line.length()) <= iw) {
                // Cette ligne peut tenir entièrement dans l'image
                lines.add(line);
            } else {
                // Il faut diviser cette ligne en plusieurs pour qu'elles tiennent
                StringBuffer tmpLine = new StringBuffer();
                int tmpWidth=0;
                int spaceWidth = fm.charWidth(' ');
                for(String word: line.split("\\s")) {
                    int wordW = fm.charsWidth(word.toCharArray(),0,word.length());
                    if (tmpWidth + wordW + spaceWidth >= iw) {
                        lines.add(tmpLine.toString());
                        tmpLine = new StringBuffer();
                        tmpWidth=0;
                    }
                    if (tmpWidth + wordW + spaceWidth < iw) {
                        if (tmpWidth!=0) {
                            tmpLine.append(' ');
                            tmpWidth+=spaceWidth;
                        }
                        tmpLine.append(word);
                        tmpWidth+=wordW;
                    }
                }
                if (tmpLine.length()>0) {
                    lines.add(tmpLine.toString());
                }
            }
        }
        int newHeight = ih + fh*lines.size();
	BufferedImage cimage = new BufferedImage(iw, newHeight, BufferedImage.TYPE_INT_RGB);
        g2d = (Graphics2D)cimage.getGraphics();
        g2d.setColor(Color.WHITE);
        g2d.setBackground(Color.BLACK);
        g2d.clearRect(0,0, iw, newHeight);
        g2d.drawImage(image, 0, 0, null);
        int curpos = ih+fb;
        for(String line: lines) {
            g2d.drawString(line, 0, curpos);
            curpos+=fh;
        }
        return cimage;
    }
}



