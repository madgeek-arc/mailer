package gr.athenarc.messaging.mailer;

import jakarta.mail.Message;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

public class MessageProducer implements Runnable {
    private final BlockingQueue<Message> messageQueue;
    private static final Logger log = LoggerFactory.getLogger(MessageProducer.class);

    public MessageProducer(BlockingQueue<Message> messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        try {
            produce();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void produce() throws InterruptedException {
        while (true) {
            Thread.sleep(400);
            Message message = Mockito.mock(Message.class);
            log.info("Adding message to Queue");
            messageQueue.add(message);
        }
    }

}
