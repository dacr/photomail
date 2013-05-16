/*
 * ImageInfoUserData.java
 *
 * Created on 28 avril 2005, 09:38
 */

package com.photomail.setup;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dcr
 */
public class ImageInfoUserData {
    private File      userDataFile;
    //private File      imageFile; // TODO : ATTENTION !! EN L'ETAT GENERE InstanciationException !!!
    private double    rotateViewAngle=0.0d;
    private String    comments;
    private Date      lastSent;
    private String    keywords;

    // Obligatoire pour que la propriété userDataFle
    // ne soit pas persistée
    static {
        try {
            BeanInfo info = Introspector.getBeanInfo(ImageInfoUserData.class);
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if (pd.getName().equals("userDataFile")) {
                    pd.setValue("transient", Boolean.TRUE);
                }
            }
        } catch (IntrospectionException e) {
        }
    }

    
    /** Creates a new instance of ImageInfoUserData */
    public ImageInfoUserData() {
    }


    public synchronized void save(){
        try {
            FileOutputStream out = new FileOutputStream(getUserDataFile());
            XMLEncoder encoder = new XMLEncoder(out);
            encoder.writeObject(this);
            encoder.close();
            out.close();
        } catch(IOException e) {
            Logger log = Logger.getLogger(getClass().getName());
            log.log(Level.SEVERE, "Can't save xml file : "+getUserDataFile(), e);
        }
    }
    public static File computeUserDataFile(File imageFile) {
        // TODO : A Factoriser avec ce qu'il y a dans ThumbnailsGenerator
        String cachedRelativeDirname = imageFile.getAbsoluteFile().getParent();
        cachedRelativeDirname = cachedRelativeDirname.replaceFirst(":",  ""); // TODO : Bad hack pour contourner le C:\ problème ... c:\temp\c:\ , n'est pas valide...        
        File userDataDir  = new File(Setup.getDataDir(), cachedRelativeDirname);

        if (!userDataDir.exists()) {
            userDataDir.mkdirs();
        }
        File userDataFile = new File(userDataDir, imageFile.getName()+"."+"xml");
        return userDataFile;
    }
    public static ImageInfoUserData load(File imageFile) {
        ImageInfoUserData userData=null;
        File userDataFile=computeUserDataFile(imageFile);
        if (userDataFile.exists()) {
            try {
                FileInputStream in = new FileInputStream(userDataFile);
                XMLDecoder decoder = new XMLDecoder(in);
                userData = (ImageInfoUserData)decoder.readObject();
                if (userData != null) {
                    userData.setUserDataFile(userDataFile);
                    //userData.setImageFile(imageFile);
                } else {
                    Logger log = Logger.getLogger(ImageInfoUserData.class.getName());
                    log.log(Level.WARNING, "Can't read xml file : "+userDataFile+", reinitializing it");
                }
                decoder.close();
                in.close();
            } catch(IOException e) {
                Logger log = Logger.getLogger(ImageInfoUserData.class.getName());
                log.log(Level.SEVERE, "Can't load xml file : "+userDataFile, e);
            }
        }
        if (userData == null) {
            userData = new ImageInfoUserData();
            userData.setUserDataFile(userDataFile);
            //userData.setImageFile(imageFile);
            userData.save();
        }
        return userData;
    }

    
    /* ******************************************************************* */
    public double getRotateViewAngle() {
        return rotateViewAngle;
    }

    public void setRotateViewAngle(double rotateViewAngle) {
        this.rotateViewAngle = rotateViewAngle;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments.trim();
    }

    public Date getLastSent() {
        return lastSent;
    }

    public void setLastSent(Date lastSent) {
        this.lastSent = lastSent;
    }

    public File getUserDataFile() {
        return userDataFile;
    }

    public void setUserDataFile(File userDataFile) {
        this.userDataFile = userDataFile;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

}
