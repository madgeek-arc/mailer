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
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

import java.util.Map;
import java.util.Properties;

public class MailSessionUtils {

    /**
     * Timeouts (ms) applied to every account unless its {@code props} set them explicitly.
     */
    static final Map<String, String> DEFAULT_TIMEOUTS = Map.of(
            "connectiontimeout", "10000",
            "timeout", "30000",
            "writetimeout", "30000"
    );

    public static Session createSession(MailerProperties.Config config) {

        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getUsername(), config.getPassword());
            }
        };

        return Session.getInstance(withDefaultTimeouts(config.getProps()), authenticator);
    }

    static Properties withDefaultTimeouts(Properties props) {
        Properties result = new Properties();
        if (props != null) {
            result.putAll(props);
        }
        for (String protocol : new String[]{"smtp", "smtps"}) {
            DEFAULT_TIMEOUTS.forEach((key, value) -> result.putIfAbsent("mail." + protocol + "." + key, value));
        }
        return result;
    }

    private MailSessionUtils() {
    }
}
