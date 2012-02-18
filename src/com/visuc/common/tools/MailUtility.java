package com.visuc.common.tools;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.visuc.common.tools.SMTPAuthenticator;
import test.TestMail;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;

public class MailUtility {

	public static void postMail( String recipients[ ], String subject, String message , String from) throws MessagingException, javax.mail.MessagingException
	{
	    boolean debug = false;

	    //Set the host smtp address
	    Properties props = new Properties();
	    props.put("mail.smtp.host", "smtp.gmail.com");
	    props.put("mail.smtp.auth", "true");

	    Authenticator auth = new SMTPAuthenticator();
	    Session session = Session.getDefaultInstance(props, auth);

	    // create some properties and get the default Session
	    session.setDebug(debug);

	    // create a message
	    Message msg = new MimeMessage(session);

	    // set the from and to address
	    InternetAddress addressFrom = new InternetAddress(from);
	    msg.setFrom(addressFrom);

	    InternetAddress[] addressTo = new InternetAddress[recipients.length]; 
	    for (int i = 0; i < recipients.length; i++)
	    {
	        addressTo[i] = new InternetAddress(recipients[i]);
	    }
	    msg.setRecipients(Message.RecipientType.TO, addressTo);
	   

	    // Optional : You can also set your custom headers in the Email if you Want
	    msg.addHeader("testHeader", "test header Value");

	    // Setting the Subject and Content Type
	    msg.setSubject(subject);
	    msg.setContent(message, "text/html; charset=ISO-8859-1");
	    Transport.send(msg);
	}

}

/**
* SimpleAuthenticator is used to do simple authentication
* when the SMTP server requires it.
*/
class SMTPAuthenticator extends javax.mail.Authenticator
{

    public PasswordAuthentication getPasswordAuthentication()
    {
        String username = TestMail.SMTP_AUTH_USER;
        String password = TestMail.SMTP_AUTH_PWD;
        return new PasswordAuthentication(username, password);
    }
}
