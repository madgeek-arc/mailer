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
package gr.athenarc.messaging.mailer.client.config;

import com.sun.net.httpserver.HttpServer;
import gr.athenarc.messaging.mailer.MailerHeaders;
import gr.athenarc.messaging.mailer.RelativePaths;
import gr.athenarc.messaging.mailer.domain.EmailMessage;
import gr.athenarc.messaging.mailer.service.Mailer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class MailClientConfigTests {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class, MailClientConfig.class));

    private HttpServer server;
    private final AtomicReference<String> receivedApiKey = new AtomicReference<>();

    @BeforeEach
    void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(RelativePaths.MAILS, exchange -> {
            receivedApiKey.set(exchange.getRequestHeaders().getFirst(MailerHeaders.API_KEY));
            exchange.sendResponseHeaders(201, -1);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    private String host() {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    private static EmailMessage message() {
        return new EmailMessage.EmailBuilder("sender@example.org", List.of("alice@example.org")).build();
    }

    @Test
    void failsWithoutHost() {
        runner.run(context -> assertThat(context).hasFailed()
                .getFailure().rootCause().hasMessageContaining("mailer.client.host must be set"));
    }

    @Test
    void backsOffWhenApplicationDefinesMailer() {
        runner.withBean(Mailer.class, () -> email -> { })
                .run(context -> assertThat(context).hasNotFailed().hasSingleBean(Mailer.class));
    }

    @Test
    void sendsApiKeyWhenConfigured() {
        runner.withPropertyValues("mailer.client.host=" + host(), "mailer.client.api-key=test-key")
                .run(context -> {
                    context.getBean(Mailer.class).sendMail(message());
                    assertThat(receivedApiKey.get()).isEqualTo("test-key");
                });
    }

    @Test
    void omitsApiKeyWhenNotConfigured() {
        runner.withPropertyValues("mailer.client.host=" + host())
                .run(context -> {
                    context.getBean(Mailer.class).sendMail(message());
                    assertThat(receivedApiKey.get()).isNull();
                });
    }
}
