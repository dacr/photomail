/*
 * SetupSearch.java
 *
 * Created on 7 avril 2005, 13:06
 */

package com.photomail.setup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author dcr
 */
public class SetupSearch {
    
    private List<String> directories;
    private boolean    startWithFlag;
    private String     startWith;
    private boolean    addedAfterFlag;
    private Date       addedAfter;
    private boolean    addedBeforeFlag;
    private Date       addedBefore;
    private boolean    notOlderFlag = false;
    private TimePeriod notOlder = TimePeriod.ONE_WEEK;
    private boolean    alreadySentFlag = false;
    private TimePeriod alreadySent = TimePeriod.ONE_MONTH;
    private boolean    commentsKeywordsFlag = false;
    private String     commentsKeywords;
    private boolean    keywordsFlag = false;
    private String     keywords;
    
    public SetupSearch(String... directories) {
        this.setDirectories(new ArrayList<String>());
        for(String directory: directories) this.getDirectories().add(directory);
    }
    public SetupSearch() {
    }

    public List<String> getDirectories() {
        return directories;
    }

    public void setDirectories(List<String> directories) {
        this.directories = directories;
    }

    public boolean isStartWithFlag() {
        return startWithFlag;
    }

    public void setStartWithFlag(boolean startWithFlag) {
        this.startWithFlag = startWithFlag;
    }

    public String getStartWith() {
        return startWith;
    }

    public void setStartWith(String startWith) {
        this.startWith = startWith;
    }

    public boolean isAddedAfterFlag() {
        return addedAfterFlag;
    }

    public void setAddedAfterFlag(boolean addedAfterFlag) {
        this.addedAfterFlag = addedAfterFlag;
    }

    public Date getAddedAfter() {
        return addedAfter;
    }

    public void setAddedAfter(Date addedAfter) {
        this.addedAfter = addedAfter;
    }

    public boolean isAddedBeforeFlag() {
        return addedBeforeFlag;
    }

    public void setAddedBeforeFlag(boolean addedBeforeFlag) {
        this.addedBeforeFlag = addedBeforeFlag;
    }

    public Date getAddedBefore() {
        return addedBefore;
    }

    public void setAddedBefore(Date addedBefore) {
        this.addedBefore = addedBefore;
    }

    public boolean isNotOlderFlag() {
        return notOlderFlag;
    }

    public void setNotOlderFlag(boolean notOlderFlag) {
        this.notOlderFlag = notOlderFlag;
    }

    public TimePeriod getNotOlder() {
        return notOlder;
    }

    public void setNotOlder(TimePeriod notOlder) {
        this.notOlder = notOlder;
    }

    public boolean isAlreadySentFlag() {
        return alreadySentFlag;
    }

    public void setAlreadySentFlag(boolean alreadySentFlag) {
        this.alreadySentFlag = alreadySentFlag;
    }

    public TimePeriod getAlreadySent() {
        return alreadySent;
    }

    public void setAlreadySent(TimePeriod alreadySent) {
        this.alreadySent = alreadySent;
    }

    public boolean isCommentsKeywordsFlag() {
        return commentsKeywordsFlag;
    }

    public void setCommentsKeywordsFlag(boolean commentsKeywordsFlag) {
        this.commentsKeywordsFlag = commentsKeywordsFlag;
    }

    public String getCommentsKeywords() {
        return commentsKeywords;
    }

    public void setCommentsKeywords(String commentsKeywords) {
        this.commentsKeywords = commentsKeywords.trim();
    }

    public boolean isKeywordsFlag() {
        return keywordsFlag;
    }

    public void setKeywordsFlag(boolean keywordsFlag) {
        this.keywordsFlag = keywordsFlag;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
