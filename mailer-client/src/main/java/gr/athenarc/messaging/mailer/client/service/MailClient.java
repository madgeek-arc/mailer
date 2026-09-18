package gr.athenarc.messaging.mailer.client.service;

import gr.athenarc.messaging.mailer.RelativePaths;
import gr.athenarc.messaging.mailer.client.config.MailClientProperties;
import gr.athenarc.messaging.mailer.domain.EmailMessage;
import gr.athenarc.messaging.mailer.service.Mailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

public class MailClient implements Mailer {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);
    private final MailClientProperties mailClientProperties;
    private final RestClient restClient;

    public MailClient(MailClientProperties mailClientProperties, RestClient restClient) {
        this.mailClientProperties = mailClientProperties;
        this.restClient = restClient;
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
        logger.debug("Sending email to: {}\nMessage: {}", path, emailMessage);
        restClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .body(emailMessage)
                .retrieve()
                .toBodilessEntity();
    }
}
