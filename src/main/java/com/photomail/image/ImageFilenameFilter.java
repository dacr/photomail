/*
 * ImageFilenameFilter.java
 *
 * Created on 5 avril 2005, 11:48
 */

package com.photomail.image;

import java.io.FilenameFilter;
import java.util.regex.Pattern;


/**
 *
 * @author dcr
 */
class ImageFilenameFilter implements FilenameFilter {
    Pattern filenamePattern = Pattern.compile(".*\\.(?:(?:gif)|(?:jpg)|(?:bmp)|(?:png))$");
    public boolean accept(java.io.File dir, String name) {
        if (filenamePattern.matcher(name.toLowerCase()).matches()) return true;
        return false;
    }
}

