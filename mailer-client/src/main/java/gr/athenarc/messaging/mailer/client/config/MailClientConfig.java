package gr.athenarc.messaging.mailer.client.config;

import gr.athenarc.messaging.mailer.client.service.MailClient;

import gr.athenarc.messaging.mailer.service.Mailer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(value = MailClientProperties.class)
public class MailClientConfig {

    @Bean
    @ConditionalOnClass(value = MailClient.class)
    Mailer mailerClient(MailClientProperties mailClientProperties) {
        return new MailClient(mailClientProperties);
    }
}
