package gr.athenarc.messaging.mailer.service;

import gr.athenarc.messaging.mailer.domain.EmailMessage;

public interface Mailer {

    void sendMail(EmailMessage emailMessage);

}
