
package utilities;

import java.util.Properties;
 
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * This show a second way to send email using a GMail account.
 * Note that:
 *  
 * mail.smtp.starttls.enable -->
 * If true, enables the use of the STARTTLS command (if supported by the server) 
 * to switch the connection to a TLS-protected connection before 
 * issuing any login commands. 
 * Note that an appropriate trust store must configured so that the client will
 * trust the server's certificate. Defaults to false. 
 * 
 * @author Curt Jones
 */
public class EmailTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       final String username = "****@gmail.com";
	final String password = "*****";
 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");// important see note above 
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
        //The following line does not appear to be needed -- some references reccomend it
                props.put("mail.smtps.quitwait","false");//prevents ssl execption if GMail is used
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("BUWeatherProject@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("cjones@bloomu.edu"));
			message.setSubject("Testing Subject");
			message.setText("Dear Mail Crawler,"
				+ "\n\n No spam to my email, please!");
                        
                        message.saveChanges(); //important according to documentation
			Transport.send(message);
 
			System.out.println("Done");
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
    }
}
