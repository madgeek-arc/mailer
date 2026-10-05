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
