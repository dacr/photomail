/*
 * FilterFileButNotDirectory.java
 *
 * Created on 29 juin 2005, 18:47
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.photomail.tools;

import java.io.File;
import java.io.FileFilter;

/**
 * FilterFileButNotDirectory
 *
 * @author David Crosson
 */
class FilterFileButNotDirectory implements FileFilter {
    private String filename;
    private boolean usecase;
    
    public FilterFileButNotDirectory(String filename, boolean usecase) {
        this.filename = (usecase)?filename:filename.toLowerCase();
        this.usecase = usecase;
    }
    public boolean accept(File file) {
        if (file.isDirectory()) return true;
        if (usecase) {
            return file.getName().equals(filename);
        } else {
            return file.getName().toLowerCase().equals(filename);
        }
    }
}
