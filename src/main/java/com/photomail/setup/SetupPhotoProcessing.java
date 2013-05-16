/*
 * SetupPhotoProcessing.java
 *
 * Created on 22 septembre 2004, 22:44
 */

package com.photomail.setup;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;

/**
 *
 * @author  dcr
 */
public class SetupPhotoProcessing implements Serializable {

    private float compression;
    private PhotoSizes size=PhotoSizes.R640x480;
    private int   desiredWidth=640;
    private int   desiredHeight=480;

    // Obligatoire pour que les deux propriétés desiredWidth et desiredHeight 
    // ne soient pas persistées
    static {
        try {
            BeanInfo info = Introspector.getBeanInfo(SetupPhotoProcessing.class);
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if (pd.getName().equals("desiredWidth") || pd.getName().equals("desiredHeight") ) {
                    pd.setValue("transient", Boolean.TRUE);
                }
            }
        } catch (IntrospectionException e) {
        }
    }

    /** Creates a new instance of SetupPhotoProcessing */
    public SetupPhotoProcessing() {
    }

    public float getCompression() {
        return compression;
    }

    public void setCompression(float compression) {
        this.compression=compression;
    }

    public int getDesiredWidth() {
        return desiredWidth;
    }

    public void setDesiredWidth(int desiredWidth) {
        this.desiredWidth=desiredWidth;
    }

    public int getDesiredHeight() {
        return desiredHeight;
    }

    public void setDesiredHeight(int desiredHeight) {
        this.desiredHeight=desiredHeight;
    }

    public PhotoSizes getSize() {
        return size;
    }

    public void setSize(PhotoSizes size) {
        this.size = size;
        switch(size) {
            case R1024x768:  setDesiredWidth(1024);  setDesiredHeight( 768); break;
            case R1280x1024: setDesiredWidth(1280);  setDesiredHeight(1024); break;
            case R1600x1200: setDesiredWidth(1600);  setDesiredHeight(1200); break;
            case R160x100:   setDesiredWidth( 160);  setDesiredHeight( 100); break;
            case R320x200:   setDesiredWidth( 320);  setDesiredHeight( 200); break;
            case R640x480:   setDesiredWidth( 640);  setDesiredHeight( 480); break;
            case R800x600:   setDesiredWidth( 800);  setDesiredHeight( 600); break;
        }
    }
    
}
