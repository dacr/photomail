/*
 * ImageDirectoryFilter.java
 *
 * Created on 5 avril 2005, 11:51
 */

package com.photomail.image;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author dcr
 */

class ImageDirectoryFilter implements FileFilter {
    public boolean accept(File file) {
        if (file.getName().startsWith(".")) return false;
        if (file.isDirectory()) return true;
        return false;
    }
}