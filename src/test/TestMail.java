package test;
import javax.mail.*;
import javax.mail.internet.*;

import java.util.*;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;


public class TestMail {

	  public static final String SMTP_AUTH_USER = "ndvasava";
	  public static final String SMTP_AUTH_PWD  = "google_392012";

	public static void main(String[] args) throws MessagingException, javax.mail.MessagingException {
		String[] recipients = new String[] {"ndvasava@gmail.com"};
		String subject = "testing of java-mail.";
		String message = "Your visucommerce account is created successfully.* <br>"
			+ "account id : <br>"
			+ "user id : <br>"
			+ "Company : <br>"
			+ "Commerce type : <br>"
			+ "Owner : <br><br>"
			+ "<b>* The account will be validated within 48 hours. Meanwhile, no action is possible on the account."
			+ "<br><b>Please Fill and sign the contract and send back to visucommerce on the following adress.</b> ";
		String from ="test@test.com";
		
		postMail(recipients, subject, message, from);
	}
	
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