package gr.athenarc.messaging.mailer;

import gr.athenarc.messaging.mailer.domain.EmailMessage;
import gr.athenarc.messaging.mailer.service.MailerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RelativePaths.MAILS)
public class MailsController {

    private final MailerService mailerService;

    public MailsController(MailerService mailerService) {
        this.mailerService = mailerService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> send(@RequestBody EmailMessage message) {
        mailerService.sendMail(message);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
