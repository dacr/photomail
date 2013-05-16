/*
 * TestImageProcess.java
 * JUnit based test
 *
 * Created on 5 avril 2005, 12:05
 */

package com.photomail.image;

import java.io.File;
import junit.framework.*;



public class TestImageProcess extends TestCase {
    
    public TestImageProcess(String testName) {
        super(testName);
    }

    protected void setUp() throws java.lang.Exception {
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public void testNOP() {
    }
    /*
    public void testBasic() {
        ImageFinder          imageFinder = new ImageFinder(new File("."));
        ThumbnailsGenerator  thumbnailsGenerator = new ThumbnailsGenerator(new File(".thumbnails/"));
        imageFinder.addImageListener(thumbnailsGenerator);
        imageFinder.start();
        thumbnailsGenerator.start();
        
    }
    */
    
}
