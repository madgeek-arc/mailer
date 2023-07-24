package gr.athenarc.messaging.mailer.client;

import gr.athenarc.messaging.mailer.RelativePaths;
import gr.athenarc.messaging.mailer.domain.EmailMessage;
import gr.athenarc.messaging.mailer.service.Mailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MailClient implements Mailer {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);
    private final String host;
    private final RestTemplate restTemplate = new RestTemplate();

    public MailClient(@Value("${mailer.host}") String host) {
        this.host = host;
    }

    @Override
    public void sendMail(EmailMessage emailMessage) {
        String path = UriComponentsBuilder.newInstance()
                .host(host)
                .path(RelativePaths.MAILS)
                .build()
                .toUri()
                .toString();
        logger.debug("Sending email to: {}\nMessage: {}", path, emailMessage);
        restTemplate.postForObject(path, emailMessage, Void.class);
    }
}
