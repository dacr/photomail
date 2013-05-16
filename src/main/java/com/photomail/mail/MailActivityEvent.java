/*
 * MailActivityEvent.java
 *
 * Created on 11 avril 2005, 12:06
 */

package com.photomail.mail;

import com.photomail.image.ImageInfo;
import java.util.List;

/**
 *
 * @author dcr
 */
public class MailActivityEvent {
    private MailActivityEventType type;
    private int prepareImageCount;
    private int prepareImageTotal;
    private int mailCount;
    private int mailTotal;
    private ImageInfo imageInfo;
    private Throwable exception;
    private String error;

    private MailActivityEvent() {
    }

    public static MailActivityEvent getInstanceThreadEnd() {
        MailActivityEvent event = new MailActivityEvent();
        event.setType(MailActivityEventType.THREAD_END);
        return event;
    }

    public static MailActivityEvent getInstanceStarting() {
        MailActivityEvent event = new MailActivityEvent();
        event.setType(MailActivityEventType.STARTING);
        return event;
    }

    public static MailActivityEvent getInstanceSuccess() {
        MailActivityEvent event = new MailActivityEvent();
        event.setType(MailActivityEventType.SUCCESS);
        return event;
    }

    public static MailActivityEvent getInstanceError(String error, Throwable exception) {
        MailActivityEvent event = new MailActivityEvent();
        event.setType(MailActivityEventType.ERROR);
        event.setException(exception);
        event.setError(error);
        return event;
    }

    public static MailActivityEvent getInstanceInterrupted() {
        MailActivityEvent event = new MailActivityEvent();
        event.setType(MailActivityEventType.INTERRUPTED);
        return event;
    }
    
    public static MailActivityEvent getInstanceErrorConnection(String error, Throwable exception) {
        MailActivityEvent event = new MailActivityEvent();
        event.setType(MailActivityEventType.ERROR_CONNECTION);
        event.setException(exception);
        event.setError(error);
        return event;
    }

    public static MailActivityEvent getInstanceErrorLogin(String error, Throwable exception) {
        MailActivityEvent event = new MailActivityEvent();
        event.setType(MailActivityEventType.ERROR_LOGIN);
        event.setException(exception);
        event.setError(error);
        return event;
    }

    public static MailActivityEvent getInstancePreparingProgress(int prepareImageCount, int prepareImageTotal) {
        MailActivityEvent event = new MailActivityEvent();
        event.setType(MailActivityEventType.PREPARING_IN_PROGRESS);
        event.setPrepareImageCount(prepareImageCount);
        event.setPrepareImageTotal(prepareImageTotal);
        return event;
    }
    
    public static MailActivityEvent getInstancePreparingFinished() {
        MailActivityEvent event = new MailActivityEvent();
        event.setType(MailActivityEventType.PREPARING_FINISHED);
        return event;
    }

    public static MailActivityEvent getInstanceSendingProgress(int mailCount, int mailTotal) {
        MailActivityEvent event = new MailActivityEvent();
        event.setType(MailActivityEventType.SENDING_IN_PROGRESS);
        event.setMailCount(mailCount);
        event.setMailTotal(mailTotal);
        return event;
    }

    public static MailActivityEvent getInstancePreparingProgress(ImageInfo imageInfo) {
        MailActivityEvent event = new MailActivityEvent();
        event.setType(MailActivityEventType.PREPARING_IMAGE);
        event.setImageInfo(imageInfo);
        return event;
    }

    public static MailActivityEvent getInstanceSendingProgress(ImageInfo imageInfo) {
        MailActivityEvent event = new MailActivityEvent();
        event.setType(MailActivityEventType.SENDING_IMAGE);
        event.setImageInfo(imageInfo);
        return event;
    }



    public MailActivityEventType getType() {
        return type;
    }

    public void setType(MailActivityEventType type) {
        this.type = type;
    }

    public int getPrepareImageCount() {
        return prepareImageCount;
    }

    public void setPrepareImageCount(int prepareImageCount) {
        this.prepareImageCount = prepareImageCount;
    }

    public int getPrepareImageTotal() {
        return prepareImageTotal;
    }

    public void setPrepareImageTotal(int prepareImageTotal) {
        this.prepareImageTotal = prepareImageTotal;
    }

    public int getMailCount() {
        return mailCount;
    }

    public void setMailCount(int mailCount) {
        this.mailCount = mailCount;
    }

    public int getMailTotal() {
        return mailTotal;
    }

    public void setMailTotal(int mailTotal) {
        this.mailTotal = mailTotal;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    public void setImageInfo(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }

}
