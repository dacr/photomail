/*
 * Setup.java
 *
 * Created on 22 septembre 2004, 22:37
 */

package com.photomail.setup;

import com.photomail.gui.PanelMailServerAutoConfigSelect;
import com.photomail.tools.MailAccount;
import com.photomail.tools.MailConfigExtractor;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author  dcr
 */
public class Setup implements Serializable {
    
    private SetupMail                   mail;
    private List<SetupMailingListEntry> mailingList;
    private SetupMailServer             mailServer;
    private SetupPhotoProcessing        photoProcessing;
    private SetupSearch                 search;
    
    private static MailAccount selectAccount() {
        Set<MailAccount> accounts;
        try {
            accounts = MailConfigExtractor.findMailAccount();
        } catch(IOException e) {
            Logger log = Logger.getLogger(Setup.class.getName());
            log.log(Level.SEVERE, "Was unable to extract mail configs", e);
            return null;
        }
        if (accounts.size()==0) return null;
        if (accounts.size()==1) return accounts.iterator().next();
        
        // Sinon il faut demander à l'utilisateur quel compte utiliser
        return PanelMailServerAutoConfigSelect.choosePopup(null, accounts);
    }
    
    public static Setup getDefaultSetup() {
        ApplicationProperties props = ApplicationProperties.getInstance();
        MailAccount account = selectAccount();
        Setup setup = new Setup();

        List<String> to = new ArrayList<String>();
        to.add("photomailwizard@wanadoo.fr");
        setup.setMail(new SetupMail());
        setup.getMail().setTo(to);
        setup.getMail().setSubject("");
        setup.getMail().setMessage("");
        setup.getMail().setSeveralMails(true);
        setup.getMail().setPhotoCountByMail(5);

        setup.setMailServer(new SetupMailServer());
        setup.getMailServer().setMailer(String.format("%s", props.getProductName()));
        if (account == null) {
            //setup.getMailServer().setMailFrom("photomailwizard@wanadoo.fr");
            //setup.getMailServer().setServer("smtp.wanadoo.fr");
            setup.getMailServer().setPort(25);
            setup.getMailServer().setAuth(false);
            setup.getMailServer().setLogin(System.getProperty("user.name"));
        } else  {
            setup.getMailServer().setMailFrom(account.getEmailAddress());
            setup.getMailServer().setServer(account.getSmtpServer());
            setup.getMailServer().setPort(account.getSmtpPort());
            setup.getMailServer().setAuth(account.isSmtpUseAuth());
            setup.getMailServer().setLogin(account.getSmtpUsername());
        }

        List<SetupMailingListEntry> tmpList = new ArrayList<SetupMailingListEntry>();
        tmpList.add(new SetupMailingListEntry("author", "photomailwizard@wanadoo.fr"));
        setup.setPhotoProcessing(new SetupPhotoProcessing());
        setup.getPhotoProcessing().setCompression(65.0f);
        setup.getPhotoProcessing().setSize(PhotoSizes.R640x480);
        setup.setMailingList(tmpList);

        List<String> directories = new ArrayList<String>();
        directories.add(new File(System.getProperties().getProperty("user.home")).getAbsolutePath());
        setup.setSearch(new SetupSearch());
        setup.getSearch().setDirectories(directories);
        setup.getSearch().setStartWithFlag(false);
        setup.getSearch().setStartWith("dsc");
        setup.getSearch().setAddedAfterFlag(false);
        setup.getSearch().setAddedAfter(new Date(System.currentTimeMillis() - 1000L*60L*60L*24L*10L));
        setup.getSearch().setAddedBeforeFlag(false);
        setup.getSearch().setAddedBefore(new Date());
        setup.getSearch().setCommentsKeywordsFlag(false);
        setup.getSearch().setCommentsKeywords("");
        setup.getSearch().setKeywordsFlag(false);
        setup.getSearch().setKeywords("");

        return setup;
    }
    
    private static File getFile() {
        File setupFile  = new File(getWorkDirFile(), "setup.xml");
        return setupFile;
    }
    
    private static void save(Setup setup, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        XMLEncoder encoder = new XMLEncoder(out);
        encoder.writeObject(setup);
        encoder.close();
        out.close();            

    }

    public static Setup load() throws FileNotFoundException, IOException {
        Setup setup=null;
        File setupFile=getFile();
        if (setupFile.exists()) {
            FileInputStream in = new FileInputStream(setupFile);
            XMLDecoder decoder = new XMLDecoder(in);
            setup = (Setup)decoder.readObject();
            decoder.close();
            in.close();
        } else {
            setup = getDefaultSetup();
            save(setup, setupFile);
        }
        return setup;
    }

    public void save() throws IOException {
        save(this, getFile());
    }
    

    public static File getWorkDirFile() {
        return new File(ApplicationProperties.getInstance().getWorkDir());
    }

    public static void initDirectories() {
        String homeDirname = ApplicationProperties.getInstance().getWorkDir();

        File homeDirFile = new File(homeDirname);
        if (!homeDirFile.exists()) {
            homeDirFile.mkdirs();
        }
        File logDirFile = new File(homeDirFile, "logs");
        if (!logDirFile.exists()) {
            logDirFile.mkdirs();
        }
        File workDirFile = new File(homeDirFile, "work");
        if (!workDirFile.exists()) {
            workDirFile.mkdirs();
        }
        File thbDirFile = new File(homeDirFile, "data");
        if (!thbDirFile.exists()) {
            thbDirFile.mkdirs();
        }
        
    }
    
    public static File getDataDir() {
        return new File(getWorkDirFile(), "data/");
    }

    public static File getWorkDir() {
        return new File(getWorkDirFile(), "work/");
    }

    public static File getLogDir() {
        return new File(getWorkDirFile(), "logs/");
    }

    /** Creates a new instance of Setup */
    public Setup() {
    }    

    public SetupMail getMail() {
        return mail;
    }

    public void setMail(SetupMail mail) {
        this.mail=mail;
    }

    public SetupMailServer getMailServer() {
        return mailServer;
    }

    public void setMailServer(SetupMailServer mailServer) {
        this.mailServer=mailServer;
    }

    public SetupPhotoProcessing getPhotoProcessing() {
        return photoProcessing;
    }

    public void setPhotoProcessing(SetupPhotoProcessing photoProcessing) {
        this.photoProcessing=photoProcessing;
    }

    public List<SetupMailingListEntry> getMailingList() {
        return mailingList;
    }

    public void setMailingList(List<SetupMailingListEntry> mailingList) {
        this.mailingList=mailingList;
    }

    public SetupSearch getSearch() {
        return search;
    }

    public void setSearch(SetupSearch search) {
        this.search = search;
    }
}
