package gr.athenarc.messaging.mailer;

import gr.athenarc.messaging.mailer.service.MailerService;
import gr.athenarc.messaging.mailer.domain.EmailMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailsController {

    private final MailerService mailerService;

    public MailsController(MailerService mailerService) {
        this.mailerService = mailerService;
    }

    @PostMapping("mails")
    public ResponseEntity<Void> send(@RequestBody EmailMessage message) {
        mailerService.sendMail(message);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
