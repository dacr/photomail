/*
 * MailAuth.java
 *
 * Created on 27 septembre 2004, 21:42
 */

package com.photomail.mail;

import javax.mail.PasswordAuthentication;
import javax.mail.Authenticator;
import com.photomail.*;

/**
 *
 * @author  dcr
 */

class MailAuth extends Authenticator {
    private String username = null;
    private String password = null;
    public MailAuth(String user, String pwd)    {
        username = user;
        password = pwd;
    }
    protected PasswordAuthentication getPasswordAuthentication(){
        return new PasswordAuthentication(username,password);
    }
}