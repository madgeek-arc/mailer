package gr.athenarc.messaging.mailer.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;

public class MessageConsumer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
    private final BlockingQueue<Message> messageQueue;

    public MessageConsumer(BlockingQueue<Message> messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        try {
            consume();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void consume() throws InterruptedException {
        while (true) {
            try {
                Message message = messageQueue.remove();
                useMessage(message);
            } catch (NoSuchElementException e) {
                Thread.sleep(500);
            }
        }
    }

    private void useMessage(Message message) {
        try {
            Transport.send(message);
        } catch (MessagingException e) {
            logger.error("Could not send message");
        }
    }
}
