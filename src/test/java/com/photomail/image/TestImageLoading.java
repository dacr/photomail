/*
 * TestAsynchronousImage.java
 * JUnit based test
 *
 * Created on 23 mai 2005, 14:55
 */

package com.photomail.image;

/*
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectory;
*/
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import junit.framework.*;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import static com.photomail.image.AsynchronousImageStatus.*;

/**
 *
 * @author dcr
 */
public class TestImageLoading extends TestCase {
    private String JPG="test/test1.jpg";
    private String PNG="test/test2.png";
    private String TIF="test/test3.tiff";
    private String GIF="test/test4.gif";
    private String BMP="test/test5.bmp";
    private String EXIF="test/testexif.jpg";
    
    public TestImageLoading(String testName) {
        super(testName);
    }

    
    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }


    public void testDefaultLoaders() throws Exception {
        // Synchronous image loading with cache capabilities  (jpg, png, gif)
        Image i1 = new ImageIcon(JPG).getImage();
        
        // Asynchronous image loading (jpg, png, gif)
        Image i2 = Toolkit.getDefaultToolkit().createImage(JPG);
        MediaTracker mt = new MediaTracker(new JLabel());
        mt.addImage(i2,0);
        mt.waitForAll();
        
        // Synchronous image loading extensible API (jpg, png, gif, bmp, wbmp)
        Image i3 = ImageIO.read(new File(JPG));
    }

    public void testDefaultMetaDataReaderUsage() throws Exception {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(data, true);
        DefaultAvailableMetaData.dumpImageInfo(EXIF, output);
        String metaData = data.toString();
        assertTrue(metaData.length()>0);
        assertTrue(metaData.indexOf("javax_imageio_1.0")>=0);
    }
    /*
    public void testDrewImagingMetaData() throws Exception {
        File jpegFile = new File(EXIF);
        Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);
        Directory dir = metadata.getDirectory(ExifDirectory.class);
        System.out.println("Taken on : "+dir.getDate(ExifDirectory.TAG_DATETIME_ORIGINAL));
        System.out.println("Model    : "+dir.getString(ExifDirectory.TAG_MODEL));
    }
*/
    public void testAsynchronousLoader() throws Exception {
        AsynchronousImageLoader loader;
        BufferedImage image;

        String[] imgs = {JPG, PNG, GIF, BMP, EXIF};

        for (String img: imgs) {
            loader = new AsynchronousImageLoader(new File(img));
            assertTrue("Not loading "+img, loader.getStatus()==LOADING);
            loader.waitImageComplete();
            image = loader.getImage();
            assertTrue("Not loaded "+img, loader.getStatus()==LOADED);
            assertTrue("Null "+img, image != null);
        }
/*
        loader = new AsynchronousImageLoader(new File("sdflkj.jpg"));
        loader.waitImageComplete();
        image = loader.getImage();
        assertTrue(loader.getStatus()==ERROR);

        loader = new AsynchronousImageLoader(new File("sdflkj"));
        loader.waitImageComplete();
        image = loader.getImage();
        assertTrue(loader.getStatus()==ERROR);
  */      
        loader = new AsynchronousImageLoader(new File(BMP));
        loader.abort();
        loader.waitImageComplete();
        assertTrue(loader.getStatus()==INTERRUPTED);
        
        
    }
    
    
}






class DefaultAvailableMetaData {
    
    static public void displayMetadata(Node root, PrintStream out) {
        displayMetadata(root, 0, out);
    }

    static void indent(int level, PrintStream out) {
        for (int i = 0; i < level; i++) {
            out.print("  ");
        }
    } 

    static void displayMetadata(Node node, int level, PrintStream out) {
        indent(level, out); // emit open tag
        out.print("<" + node.getNodeName());
        NamedNodeMap map = node.getAttributes();
        if (map != null) { // print attribute values
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                out.print(" " + attr.getNodeName() +
                                 "=\"" + attr.getNodeValue() + "\"");
            }
        }

        Node child = node.getFirstChild();
        if (child != null) {
            out.println(">"); // close current tag
            while (child != null) { // emit child tags recursively
                displayMetadata(child, level + 1, out);
                child = child.getNextSibling();
            }
            indent(level, out); // emit close tag
            out.println("</" + node.getNodeName() + ">");
        } else {
            out.println("/>");
        }
    }
    
    static public String getSuffix(String name) {
        String[] p=name.split("\\.");
	return p[p.length-1];
    }
    static public void dumpImageInfo(String name, PrintStream out) throws Exception {
	String suffix = getSuffix(name);
	Iterator<ImageReader> it = ImageIO.getImageReadersBySuffix(suffix);
	if (it.hasNext()) {
	    ImageReader reader = it.next();
	    ImageInputStream imageIn= new FileImageInputStream(new File(name));
	    reader.setInput((Object)imageIn, false, false);
	    IIOMetadata meta = reader.getImageMetadata(0);
	    String[] formatNames = meta.getMetadataFormatNames();
	    for(int i=0; i<formatNames.length; i++) {
		Node node = meta.getAsTree(formatNames[i]);
                displayMetadata(node, out);
	    }
	} else {
	    out.println("No image reader available");
	}
    }        
}
