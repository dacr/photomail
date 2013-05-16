/*
 * ImageDirectorySearch.java
 *
 * Created on 5 avril 2005, 11:32
 */

package com.photomail.image;

import com.photomail.setup.Setup;
import com.photomail.setup.SetupSearch;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author dcr
 */
public class ImageFinder extends Thread {
    private Logger log = Logger.getLogger(getClass().getName());
    private List<ImageListener> imageListeners = new ArrayList<ImageListener>();
    private List<ImageFinderProgressListener> progressListeners = new ArrayList<ImageFinderProgressListener>();
    private int count;

    private SetupSearch search;

    public ImageFinder() {
        super("ImageFinder");
        search = getDefaultSetupSearch();
        search.getDirectories().clear();
        for(File dir: File.listRoots()) search.getDirectories().add(dir.getAbsolutePath());
    }
    public ImageFinder(File[] searchDirectories) {
        super("ImageFinder");
        search = getDefaultSetupSearch();
        search.getDirectories().clear();
        for(File dir: searchDirectories) search.getDirectories().add(dir.getAbsolutePath());
    }
    public ImageFinder(File searchDirectory) {
        super("ImageFinder");
        search = getDefaultSetupSearch();
        search.getDirectories().clear();
        search.getDirectories().add(searchDirectory.getAbsolutePath());
    }
    public ImageFinder(List<File> searchDirectories) {
        super("ImageFinder");
        search = getDefaultSetupSearch();
        search.getDirectories().clear();
        for(File dir: searchDirectories) search.getDirectories().add(dir.getAbsolutePath());
    }
    public ImageFinder(Setup setup) {
        search = setup.getSearch();
    }

    private SetupSearch getDefaultSetupSearch() {
        search = new SetupSearch();
        search.setAddedAfterFlag(false);
        search.setAddedBeforeFlag(false);
        search.setStartWithFlag(false);
        search.setNotOlderFlag(false);
        return search;
    }


    public void addImageListener(ImageListener listener) {
        getImageListeners().add(listener);
    }

    public void removeImageListener(ImageListener listener) {
        getImageListeners().remove(listener);
    }

    public void notifyImageListeners(ImageInfo event) {
        for(ImageListener listener : imageListeners) {
            listener.imageFound(event);
        }
    }
    
    public void addProgressListener(ImageFinderProgressListener listener) {
        progressListeners.add(listener);
    }
    public void removeProgressListener(ImageFinderProgressListener listener) {
        progressListeners.remove(listener);
    }
    public void notifyProgressListeners(ImageFinderProgress progress) {
        for(ImageFinderProgressListener listener : progressListeners) {
            listener.progressMade(progress);
        }
    }
    

    private long notOlderTime=0;
    private long alreadySentTime=0;
    public void run() {
        count=0;
        
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
        gc.set(GregorianCalendar.MINUTE, 0);
        switch(search.getNotOlder()) {
            case ONE_DAY:      gc.add(GregorianCalendar.DAY_OF_YEAR, -1); break;
            case TWO_DAY:      gc.add(GregorianCalendar.DAY_OF_YEAR, -2); break;
            case THREE_DAYS:   gc.add(GregorianCalendar.DAY_OF_YEAR, -3); break;
            case ONE_WEEK:     gc.add(GregorianCalendar.WEEK_OF_YEAR, -1); break;
            case TWO_WEEKS:    gc.add(GregorianCalendar.WEEK_OF_YEAR, -2); break;
            case ONE_MONTH:    gc.add(GregorianCalendar.MONTH, -1); break;
            case TWO_MONTHS:   gc.add(GregorianCalendar.MONTH, -2); break;
            case THREE_MONTHS: gc.add(GregorianCalendar.MONTH, -3); break;
            case SIX_MONTHS:   gc.add(GregorianCalendar.MONTH, -6); break;
            case ONE_YEAR:     gc.add(GregorianCalendar.YEAR,  -1); break;
        }
        notOlderTime = gc.getTimeInMillis();
        
        
        gc = new GregorianCalendar();
        gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
        gc.set(GregorianCalendar.MINUTE, 0);
        switch(search.getAlreadySent()) {
            case ONE_DAY:      gc.add(GregorianCalendar.DAY_OF_YEAR, -1); break;
            case TWO_DAY:      gc.add(GregorianCalendar.DAY_OF_YEAR, -2); break;
            case THREE_DAYS:   gc.add(GregorianCalendar.DAY_OF_YEAR, -3); break;
            case ONE_WEEK:     gc.add(GregorianCalendar.WEEK_OF_YEAR, -1); break;
            case TWO_WEEKS:    gc.add(GregorianCalendar.WEEK_OF_YEAR, -2); break;
            case ONE_MONTH:    gc.add(GregorianCalendar.MONTH, -1); break;
            case TWO_MONTHS:   gc.add(GregorianCalendar.MONTH, -2); break;
            case THREE_MONTHS: gc.add(GregorianCalendar.MONTH, -3); break;
            case SIX_MONTHS:   gc.add(GregorianCalendar.MONTH, -6); break;
            case ONE_YEAR:     gc.add(GregorianCalendar.YEAR,  -1); break;
        }
        alreadySentTime = gc.getTimeInMillis();
        
        
        log.fine("Image finger started");
        for(File dir: optimize(search.getDirectories())) {
            log.fine("Looking for images within root directory "+dir);
            try {
                search(dir);
            } catch(InterruptedException e) {
                log.fine("Image finger has been interrupted");
                break;
            }
        }
        notifyProgressListeners(new ImageFinderProgress(count));
        notifyImageListeners(null);
        log.fine("Image finger finished");
    }
    
    class FileComparator implements Comparator<File> {
        public int compare(File o1, File o2) {
            if (o1.lastModified() == o2.lastModified()) return 0;
            if (o1.lastModified() < o2.lastModified())
                return -1;
            else
                return 1;
        }
        
    }

    protected void search(File dir) throws InterruptedException {
        if (dir==null) return;
        if (!dir.isDirectory()) return;
        File[] files = dir.listFiles(new ImageFilenameFilter());
        Arrays.sort(files, new FileComparator());

        if (files==null) return;
        notifyProgressListeners(new ImageFinderProgress(dir.getAbsolutePath(), count));
        for(File file:files) {
            Thread.sleep(1); // Pour permettre l'interrupt
            if (search.isStartWithFlag()   && !file.getName().toLowerCase().startsWith(search.getStartWith())) continue;
            if (search.isAddedAfterFlag()  && (search.getAddedAfter().getTime() > file.lastModified())) continue;
            if (search.isAddedBeforeFlag() && (search.getAddedBefore().getTime() < file.lastModified())) continue;
            if (search.isNotOlderFlag()    && (notOlderTime > file.lastModified())) continue;
            if (search.isAlreadySentFlag() || search.isCommentsKeywordsFlag() || search.isKeywordsFlag()) {
                ImageInfo ii = new ImageInfo(file);
                // ----------------------------
                if (search.isAlreadySentFlag()) {
                    if (ii.getLastSent() != null) {
                        if (alreadySentTime > ii.getLastSent().getTime()) continue;
                    } else continue;
                }
                // ----------------------------
                if (search.isCommentsKeywordsFlag()) {
                    if (search.getCommentsKeywords() == null || search.getCommentsKeywords().length() == 0) {
                        if (ii.getComments() != null && ii.getComments().length() > 0) {
                            continue; // Dans ce cas on ne prend que les photos sans commentaires
                        }
                    } else {
                        if (ii.getComments() == null || ii.getComments().length()==0) continue;
                        String comments = ii.getComments().toLowerCase();
                        boolean ok=true;
                        // On ne prend la photo que si elle contient tous les mots clï¿œs
                        for(String keyword : search.getCommentsKeywords().toLowerCase().split("\\s+")) {
                            if (!comments.contains(keyword)) {
                                ok=false;
                                break;
                            }
                        }
                        if (!ok) continue;
                    }
                }
                // ----------------------------
                if (search.isKeywordsFlag()) {
                    if (search.getKeywords() == null || search.getKeywords().length() == 0) {
                        if (ii.getKeywords() != null && ii.getKeywords().length() > 0) {
                            continue; // Dans ce cas on ne prend que les photos sans commentaires
                        }
                    } else {
                        if (ii.getKeywords() == null || ii.getKeywords().length()==0) continue;
                        String keywords = ii.getKeywords().toLowerCase();
                        boolean ok=true;
                        // On ne prend la photo que si elle contient tous les mots clï¿œs
                        for(String keyword : search.getKeywords().toLowerCase().split("\\s+")) {
                            if (!keywords.contains(keyword)) {
                                ok=false;
                                break;
                            }
                        }
                        if (!ok) continue;
                    }
                }
            }
            count++;
            notifyImageListeners(new ImageInfo(file)); // TODO : Raise a InstanciationException in ImageInfo constructor
        }

        File[] dirs = dir.listFiles(new ImageDirectoryFilter());
        for(File fdir:dirs) {
            Thread.sleep(1); // Pour permettre l'interrupt
            search(fdir);
        }
    }

    public List<ImageListener> getImageListeners() {
        return imageListeners;
    }

    public void setImageListeners(List<ImageListener> imageListeners) {
        this.imageListeners = imageListeners;
    }

    /**
     * Checks and optimizes directories :
     * Don't make the same search two times...
     */
    private List<File> optimize(List<String> directories) {
        List<File> dirs = new ArrayList<File>();
        List<File> dir2ret = new ArrayList<File>();
        for(String dirname: directories) {
            File dir = new File(dirname);
            // On ignore les rï¿œpertoires n'existant pas sur le file systï¿œme
            if (!dir.exists()) {
                log.warning("Directory "+dir+" doesn't exists, continuing...");
                // TODO - Traiter le cas ou le rï¿œpertoire n'existe pas
                continue;
            }
            // On calcul le rï¿œpertoire "unique" correspondant ï¿œ ce fichier
            try {
                dir = dir.getCanonicalFile();
            } catch(IOException e) {
                log.severe("Failed to get unique directory name for "+dir+", continuing...");
                // TODO - Traiter cette erreur, quand il n'est pas possible de rï¿œcupï¿œrer le fichier 'canonique'
                continue;
            }
            // On ne prend pas un mï¿œme rï¿œpertoire prï¿œsent plusieurs fois
            boolean alreadyAdded=false;
            for(File d:dirs) {
                if (dir.equals(d)) {
                    alreadyAdded=true;
                    break;
                }
            }
            if (!alreadyAdded) dirs.add(dir);
        }
        // On ne prend pas en compte les rï¿œpertoires inclus dans d'autres
        for(File dir2chk: dirs) {
            boolean takeThisDir=true;
            for(File dir: dirs) {
                if (dir2chk.equals(dir)) continue;
                String dirname2chk = dir2chk.getAbsolutePath();
                String dirname = dir.getAbsolutePath();
                if ( (dirname.length() < dirname2chk.length()) &&
                     (dirname2chk.startsWith(dirname)) ) {
                    takeThisDir=false;
                    break;
                }
            }
            if (takeThisDir) {
                dir2ret.add(dir2chk);
            }
        }
        return dir2ret;
    }
}

