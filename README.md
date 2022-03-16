# Kafka fast consumer

Kafka consumer implemented with Spring Cloud Streams, using Spring Functions and Spring Reactive.

Usually the best use case to implement this is if you need you have huge time between message processing, or a very variable time that change every message and you can't control with kafka configuration. 

## Relevant links

* [Spring cloud streams, Kafka binder documentation](https://docs.spring.io/spring-cloud-stream-binder-kafka/docs/3.2.1/reference/html/spring-cloud-stream-binder-kafka.html#_functional_style)
* [Spring cloud streams, functional and reactive](https://spring.io/blog/2019/10/17/spring-cloud-stream-functional-and-reactive)
* [Project reactor documentation](https://projectreactor.io/docs)

## Important configuration

To produce message to Kafka topic in batch mode it's important to use this configuration:

```yaml
spring:
  cloud:
    stream:
      poller:
        fixedDelay: 500 # Publish every 500ms
        maxMessagesPerPoll: 1000 # Produce 1000 messages before sending
```

In the consumer, some important configuration are:
```yaml
spring:
  cloud:
    stream:
      kafka:
        default:
          consumer:
            ackMode: MANUAL # This allow to do manual commits
    
    bindings:
    process-in-0: # Name 'process' is the name of my function annotated with @Bean
        destination: consumer-output
        group: consumer-output-group
        consumer:
        # Consumes messages in batches using max.poll.records as a number of messages in the batch
        batch-mode: true
```

Also, set max.poll.records property could be interesting if we want to process more than 500 messages, quantity of messages polled in a single poll by default.

## Kafka concepts

It's important to pause the consumer while we process messages, because if we're going to take a long time for some reason, Kafka will try to poll again but couldn't send the heartbeat with the up status, so Kafka will consider the consumer as dead.

We can do this with this code:

```java
private void pauseConsumer(org.apache.kafka.clients.consumer.Consumer<?,?> consumer){
    Optional.ofNullable(consumer)
            .map(org.apache.kafka.clients.consumer.Consumer::assignment)
            .ifPresent(consumer::pause);
    log.info("--> CONSUMER PAUSED");
    }
```

For sure, you'll need to resume the consumer in a very similar way, you can check the code [here](kafka-fast-consumer/src/main/java/org/cgg/poc/kafkafastconsumer/KafkaFastConsumer.java)