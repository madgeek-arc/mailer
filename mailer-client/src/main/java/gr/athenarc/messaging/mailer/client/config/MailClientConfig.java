package gr.athenarc.messaging.mailer.client.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(value = "gr.athenarc.messaging.mailer.client")
@EnableConfigurationProperties(value = MailClientProperties.class)
public class MailClientConfig {
}
