/*
 * TestGUI.java
 * JUnit based test
 *
 * Created on 7 avril 2005, 11:22
 */

package com.photomail.gui;

import com.photomail.setup.Setup;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import junit.framework.*;

/**
 *
 * @author dcr
 */
public class TestGUI extends TestCase {
    
    public TestGUI(String testName) {
        super(testName);
    }

    protected void setUp() throws java.lang.Exception {
    }

    protected void tearDown() throws java.lang.Exception {
    }
    
    public void testRefresh() throws IOException {
        Activities activities = new Activities(Setup.load());
        activities.getThumbnailsGenerator().purge();

        JImageComboBox combobox = new JImageComboBox(activities.getAvailableImageDataModel());
        
        JScrollPane samosaic = new JScrollPane(new JImageMosaic(activities.getAvailableImageDataModel()));
        JScrollPane satable  = new JScrollPane(new JImageTable(activities.getAvailableImageDataModel()));
        JScrollPane salist   = new JScrollPane(new JImageList(activities.getAvailableImageDataModel()));

        JPanel containers = new JPanel();
        containers.setLayout(new GridLayout(1,3));
        containers.add(samosaic);
        containers.add(satable);
        containers.add(salist);

        JFrame frame = new JFrame("Test");
        frame.setLayout(new BorderLayout());
        frame.add(containers, BorderLayout.CENTER);
        frame.add(combobox, BorderLayout.NORTH);
        frame.setPreferredSize(new Dimension(640,480));
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        activities.startImageFinder();
    }
}
