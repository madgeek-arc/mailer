package gr.athenarc.messaging.mailer.service;

import gr.athenarc.messaging.mailer.domain.EmailMessage;
import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface MailerService extends Mailer {

    Logger logger = LoggerFactory.getLogger(MailerService.class);

    Session getSession();

    Session getSession(String mailer);

    Session getSessionFromHost(EmailMessage emailMessage);

    default MimeMessage createMessage(EmailMessage emailMessage) {
        Session session = getSessionFromHost(emailMessage);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(emailMessage.getFrom(), (String) session.getProperties().get("mail.display-name")));
            message.setRecipients(Message.RecipientType.TO, toAddressArray(emailMessage.getTo()));
            message.setRecipients(Message.RecipientType.CC, toAddressArray(emailMessage.getCc()));
            message.setRecipients(Message.RecipientType.BCC, toAddressArray(emailMessage.getBcc()));

            message.setSubject(emailMessage.getSubject() != null ? emailMessage.getSubject() : "[No Subject]");
            message.setText(emailMessage.getText() != null ? emailMessage.getText() : "");
            if (emailMessage.isHtml()) {
                message.setText(emailMessage.getText() != null ? emailMessage.getText() : "", "utf-8", "html");
            }
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }

        return message;
    }

    default void sendMail(EmailMessage emailMessage) {
        Message message = createMessage(emailMessage);
        sendMail(message);
    }

    default void sendMail(Message message) {
        try {
            Transport.send(message);
        } catch (MessagingException e) {
            logger.error(e.getMessage(), e);
        }
    }

    default Address[] toAddressArray(List<String> emails) {
        if (emails == null) {
            emails = new ArrayList<>();
        }
        return emails
                .stream()
                .filter(Objects::nonNull)
                .map(this::toAddress)
                .filter(Objects::nonNull)
                .toArray(Address[]::new);
    }

    default Address toAddress(String email) {
        Address address = null;
        try {
            address = new InternetAddress(email);
        } catch (AddressException e) {
            logger.error(e.getMessage());
        }
        return address;
    }
}
