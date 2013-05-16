/*
 * SetupMailServer.java
 *
 * Created on 22 septembre 2004, 22:44
 */

package com.photomail.setup;

import java.io.Serializable;

/**
 *
 * @author  dcr
 */
public class SetupMailServer implements Serializable {
    
    private String   server;
    private int      port=25;
    private boolean  auth=false;
    private String   login;
    private String   password;
    private String   mailFrom;
    private String   mailer="PhotoMailer";
    private int      connectionTimeout=20*1000;
    private int      timeout=80*1000;
    private int      retryMaxOnFailure=5;
    private long     retryDelay=10*1000;
    private String   checkValue;


    /** Creates a new instance of SetupMailServer */
    public SetupMailServer() {
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server=server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port=port;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth=auth;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login=login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password=password;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom=mailFrom;
    }

    public String getMailer() {
        return mailer;
    }

    public void setMailer(String mailer) {
        this.mailer=mailer;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout=connectionTimeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout=timeout;
    }

    public int getRetryMaxOnFailure() {
        return retryMaxOnFailure;
    }

    public void setRetryMaxOnFailure(int retryMaxOnFailure) {
        this.retryMaxOnFailure=retryMaxOnFailure;
    }

    public long getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(long retryDelay) {
        this.retryDelay=retryDelay;
    }

    public String getCheckValue() {
        return checkValue;
    }

    public void setCheckValue(String checkValue) {
        this.checkValue = checkValue;
    }
    
}
