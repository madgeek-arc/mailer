package gr.athenarc.messaging.mailer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
@ConfigurationProperties(prefix = "mail")
public class MailerProperties {

    private final Map<String, Config> mailer = new LinkedHashMap<>();

    private final Redirect redirect = new Redirect();

    public Map<String, Config> getMailer() {
        return mailer;
    }

    public Redirect getRedirect() {
        return redirect;
    }

    public static class Redirect {
        boolean enabled;
        List<String> to = new ArrayList<>();
        boolean includeOriginalRecipientsMasked;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getTo() {
            return to;
        }

        public void setTo(List<String> to) {
            this.to = to;
        }

        public boolean isIncludeOriginalRecipientsMasked() {
            return includeOriginalRecipientsMasked;
        }

        public void setIncludeOriginalRecipientsMasked(boolean includeOriginalRecipientsMasked) {
            this.includeOriginalRecipientsMasked = includeOriginalRecipientsMasked;
        }
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
