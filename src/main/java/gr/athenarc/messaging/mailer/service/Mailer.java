package gr.athenarc.messaging.mailer.service;

import jakarta.mail.Message;
import jakarta.mail.Session;

public interface Mailer {

    // TODO: add / remove methods
    Session getSession();
    Session getSession(String mailer);
    void createMessage();

    void sendMail(Message message);
}
