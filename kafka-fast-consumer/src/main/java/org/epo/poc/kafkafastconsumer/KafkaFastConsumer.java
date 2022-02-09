package org.epo.poc.kafkafastconsumer;

import lombok.extern.slf4j.Slf4j;
import org.epo.poc.kafkafastconsumer.model.Todo;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
@Slf4j
public class KafkaFastConsumer {

    /**
     * Consumer function will be exposed to Spring context as a message stream listener
     * in this case, for kafka due we have kafka binder in the configuration.
     *
     * We accept a list of {@link org.epo.poc.kafkafastconsumer.model.Todo} objects cause we want to process in batch mode. See configuration.
     */
    @Bean
    private Consumer<List<Todo>> process() {
        return todoList -> {
            todoList.forEach(todo -> {
                todo.setTask(todo.getTask().toUpperCase());
                log.info(todo.toString());
            });
        };
    }
}
