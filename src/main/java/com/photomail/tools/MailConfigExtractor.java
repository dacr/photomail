/*
 * SmtpConfigExtractor.java
 *
 * Created on 29 juin 2005, 18:49
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.photomail.tools;

import ca.beq.util.win32.registry.KeyIterator;
import ca.beq.util.win32.registry.RegistryException;
import ca.beq.util.win32.registry.RegistryKey;
import ca.beq.util.win32.registry.RootKey;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author David Crosson
 */
public class MailConfigExtractor {
    private static Logger log = Logger.getLogger(MailConfigExtractor.class.getName());

    /** Creates a new instance of SmtpConfigExtractor */
    private MailConfigExtractor() {
    }
    
    
    
    /**
     * Returns found mail account
     */
    public static Set<MailAccount> findMailAccount() throws IOException{
        Set<MailAccount> foundAccounts = new TreeSet<MailAccount>();
        addMailAccountOutlookExpress(foundAccounts);
        addMailAccountThunderBirdOrMozillaMail(foundAccounts);
        return foundAccounts;
    }

    
    
    /**
     * Récupération des configurations SMTP disponibles sous OutlookExpress
     * Effectue des accès à la base de registre
     */
    private static void addMailAccountOutlookExpress(Set<MailAccount> foundAccounts) {
        String osname   = System.getProperty("os.name").toLowerCase();
        if (osname.contains("linux")) return;
        
        String dllResName = "jRegistryKey.dll";
        InputStream inputStream = MailConfigExtractor.class.getResourceAsStream("/"+dllResName);
        if (inputStream == null) {
            log.severe("Couldn't find dll ressource named "+dllResName);
            return;
        }
        String tmpDirname = System.getProperty("java.io.tmpdir");
        File dllFile = new File(tmpDirname,  dllResName);

        try {
            if (!dllFile.exists()) {
                log.finest("Copying dll on disk to "+dllFile);
                FileToolBox.inputStreamToFile(inputStream, dllFile);
            }
        } catch(IOException e) {
            log.log(Level.SEVERE, "Was unable to copy required dll file", e);
            //return;
        }

        try {
            RegistryKey.initialize(dllFile.getAbsolutePath());
        } catch(Exception e) {
            log.log(Level.SEVERE, "Was unable to initialize registry accesss dll, trying something else", e);
            try {
                RegistryKey.initialize(dllResName);
            } catch(Exception e2) {
                log.log(Level.SEVERE, "Was unable to initialize registry accesss dll", e2);
                if (!RegistryKey.isInitialized()) return;
            }
        }

        RootKey r = RootKey.HKEY_CURRENT_USER;
        RegistryKey base = new RegistryKey(r, "Software\\Microsoft\\Internet Account Manager\\Accounts");
        KeyIterator i = new KeyIterator(base);
        Pattern pattern = Pattern.compile("0+[0-9]+");
        while(i.hasNext()) {
            RegistryKey accountKey = (RegistryKey)i.next();
            String name = accountKey.getName();
            Matcher matcher = pattern.matcher(name);
            if (matcher.matches()) {
                MailAccount account = new MailAccount("OutlookExpress");
                try {
                    account.setEmailAddress(accountKey.getValue("SMTP Email Address").getStringValue());
                } catch(RegistryException e) {
                }
                try {
                    account.setSmtpServer(accountKey.getValue("SMTP Server").getStringValue());
                } catch(RegistryException e) {}
                try {
                    account.setSmtpPort(((Integer)accountKey.getValue("SMTP Port").getData()).intValue());
                } catch(RegistryException e) {
                    account.setSmtpPort(25);
                }
                try {
                    account.setSmtpUsername(accountKey.getValue("SMTP User Name").getStringValue());
                    account.setSmtpUseAuth(true);
                }catch(RegistryException e) {}

                if (account.getEmailAddress() != null && account.getSmtpServer() != null) {
                    foundAccounts.add(account);
                }
            }
        }
    }
    
    /**
     * Récupération des configurations SMTP disponibles sous Firefox ou Mozilla
     *
     * Sous windows : $HOME\Application Data\Thunderbird\Profiles\{profile dirname}\prefs.js
     * Sous Unix    : $HOME/.thunderbird/{profile dirname}/prefs.js
     * Algo :
     *     - Rechercher récursivement les fichiers prefs.js et pour chacun d'entre eux :
     *        + rechercher "mail.accountX.identities", "idY"
     *        + 
     */
    private static void addMailAccountThunderBirdOrMozillaMail(Set<MailAccount> foundAccounts) throws IOException {
        List<File> foundPrefsFile = new ArrayList<File>();
        List<File> searchDirs = new ArrayList<File>();
        String userhome = System.getProperty("user.home");
        String osname   = System.getProperty("os.name").toLowerCase();
        if (osname.contains("linux")) {
            searchDirs.add(new File(userhome+File.separator+".thunderbird"));
            searchDirs.add(new File(userhome+File.separator+".mozilla"));
        } else {
            if (osname.contains("windows")) {
                searchDirs.add(new File(userhome+File.separator+"Application Data"+File.separator+"Thunderbird"));
                searchDirs.add(new File(userhome+File.separator+"Application Data"+File.separator+"Mozilla"));
            } else {
                searchDirs.add(new File(userhome+File.separator));
            }
        }
        for(File searchDir: searchDirs) {
            FileToolBox.recursiveFileSearch(searchDir, new FilterFileButNotDirectory("prefs.js", false), foundPrefsFile);
        }
        
        
        for(File prefsFile: foundPrefsFile) {
            Properties props = prefs2Properties(prefsFile);
            String accountsString = props.getProperty("mail.accountmanager.accounts");
            String[] accounts = new String[0];
            if (accountsString == null || accountsString.length()==0) {
                String defaultAccount = props.getProperty("mail.accountmanager.defaultaccount");
                if (defaultAccount != null && defaultAccount.length()>0) {
                    accounts = new String[1];
                    accounts[0] = defaultAccount;
                }
            } else {
                accounts = accountsString.split(",");
            }
            for(String accountId: accounts) {
                String accountKey = "mail.account."+accountId+".identities";
                String idKey = props.getProperty(accountKey);
                if (idKey==null) continue;
                String idSmtp = props.getProperty("mail.identity."+idKey+".smtpServer");
                if (idSmtp==null) {
                    idSmtp = props.getProperty("mail.smtp.defaultserver");
                }
                String emailAddress = props.getProperty("mail.identity."+idKey+".useremail");
                String smtpServer   = props.getProperty("mail.smtpserver."+idSmtp+".hostname");
                String smtpUsername = props.getProperty("mail.smtpserver."+idSmtp+".username");
                int smtpPort;
                try {
                    String smtpPortString = props.getProperty("mail.smtpserver."+idSmtp+".port");
                    if (smtpPortString == null || smtpPortString.length()==0) smtpPort=25;
                    else smtpPort = Integer.parseInt(smtpPortString);
                } catch(NumberFormatException e) {
                    smtpPort = 25;
                }
                MailAccount account = new MailAccount("Mozilla/ThunderBird");
                account.setEmailAddress(emailAddress);
                account.setSmtpServer(smtpServer);
                account.setSmtpPort(smtpPort);
                account.setSmtpUsername(smtpUsername);
                if (smtpUsername != null || smtpUsername.length()>0) {
                    account.setSmtpUseAuth(true);
                }
                foundAccounts.add(account);
            }
        }
    }


    /**
     * Converts mozilla / thunderbirds into java properties
     */
    public static Properties prefs2Properties(File file) throws IOException {
        Pattern linePattern = Pattern.compile("^user_pref\\(\"([0-9a-zA-Z_.]+)\"\\s*,\\s*(.+)\\s*\\);\\s*$");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;
        Properties props = new Properties();
        while((line=reader.readLine()) != null) {
            Matcher matcher = linePattern.matcher(line);
            if (matcher.matches()) {
                String key = matcher.group(1);
                String value = matcher.group(2);
                if (value.startsWith("\"")) value = value.substring(1, value.length()-1);
                props.setProperty(key, value);
            }
        }
        return props;
    }

}
