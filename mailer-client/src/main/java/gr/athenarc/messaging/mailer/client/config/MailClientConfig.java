package gr.athenarc.messaging.mailer.client.config;

import gr.athenarc.messaging.mailer.client.service.MailClient;

import gr.athenarc.messaging.mailer.service.Mailer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@AutoConfiguration
@EnableConfigurationProperties(value = MailClientProperties.class)
public class MailClientConfig {

    @Bean
    @ConditionalOnClass(value = MailClient.class)
    Mailer mailerClient(MailClientProperties mailClientProperties, RestClient.Builder restClientBuilder) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(mailClientProperties.getClient().getConnectTimeout())
                .withReadTimeout(mailClientProperties.getClient().getReadTimeout());
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactoryBuilder.detect().build(settings);
        RestClient restClient = restClientBuilder.requestFactory(requestFactory).build();
        return new MailClient(mailClientProperties, restClient);
    }
}
