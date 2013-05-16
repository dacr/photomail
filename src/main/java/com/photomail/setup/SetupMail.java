/*
 * SetupMail.java
 *
 * Created on 22 septembre 2004, 22:44
 */

package com.photomail.setup;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author  dcr
 */
public class SetupMail implements Serializable {
    private List<String>  to;
    private String  subject;
    private String  message;
    private boolean severalMails=true;
    private int     photoCountByMail;
    private boolean insertComments=true;

    /** Creates a new instance of SetupMail */
    public SetupMail() {
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to=to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject=subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message=message;
    }

    public int getPhotoCountByMail() {
        return photoCountByMail;
    }

    public void setPhotoCountByMail(int photoCountByMail) {
        this.photoCountByMail=photoCountByMail;
    }

    public boolean isSeveralMails() {
        return severalMails;
    }

    public void setSeveralMails(boolean severalMails) {
        this.severalMails = severalMails;
    }

    public boolean isInsertComments() {
        return insertComments;
    }

    public void setInsertComments(boolean insertComments) {
        this.insertComments = insertComments;
    }
    
}
