package gr.athenarc.messaging.mailer.service;

import gr.athenarc.messaging.mailer.config.MailerProperties;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

import java.util.Properties;

public class MailSessionUtils {

    public static Session createSession(MailerProperties.Mail provider) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", provider.getSmtp().isAuth());
        props.put("mail.smtp.starttls.enable", provider.getSmtp().isStarttlsEnable());
        props.put("mail.smtp.host", provider.getSmtp().getHost());
        props.put("mail.smtp.port", provider.getSmtp().getPort());

        //create the Session object
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(provider.getUsername(), provider.getPassword());
            }
        };

        return Session.getInstance(props, authenticator);
    }

    private MailSessionUtils() {
    }
}
