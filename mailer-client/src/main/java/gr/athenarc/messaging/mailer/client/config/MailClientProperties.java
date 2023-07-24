package gr.athenarc.messaging.mailer.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "mailer")
public class MailClientProperties {

    private Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public static class Client {
        private String host;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }
    }
}
