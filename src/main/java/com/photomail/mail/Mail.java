/*
 * MailSend.java
 *
 * Created on 22 janvier 2004, 09:13
 */

package com.photomail.mail;

import com.photomail.setup.Resources;
import com.sun.mail.smtp.SMTPAddressFailedException;
import com.sun.mail.smtp.SMTPAddressSucceededException;
import com.sun.mail.smtp.SMTPSendFailedException;
import com.sun.mail.smtp.SMTPTransport;
import java.io.File;
import java.io.InterruptedIOException;
import java.util.logging.Logger;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;


import javax.mail.SendFailedException;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.mail.AuthenticationFailedException;




/**
 *
 * @author  dcr
 */
public class Mail {
    private Logger log = Logger.getLogger(getClass().getName());
    private MailRequest request;
    
    public Mail(MailRequest request) {
        setRequest(request);
    }

    
    private void sendMessage(MailRequest request) throws MailException, InterruptedException {
        //SetupMailServer server = request.getMailServer();
        //SetupMail mail = request.getMail();
        MailAuth mailauth = null;
        if (request.isAuth()) {
            String user = request.getLogin();
            String password = request.getPassword();
            mailauth = new MailAuth(user, password);
            log.finest("Connecting to smtp with authentication; user="+user);
        }
        
        if (request.getMailFrom() == null || request.getMailFrom().length()==0) {
            throw new MailException(String.format(Resources.getString("mail.msg.error.fromundefined")));
        }
        if (request.getServer() == null || request.getServer().length()==0) {
            throw new MailException(String.format(Resources.getString("mail.msg.error.serverundefined")));
        }
        if (request.getPort() == 0) {
            throw new MailException(String.format(Resources.getString("mail.msg.error.portundefined")));
        }
        
        Properties p = new Properties();
        p.setProperty("mail.host", request.getServer());
        p.setProperty("mail.smtp.port" ,String.valueOf(request.getPort()));
        p.setProperty("mail.smtp.connectiontimeout", String.valueOf(request.getConnectionTimeout()));
        p.setProperty("mail.smtp.timeout", String.valueOf(request.getTimeout()));
        
        javax.mail.Session mailSession = javax.mail.Session.getInstance(p, mailauth);
        Message msg = new MimeMessage(mailSession);

        // -------------------- FROM
        try {
            msg.setFrom(new InternetAddress(request.getMailFrom()));
        } catch(AddressException ae) {
            throw new MailException(String.format(Resources.getString("mail.msg.error.invalidfromemail"), ae.getRef()));
        } catch(MessagingException me) {
            log.log(Level.SEVERE, "Can not initialize message FROM part", me);
            throw new MailInternalErrorException(me);
        }
        // -------------------- TO        
        try {
            InternetAddress recipients[] = new InternetAddress[request.getTo().size()];
            int i=0;
            for(String email:request.getTo()) recipients[i++] = new InternetAddress(email);
            msg.setRecipients(RecipientType.TO, recipients);
        } catch(AddressException ae) {
            log.log(Level.SEVERE, "Can not initialize message TO part", ae);
            throw new MailException(String.format(Resources.getString("mail.msg.error.invalidtoemail"), ae.getRef()));
        } catch(MessagingException me) {
            throw new MailInternalErrorException(me);
        }

        // -------------------- SUBJECT 
        try {
            if (request.getMailNumber()<=1) {
                msg.setSubject(request.getSubject());
            } else {
                msg.setSubject(request.getSubject()+" "+request.getMailCount()+"/"+request.getMailNumber());
            }
        } catch(MessagingException me) {
            log.log(Level.SEVERE, "Can not initialize message SUBJECT part", me);
            throw new MailInternalErrorException(me);
        }

        // -------------------- MESSAGE
        MimeBodyPart mbmessage = new MimeBodyPart();
        try {
            String message = request.getMessage();
            message+="\n\n"+Resources.getString("mail.msg.signature");
            mbmessage.setText(message);
            //mbmessage.addHeaderLine("Content-Type: text/plain; charset=\"ISO-8859-1\"");
            //mbmessage.addHeaderLine("Content-Transfer-Encoding: quoted-printable");
        } catch(MessagingException me) {
            log.log(Level.SEVERE, "Can not initialize message TEXT part", me);
            throw new MailInternalErrorException(me);
        }
        
        
        // -------------------- Assembling multi part message content 
        Multipart mp = new MimeMultipart();
        try {
            mp.addBodyPart(mbmessage);
            if (getRequest().getPhotos() != null) {
                for(File photo : getRequest().getPhotos()) {
                    MimeBodyPart mbpPhoto = new MimeBodyPart();
                    FileDataSource fds = new FileDataSource(photo);
                    mbpPhoto.setDataHandler(new DataHandler(fds));
                    mbpPhoto.setFileName(photo.getName());
                    mp.addBodyPart(mbpPhoto);
                }
            }
            msg.setContent(mp);
        } catch(MessagingException me) {
            log.log(Level.SEVERE, "Can not initialize message ATTACHED FILES part", me);
            throw new MailInternalErrorException(me);
        }
        
        // -------------------- Other mail attributes
        try {
            msg.setHeader("X-Mailer", request.getMailer());
        } catch(MessagingException me) {
            log.log(Level.SEVERE, "Can not initialize message X-MAILER part", me);
            throw new MailInternalErrorException(me);
        }
        try {
            msg.setSentDate(new Date());
        } catch(MessagingException me) {
            log.log(Level.SEVERE, "Can not initialize message SENTDATE part", me);
            throw new MailInternalErrorException(me);
        }
 
        // -------------------- SEND This Email
        try {
            SMTPTransport t = (SMTPTransport)mailSession.getTransport("smtp");
            t.connect();
            t.sendMessage(msg, msg.getAllRecipients());
        } catch(AuthenticationFailedException afe) {
            log.log(Level.WARNING, "Access refused (bad login or password)", afe);
            throw new MailException(String.format(Resources.getString("mail.msg.error.authfailed")));
        } catch(SendFailedException sfe) {
            processMessagingExceptions(sfe,p);
            log.log(Level.SEVERE, "Send Failed Exception", sfe);
            throw new MailInternalErrorException(sfe);
        } catch(MessagingException me) {
            processMessagingExceptions(me,p);
            log.log(Level.SEVERE, "Send Failed Exception", me);
            throw new MailInternalErrorException(me);
        }
    }

    private void processMessagingExceptions(MessagingException e, Properties p) throws MailException, InterruptedException {
        String host = p.getProperty("mail.host");
        int port;
        try {
            port = Integer.parseInt(p.getProperty("mail.smtp.port"));
        } catch(NumberFormatException e2) {
            port=-1;
            log.log(Level.WARNING, "MUST NEVER OCCURE : SMTP PORT NUMBER INT PARSING EXCEPTION", e2);
        }
        if (e instanceof SendFailedException) {
            MessagingException sfe = (MessagingException)e;
            if (sfe instanceof SMTPSendFailedException) {
                SMTPSendFailedException smtpe = (SMTPSendFailedException)sfe;
                switch(smtpe.getReturnCode()) {
                    case 550:  // ACCESS DENIED
                    case 554:  // RELAY ACCESS DENIED
                        throw new MailException(String.format(Resources.getString("mail.msg.error.authfailed")));
                    default:
                }                
            }
            Exception ne;
            while ((ne = sfe.getNextException()) != null && ne instanceof MessagingException) {
                List<InternetAddress> ok  = new ArrayList<InternetAddress>();
                List<InternetAddress> nok = new ArrayList<InternetAddress>();
                sfe = (MessagingException)ne;
                if (sfe instanceof SMTPAddressFailedException) {
                    SMTPAddressFailedException ssfe = (SMTPAddressFailedException)sfe;
                    nok.add(ssfe.getAddress());
                } else if (sfe instanceof SMTPAddressSucceededException) {
                    SMTPAddressSucceededException ssfe = (SMTPAddressSucceededException)sfe;
                    ok.add(ssfe.getAddress());
                }
                StringBuffer nokAddr = new StringBuffer();
                for(InternetAddress ia : nok) {
                    nokAddr.append(ia.getAddress());
                    nokAddr.append(" ");
                }
                throw new MailException(String.format(Resources.getString("mail.msg.error.partiallysent"), nokAddr.toString()));
                        
            }
        } else {
            Throwable next = e.getNextException();
            while(next!=null) {
                if (next instanceof UnknownHostException) {
                    throw new MailException(String.format(Resources.getString("mail.msg.error.unknownhost"), host));
                }
                if (next instanceof ConnectException) {
                    throw new MailException(String.format(Resources.getString("mail.msg.error.connect"), host, port));
                }
                if (next instanceof SocketTimeoutException) {
                    throw new MailException(String.format(Resources.getString("mail.msg.error.timeout"), host, port));
                }
                if (next instanceof NoRouteToHostException) {
                    throw new MailException(String.format(Resources.getString("mail.msg.error.noroute"), host, port));
                }
                if (next instanceof PortUnreachableException) {
                    throw new MailException(String.format(Resources.getString("mail.msg.error.portunreacheable"), host, port));
                }
                if (next instanceof SocketException) {
                    throw new MailException(String.format(Resources.getString("mail.msg.error.netunreachable")));
                }
                if (next instanceof InterruptedIOException || next instanceof InterruptedException) {
                    throw new InterruptedException();
                }
                log.log(Level.WARNING, "was unable to process the following exception "+next.getClass().getName()+"(continuing with the next one)", next);
                if (next instanceof MessagingException) next = ((MessagingException)next).getNextException();
            }
        }
    }
    
    public void doSend() throws MailException, InterruptedException {
        sendMessage(getRequest());
    }

    public MailRequest getRequest() {
        return request;
    }

    public void setRequest(MailRequest request) {
        this.request=request;
    }
}
