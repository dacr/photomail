/*
 * MailErrorException.java
 *
 * Created on 27 septembre 2004, 21:58
 */

package com.photomail.mail;

import com.photomail.*;

/**
 *
 * @author  dcr
 */
public class MailException extends Exception {
    
    public MailException(String msg) {
        super(msg);
    }

    public MailException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
