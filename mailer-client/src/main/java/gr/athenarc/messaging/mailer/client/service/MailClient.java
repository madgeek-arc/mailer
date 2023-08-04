package gr.athenarc.messaging.mailer.client.service;

import gr.athenarc.messaging.mailer.RelativePaths;
import gr.athenarc.messaging.mailer.client.config.MailClientProperties;
import gr.athenarc.messaging.mailer.domain.EmailMessage;
import gr.athenarc.messaging.mailer.service.Mailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MailClient implements Mailer {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);
    private final MailClientProperties mailClientProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public MailClient(MailClientProperties mailClientProperties) {
        this.mailClientProperties = mailClientProperties;
    }

    @Override
    public void sendMail(EmailMessage emailMessage) {
        String path = UriComponentsBuilder
                .fromHttpUrl(mailClientProperties.getClient().getHost())
                .path(RelativePaths.MAILS)
                .build()
                .encode()
                .toUri()
                .toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmailMessage> emailEntity = new HttpEntity<>(emailMessage, headers);
        logger.debug("Sending email to: {}\nMessage: {}", path, emailMessage);
        restTemplate.postForObject(path, emailEntity, Void.class);
    }
}
