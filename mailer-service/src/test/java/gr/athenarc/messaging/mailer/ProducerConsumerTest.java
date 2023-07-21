package gr.athenarc.messaging.mailer;

import gr.athenarc.messaging.mailer.service.MessageConsumer;
import jakarta.mail.Message;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ProducerConsumerTest {

    @Test
    void run() throws InterruptedException {
        BlockingQueue<Message> messageQueue = new LinkedBlockingDeque<>(100);


        MessageProducer producer = new MessageProducer(messageQueue);
        Thread producerThread = new Thread(producer);


        MessageConsumer consumer = new MessageConsumer(messageQueue);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();

        Thread.sleep(4000);
        producerThread.stop();

        Thread.sleep(2000);
        consumerThread.stop();

        assertEquals(0, messageQueue.size());
    }

    @Test
    void runConsumerTest() {
        // Consumer to multiply 2 to every integer of a list
        Consumer<List<Integer>> modify = list ->
        {
            for (int i = 0; i < list.size(); i++)
                list.set(i, 2 * list.get(i));
        };

        // Consumer to display a list of integers
        Consumer<List<Integer>> dispList = list -> list.stream().forEach(a -> System.out.print(a + " "));

        List<Integer> list = new ArrayList<Integer>();
        list.add(2);
        list.add(1);
        list.add(3);

        // using addThen()
        modify.andThen(dispList).accept(list);
        ;
    }
}
