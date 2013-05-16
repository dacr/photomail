/*
 * FileToolBox.java
 *
 * Created on 29 juin 2005, 18:43
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.photomail.tools;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 *
 * @author David Crosson
 */
public class FileToolBox {
    
    /** Creates a new instance of FileToolBox */
    private FileToolBox() {
    }
    
    

    /**
     * Recursive directory file search 
     */
    public static void recursiveFileSearch(File searchDir, FileFilter filter, List<File> found) {
        if (searchDir == null) return;
        if (!searchDir.exists()) return;
        if (!searchDir.isDirectory()) return;
        for(File file: searchDir.listFiles(filter)) {
            if (file.isDirectory()) {
                recursiveFileSearch(file, filter, found);
            } else {
                found.add(file);
            }
        }
    }
    
    /**
     * InputStream to file
     */
    public static void inputStreamToFile(InputStream inputStream, File destFile) throws IOException {
        BufferedInputStream  bis = new BufferedInputStream(inputStream);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
        byte[] buffer = new byte[8192];
        int l;
        while( (l = bis.read(buffer)) != -1) {
            bos.write(buffer, 0, l);
        }
        bos.flush();
        bos.close();
    }
 
}


