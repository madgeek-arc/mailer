package gr.athenarc.messaging.mailer;

import gr.athenarc.messaging.mailer.config.MailerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MailerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailerApplication.class, args);
	}

}
