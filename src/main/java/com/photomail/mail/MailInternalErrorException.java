/*
 * MailInternalException.java
 *
 * Created on 18 avril 2005, 14:09
 */

package com.photomail.mail;

import com.photomail.setup.Resources;
import com.photomail.setup.Setup;

/**
 *
 * @author dcr
 */
public class MailInternalErrorException extends MailException {
    
    public MailInternalErrorException(Throwable cause) {
        super(String.format(Resources.getString("mail.msg.error.internal"), Setup.getLogDir().getPath()), cause);
    }
    
}
