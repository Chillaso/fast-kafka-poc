package org.epo.poc.kafkafastproducer;

import org.epo.poc.kafkafastproducer.model.Todo;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.function.Supplier;

@Component
public class KafkaFastProducer {

    @Bean
    public Supplier<Todo> producer() {
        return () -> new Todo("Random text: " + generateRandomAlphabeticText());
    }

    private String generateRandomAlphabeticText() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
