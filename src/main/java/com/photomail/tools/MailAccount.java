/*
 * MailAccount.java
 *
 * Created on 29 juin 2005, 18:50
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.photomail.tools;

/**
 *
 * @author David Crosson
 */
public class MailAccount implements Comparable<MailAccount> {
    private String foundFrom;
    private String emailAddress;
    private String smtpServer;
    private boolean smtpUseAuth;
    private String smtpUsername;
    private int smtpPort;
    public MailAccount(String foundFrom) {
        this.foundFrom = foundFrom;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public boolean isSmtpUseAuth() {
        return smtpUseAuth;
    }

    public void setSmtpUseAuth(boolean smtpUseAuth) {
        this.smtpUseAuth = smtpUseAuth;
    }

    public String getSmtpUsername() {
        return smtpUsername;
    }

    public void setSmtpUsername(String smtpUsername) {
        this.smtpUsername = smtpUsername;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String toString() {
        return String.format("%s %s:%d", emailAddress, smtpServer, smtpPort);
    }
    
    /**
     * Construit la chaine de caractère matérialisant la forme "unique" de cet objet 
     * c'est à dire que deux instances ayant cette même chaine seront égales...
     * Permet d'enlever automatiquement les doublons
     */
    private String getUnique() {
        return ""+emailAddress+smtpServer+smtpPort;
    }
    public int hashCode() {
        return getUnique().hashCode();
    }
    
    public int compareTo(MailAccount to) {
        return getUnique().compareTo(to.getUnique());
    }
    public boolean equals(Object o) {
        if (!(o instanceof MailAccount)) return false;
        return getUnique().equals(((MailAccount)o).getUnique());
    }
}
