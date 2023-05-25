package gr.athenarc.messaging.mailer.service;

import gr.athenarc.messaging.mailer.config.MailerProperties;
import jakarta.mail.Message;
import jakarta.mail.Session;
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
        for (Map.Entry<String, MailerProperties.Mail> mailEntry : mailerProperties.getMail().entrySet()) {
            sessionMap.put(mailEntry.getKey(), MailSessionUtils.createSession(mailEntry.getValue()));
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
    public void createMessage() {

    }

    @Override
    public void sendMail(Message message) {

    }
}
