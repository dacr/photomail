/*
 * MailRequest.java
 *
 * Created on 27 septembre 2004, 23:20
 */

package com.photomail.mail;

import java.util.List;
import java.io.File;
import com.photomail.*;

/**
 *
 * @author  dcr
 */
public class MailRequest {
    private List<String>  to;
    private String        subject;
    private String        message;
    private String        server;
    private int           port;
    private boolean       auth;
    private String        login;
    private String        password;
    private String        mailFrom;
    private String        mailer;
    private int           connectionTimeout;
    private int           timeout;
    private int           mailCount;
    private int           mailNumber;
    private List<File>    photos;


    public MailRequest() {
    }
    /** Creates a new instance of MailRequest */
    public MailRequest(List<String>  to,
                       String        subject,
                       String        message,
                       String        server,
                       int           port,
                       boolean       auth,
                       String        login,
                       String        password,
                       String        mailFrom,
                       String        mailer,
                       int           connectionTimeout,
                       int           timeout,
                       List<File>    photos,
                       int           mailCount,
                       int           mailNumber) {
        setTo(to);
        setSubject(subject);
        setMessage(message);
        setServer(server);
        setPort(port);
        setAuth(auth);
        setLogin(login);
        setPassword(password);
        setMailFrom(mailFrom);
        setMailer(mailer);
        setConnectionTimeout(connectionTimeout);
        setTimeout(timeout);
        setMailCount(mailCount);
        setMailNumber(mailNumber);
        setPhotos(photos);
    }

    public int getMailCount() {
        return mailCount;
    }

    public void setMailCount(int mailCount) {
        this.mailCount=mailCount;
    }

    public int getMailNumber() {
        return mailNumber;
    }

    public void setMailNumber(int mailNumber) {
        this.mailNumber=mailNumber;
    }

    public List<File> getPhotos() {
        return photos;
    }

    public void setPhotos(List<File> photos) {
        this.photos=photos;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailer() {
        return mailer;
    }

    public void setMailer(String mailer) {
        this.mailer = mailer;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
}
