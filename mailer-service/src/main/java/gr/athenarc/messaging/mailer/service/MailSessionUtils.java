package gr.athenarc.messaging.mailer.service;

import gr.athenarc.messaging.mailer.config.MailerProperties;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

public class MailSessionUtils {

    public static Session createSession(MailerProperties.Config config) {

        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getUsername(), config.getPassword());
            }
        };

        //create the Session object
        return Session.getInstance(config.getProps(), authenticator);
    }

    private MailSessionUtils() {
    }
}
