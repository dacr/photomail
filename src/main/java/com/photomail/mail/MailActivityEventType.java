/*
 * MailActivityEventType.java
 *
 * Created on 11 avril 2005, 12:26
 */

package com.photomail.mail;

/**
 *
 * @author dcr
 */
public enum MailActivityEventType {
    STARTING,
    PREPARING_IMAGE,
    PREPARING_IN_PROGRESS,
    PREPARING_FINISHED,
    SENDING_IMAGE,
    SENDING_IN_PROGRESS,
    ERROR_CONNECTION,
    ERROR_LOGIN,
    ERROR,
    INTERRUPTED,
    SUCCESS,
    THREAD_END;
}
