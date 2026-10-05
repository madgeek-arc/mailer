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

import gr.athenarc.messaging.mailer.config.MailerProperties;
import gr.athenarc.messaging.mailer.domain.EmailMessage;
import jakarta.mail.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MultiMailerServiceService implements MailerService {

    private static final Logger logger = LoggerFactory.getLogger(MultiMailerServiceService.class);

    private final Map<String, Session> sessionMap = new LinkedHashMap<>();


    private final MailRedirector redirector;

    public MultiMailerServiceService(MailerProperties mailerProperties) {
        if (mailerProperties.getRedirect().isEnabled()) {
            redirector = new MailRedirector(mailerProperties.getRedirect());
            logger.warn("Mail redirect is ACTIVE: all messages are sent to {} configured address(es) instead of their recipients", redirector.targetCount());
        } else {
            redirector = null;
        }
        for (Map.Entry<String, MailerProperties.Config> mailEntry : mailerProperties.getMailer().entrySet()) {
            try {
                sessionMap.put(mailEntry.getKey(), MailSessionUtils.createSession(mailEntry.getValue()));
            } catch (RuntimeException e) {
                logger.error("Could not create session for provider: " + mailEntry.getKey(), e);
            }
        }
    }

    @Override
    public void sendMail(EmailMessage emailMessage) {
        MailerService.super.sendMail(redirector != null ? redirector.redirect(emailMessage) : emailMessage);
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
            String from = (String) entry.getValue().getProperties().get("mail.from");
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
