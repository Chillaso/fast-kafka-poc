package org.epo.poc.kafkafastconsumer;

import lombok.extern.slf4j.Slf4j;
import org.epo.poc.kafkafastconsumer.model.Todo;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
    private Consumer<Message<List<Todo>>> process() {
        return message -> {
            final org.apache.kafka.clients.consumer.Consumer<?, ?> consumer = Optional.of(message)
                            .map(Message::getHeaders)
                            .map(it -> it.get(KafkaHeaders.CONSUMER, org.apache.kafka.clients.consumer.Consumer.class))
                            .orElseThrow(NullPointerException::new);
//            pauseConsumer(consumer);

            final List<Todo> todos = message.getPayload();
            Flux.fromIterable(todos)
                .parallel(100) //Number of cpus
                .runOn(Schedulers.boundedElastic()) // Get threads automatically
                .doOnNext(this::processTodo)
                .sequential()
                .then()
                .doOnSuccess(unused -> {
                    log.info("FINISHED");
                    commit(message);
//                    resumeConsumer(consumer);
                })
                .block();
            log.info("REAL FINISHED");
        };
    }

    private void processTodo(final Todo todo){
        todo.setTask(todo.getTask().toUpperCase());
        try{
            log.info("Doing interesting jobs for 15 seconds");
            Thread.sleep(15000);
        } catch(InterruptedException e){
            log.error(e.getMessage(), e);
        }
        log.info(todo.toString());
    };

    private void commit(Message<List<Todo>> message) {
        //Do manual commit. Property ackMode should be set in MANUAL
        Optional.of(message)
                .map(Message::getHeaders)
                .map(it -> it.get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class))
                .ifPresent(Acknowledgment::acknowledge);
    }

    private void pauseConsumer(org.apache.kafka.clients.consumer.Consumer<?,?> consumer){
        Optional.ofNullable(consumer)
                .map(org.apache.kafka.clients.consumer.Consumer::assignment)
                .ifPresent(consumer::pause);
        log.info("--> CONSUMER PAUSED");
    }

    private void resumeConsumer(org.apache.kafka.clients.consumer.Consumer<?,?> consumer){
        Optional.ofNullable(consumer)
                .map(org.apache.kafka.clients.consumer.Consumer::assignment)
                .ifPresent(consumer::resume);
        log.info("--> CONSUMER RESUMED");
    }
}
