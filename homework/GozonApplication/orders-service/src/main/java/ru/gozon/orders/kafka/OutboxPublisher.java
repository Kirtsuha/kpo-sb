package ru.gozon.orders.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.gozon.common.kafka.Topics;
import ru.gozon.orders.repo.OutboxRepository;

import java.time.Instant;

@Component
public class OutboxPublisher {

    private final OutboxRepository outbox;
    private final KafkaTemplate<String, String> kafka;
    private final int batchSize;

    public OutboxPublisher(OutboxRepository outbox,
                           KafkaTemplate<String, String> kafka,
                           @Value("${app.outbox.batch-size:50}") int batchSize) {
        this.outbox = outbox;
        this.kafka = kafka;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval:1000}")
    @Transactional
    public void publish() {
        var msgs = outbox.lockNextUnsent(batchSize);
        for (var m : msgs) {
            kafka.send(Topics.PAYMENT_REQUESTS, m.getAggregateId().toString(), m.getPayload());
            m.markSent(Instant.now());
        }
    }
}
