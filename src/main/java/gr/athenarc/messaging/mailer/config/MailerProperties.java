package gr.athenarc.messaging.mailer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "mailer")
public class MailerProperties {

    private final Map<String, Mail> mail = new HashMap<>();

    public Map<String, Mail> getMail() {
        return this.mail;
    }

    public static class Mail {
        String username;
        String password;
        Smtp smtp;

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

        public Smtp getSmtp() {
            return smtp;
        }

        public void setSmtp(Smtp smtp) {
            this.smtp = smtp;
        }
    }

    public static class Smtp {
        String host;
        String port;
        boolean auth = true;
        boolean starttlsEnable = true;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public boolean isAuth() {
            return auth;
        }

        public void setAuth(boolean auth) {
            this.auth = auth;
        }

        public boolean isStarttlsEnable() {
            return starttlsEnable;
        }

        public void setStarttlsEnable(boolean starttlsEnable) {
            this.starttlsEnable = starttlsEnable;
        }
    }
}
