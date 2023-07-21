package gr.athenarc.messaging.mailer;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.athenarc.messaging.mailer.domain.EmailMessage;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class EmailMessageDeserializer implements Deserializer<EmailMessage> {

    private static final Logger logger = LoggerFactory.getLogger(EmailMessageDeserializer.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public EmailMessage deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                logger.error("Null message");
                return null;
            }
            return objectMapper.readValue(new String(data, StandardCharsets.UTF_8), EmailMessage.class);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing EmailMessage");
        }
    }

    @Override
    public void close() {
    }
}
