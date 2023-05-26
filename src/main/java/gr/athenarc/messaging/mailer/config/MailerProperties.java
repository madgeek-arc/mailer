package gr.athenarc.messaging.mailer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Component
@ConfigurationProperties(prefix = "mail")
public class MailerProperties {

    private final Map<String, Config> mailer = new LinkedHashMap<>();

    public Map<String, Config> getMailer() {
        return mailer;
    }

    public static class Config {
        String username;
        String password;
        Properties props;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Properties getProps() {
            return props;
        }

        public void setProps(Properties props) {
            this.props = props;
        }
    }
}
