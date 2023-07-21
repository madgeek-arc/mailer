package gr.athenarc.messaging.mailer;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.athenarc.messaging.mailer.domain.EmailMessage;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EmailMessageSerializer implements Serializer<EmailMessage> {

    private static final Logger logger = LoggerFactory.getLogger(EmailMessageDeserializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, EmailMessage data) {
        try {
            if (data == null){
                logger.error("Null message");
                return null;
            }
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing EmailMessage");
        }
    }

    @Override
    public void close() {
    }
}
