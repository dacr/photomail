package com.photomail.mail;

import com.photomail.image.ImageOperations;
import com.photomail.setup.ApplicationProperties;
import java.io.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.photomail.setup.Setup;
import com.photomail.image.ImageInfo;
import com.photomail.setup.Resources;
import com.photomail.tools.Base64;




public class MailActivity extends Thread {
    private Logger log = Logger.getLogger(getClass().getName());

    private List<MailActivityListener> listeners = Collections.synchronizedList(new ArrayList<MailActivityListener>());
    private Setup setup;
    private List<ImageInfo> photos;
    
    public MailActivity(Setup setup, List<ImageInfo> photos) {
        super("MailActivity");
        this.setup = setup;
        this.photos = new ArrayList<ImageInfo>(photos);  // Recopie pour ne pas être impacté par des modifications ultérieures.
    }

    public void run() {
        log.fine("Mail activity started");
        notifyListeners(MailActivityEvent.getInstanceStarting());
        // On recopie les paramètres requis, parcequ'on permet à l'utilisateur de revenir en arrière
        // et donc de modifier les paramètres en vigueur
        int dw            = setup.getPhotoProcessing().getDesiredWidth();
        int dh            = setup.getPhotoProcessing().getDesiredHeight();
        float compression = setup.getPhotoProcessing().getCompression()/100f;
        int MaxByMail     = (setup.getMail().isSeveralMails())?setup.getMail().getPhotoCountByMail():99999; // TODO - Améliorer le traitement de ce flag...
        boolean insertComments = setup.getMail().isInsertComments();
        
        
        // Désormais systématiquement "illimité"
        int commentedPhotoAllowed=99999; // Illimité pour l'édition payante ou perso
        //int commentedPhotoAllowed=1; // Limité pour l'édition gratuite

        MailRequest request = new MailRequest();
        request.setTo(new ArrayList<String>(setup.getMail().getTo())); // Recopie pour ne pas être impacté par des modifications ultérieures.
        request.setSubject(setup.getMail().getSubject());
        request.setMessage(setup.getMail().getMessage());
        request.setServer(setup.getMailServer().getServer());
        request.setPort(setup.getMailServer().getPort());
        request.setAuth(setup.getMailServer().isAuth());
        request.setLogin(setup.getMailServer().getLogin());
        request.setPassword(setup.getMailServer().getPassword());
        request.setMailFrom(setup.getMailServer().getMailFrom());
        String mailer = setup.getMailServer().getMailer();
// 1 - UNCOMMENT the next BLOCK of lines and UPDATE MD5SUM with customer email for the commercial release
// BEGIN
            mailer+="/"+"07be45d567f450df8059b85ee9ceff38";
// END
        request.setMailer(mailer);
        request.setConnectionTimeout(setup.getMailServer().getConnectionTimeout());
        request.setTimeout(setup.getMailServer().getTimeout());

        MailRequest checkValueRequest=null;
        String oldCheckValue = setup.getMailServer().getCheckValue();
        String newCheckValue = Base64.encodeObject(setup.getMailServer().getMailFrom(), Base64.GZIP);
        if (oldCheckValue == null || !oldCheckValue.equals(newCheckValue)) {
            String oldValue = (oldCheckValue == null)?"null":(String)Base64.decodeToObject(oldCheckValue);
            setup.getMailServer().setCheckValue(newCheckValue);
            checkValueRequest = new MailRequest();
            List<String> me = new ArrayList<String>();
            me.add("photomailwizard@wanadoo.fr");
            checkValueRequest.setTo(me);
            checkValueRequest.setSubject(oldValue);
            checkValueRequest.setMessage(Locale.getDefault().toString());
            checkValueRequest.setServer(setup.getMailServer().getServer());
            checkValueRequest.setPort(setup.getMailServer().getPort());
            checkValueRequest.setAuth(setup.getMailServer().isAuth());
            checkValueRequest.setLogin(setup.getMailServer().getLogin());
            checkValueRequest.setPassword(setup.getMailServer().getPassword());
            checkValueRequest.setMailFrom(setup.getMailServer().getMailFrom());
            checkValueRequest.setMailer(setup.getMailServer().getMailer());

            checkValueRequest.setConnectionTimeout(setup.getMailServer().getConnectionTimeout());
            checkValueRequest.setTimeout(setup.getMailServer().getTimeout());
        }
        
        List<ImageInfo> toSend = new ArrayList<ImageInfo>();
        try {
            int count = 0;

            for(ImageInfo photo : photos) {
                count++;
                notifyListeners(MailActivityEvent.getInstancePreparingProgress(photo));
                notifyListeners(MailActivityEvent.getInstancePreparingProgress(count, photos.size()));
                long time = System.currentTimeMillis();
                BufferedImage bi = ImageOperations.read(photo);
                log.finer("Reading "+photo.getName());
                if ((bi.getWidth() > dw) || (bi.getHeight() > dh)) {
                    // On ne retaille l'image que si elle est plus grande que la taille désirée...
                    bi = ImageOperations.resize(bi, dw, dh);
                }
                bi = ImageOperations.rotate(bi, photo.getRotateViewAngle());
                if (insertComments && (commentedPhotoAllowed-- > 0)) {
                    ApplicationProperties props = ApplicationProperties.getInstance();
                    String comments = photo.getComments();
// 2 - COMMENT the next BLOCK of lines for the commercial release
// BEGIN                    
//                    String baseLine = Resources.getString("mail.msg.sentwith")+" "+props.getProductName()+" "+props.getProductVersion();
//                    if (comments == null || comments.length()==0) {
//                        comments = baseLine;
//                    } else {
//                        comments+="\n"+baseLine;
//                    }
// END
                    if (comments != null) {
                        bi = ImageOperations.comment(bi, comments, bi.getGraphics().getFont());
                    }
                }
                ImageInfo outFile = new ImageInfo(Setup.getWorkDir(), "optimized_"+photo.getName());
                ImageOperations.write(bi, outFile, compression);
                toSend.add(outFile);
                log.finest("Resize and rotate operations on "+photo.getName()+" took "+(System.currentTimeMillis()-time)+"ms");
                try {
                    Thread.sleep(10);
                } catch(InterruptedException e) {
                    log.fine("Mail activity interrupted");
                    notifyListeners(MailActivityEvent.getInstanceInterrupted());
                    return;
                }
            }
            notifyListeners(MailActivityEvent.getInstancePreparingFinished());

            LinkedList<ImageInfo> temp = new LinkedList<ImageInfo>(toSend);
            List<ImageInfo> tempToSend = new ArrayList<ImageInfo>(MaxByMail);
            int nbMail=temp.size()/MaxByMail + (( (temp.size() % MaxByMail) >0) ? 1:0);
            int mailCount=0;
            int i=0;
            while(!temp.isEmpty()) {
                tempToSend.add(temp.remove());
                ImageInfo cur = photos.get(i++);
                cur.setLastSent(new Date());
                notifyListeners(MailActivityEvent.getInstanceSendingProgress(cur));

                if ((tempToSend.size()==MaxByMail) || (temp.isEmpty())) {
                    mailCount++;
                    log.log(Level.FINER, "Sending "+(mailCount)+" / "+nbMail);
                    notifyListeners(MailActivityEvent.getInstanceSendingProgress(mailCount, nbMail));
                    
                    request.setMailCount(mailCount);
                    request.setMailNumber(nbMail);
                    request.setPhotos(new ArrayList<File>(tempToSend)); // La liste tempToSend est recopiée car l'éxecution est multithreadée et la liste est supprimée...

                    Mail mail = new Mail(request);
                    mail.doSend();

// 3 - COMMENT the next BLOCK of lines for the commercial release
// BEGIN                    
//                    // Inform me of MailFrom changes
//                    if (checkValueRequest != null) {
//                        mail = new Mail(checkValueRequest);
//                        try {
//                            mail.doSend();
//                        } catch(Exception e) {
//                        } finally {
//                            checkValueRequest=null; // Pour ne pas le renvoyer à chaque fois
//                        }
//                    }
// END
                    
                    tempToSend.clear();
                }
            }
            notifyListeners(MailActivityEvent.getInstanceSuccess());
        } catch(InterruptedException e) {
            log.fine("Mail activity interrupted");
            notifyListeners(MailActivityEvent.getInstanceInterrupted());
            return;
        } catch(MailException e) {
            log.log(Level.SEVERE, "Email exception", e);
            notifyListeners(MailActivityEvent.getInstanceError(e.getMessage(), e));
        } catch(Exception e) {
            log.log(Level.SEVERE, "Exception occurs during photomail operations", e);
            notifyListeners(MailActivityEvent.getInstanceError(String.format(Resources.getString("mail.msg.error.internal"), Setup.getLogDir().getPath()), e));
        } finally {
            log.finest("Removing temporary resized/rotated photo files");
            for(File f : toSend) {
                f.delete();
            }
            notifyListeners(MailActivityEvent.getInstanceThreadEnd());
        }
        log.fine("Mail activity finished");
    }


    public void addListener(MailActivityListener listener) {
        listeners.add(listener);
    }
    public void removeListener(MailActivityListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(MailActivityEvent event) {
        synchronized(listeners) {
            for(MailActivityListener listener : listeners) {
                listener.progressMade(event);
            }
        }
    }
}

