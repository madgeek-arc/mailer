package gr.athenarc.messaging.mailer.service;

import gr.athenarc.messaging.mailer.EmailMessage;
import gr.athenarc.messaging.mailer.config.MailerProperties;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MailerService implements Mailer {

    private static final Logger logger = LoggerFactory.getLogger(MailerService.class);

    private final Map<String, Session> sessionMap = new LinkedHashMap<>();


    public MailerService(MailerProperties mailerProperties) {
        for (Map.Entry<String, MailerProperties.Config> mailEntry : mailerProperties.getMailer().entrySet()) {
            try {
                sessionMap.put(mailEntry.getKey(), MailSessionUtils.createSession(mailEntry.getValue()));
            } catch (RuntimeException e) {
                logger.error("Could not create session for provider: " + mailEntry.getKey(), e);
            }
        }
    }

    @Override
    public Session getSession() {
        Map.Entry<String, Session> sessionEntry = sessionMap.entrySet().iterator().next();
        logger.debug("Using session: {}", sessionEntry.getKey());
        return sessionEntry.getValue();
    }

    @Override
    public Session getSession(String mailer) {
        if (!sessionMap.containsKey(mailer)) {
            throw new RuntimeException(String.format("Requested mailer '%s' configuration does not exist.", mailer));
        }
        return sessionMap.get(mailer);
    }

    @Override
    public Session getSessionFromHost(EmailMessage emailMessage) {
        for (Map.Entry<String, Session> entry : sessionMap.entrySet()) {
            String from = entry.getValue().getProperties().get("mail.from").toString();
            if (from != null && from.equals(emailMessage.getFrom())) {
                return entry.getValue();
            } else if (emailMessage.getFrom().contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        logger.warn("No mailer session found for sender, attempting to use default");
        return getSession();
    }
}
