/*
 * Copyright 2023-2026 OpenAIRE AMKE & Athena Research and Innovation Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gr.athenarc.messaging.mailer.service;

import gr.athenarc.messaging.mailer.domain.EmailMessage;
import gr.athenarc.messaging.mailer.exception.InvalidEmailException;
import gr.athenarc.messaging.mailer.exception.MailDeliveryException;
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

    /**
     * Rejects messages without a sender or without at least one valid recipient.
     */
    default void validate(EmailMessage emailMessage) {
        if (emailMessage == null || emailMessage.getFrom() == null || emailMessage.getFrom().isBlank()) {
            throw new InvalidEmailException("'from' is required");
        }
        int recipients = toAddressArray(emailMessage.getTo()).length
                + toAddressArray(emailMessage.getCc()).length
                + toAddressArray(emailMessage.getBcc()).length;
        if (recipients == 0) {
            throw new InvalidEmailException("At least one valid recipient (to, cc or bcc) is required");
        }
    }

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
            throw new InvalidEmailException("Could not build the message: " + e.getMessage(), e);
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
            throw new MailDeliveryException("Mail delivery failed: " + e.getMessage(), e);
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
