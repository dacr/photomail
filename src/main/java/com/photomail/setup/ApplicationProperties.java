/*
 * ApplicationProperties.java
 *
 * Created on 14 avril 2005, 11:47
 */

package com.photomail.setup;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author dcr
 */
public class ApplicationProperties extends Properties {
    private final String RESPATH="/application.properties";
    
    /** Creates a new instance of ApplicationProperties */
    private ApplicationProperties() {
        try {
            load(getClass().getResourceAsStream(RESPATH));
        } catch(IOException e) {
            assert false;
        }
    }
    
    static private ApplicationProperties instance;
    static public ApplicationProperties getInstance() {
        if (instance == null) {
            instance = new ApplicationProperties();
        }
        return instance;
    }
    
    public String getWorkDir() {
        return System.getProperty("user.home")+File.separator+getProperty("product.workdir");
    }
    public String getProductName() {
        return getProperty("product.name");
    }
    public String getProductUrl() {
        return getProperty("product.url");
    }
    public String getProductEmail() {
        return getProperty("product.email");
    }
    public String getProductVersion() {
        return getProperty("product.version");
    }
    public String getProductAuthor() {
        return getProperty("product.author");
    }
    public String getProductAuthorEmail() {
        return getProperty("product.author.email");
    }

}
