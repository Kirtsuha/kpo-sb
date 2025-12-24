package ru.gozon.orders.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.gozon.common.events.PaymentFailed;
import ru.gozon.common.events.PaymentSucceeded;
import ru.gozon.common.kafka.Topics;
import ru.gozon.orders.domain.OrderStatus;
import ru.gozon.orders.repo.OrderRepository;

import java.util.UUID;

@Component
public class PaymentResultConsumer {

    private record Parsed(UUID orderId, boolean success) {}

    private final ObjectMapper mapper;
    private final OrderRepository orders;

    public PaymentResultConsumer(ObjectMapper mapper, OrderRepository orders) {
        this.mapper = mapper;
        this.orders = orders;
    }

    @KafkaListener(topics = Topics.PAYMENT_RESULTS)
    @Transactional
    public void onMessage(ConsumerRecord<String, String> rec, Acknowledgment ack) throws Exception {
        Parsed parsed = parse(rec.value());

        orders.findById(parsed.orderId()).ifPresent(order -> {
            if (order.getStatus() != OrderStatus.NEW) return; // idempotent
            order.setStatus(parsed.success() ? OrderStatus.FINISHED : OrderStatus.CANCELLED);
        });

        ack.acknowledge();
    }

    private Parsed parse(String json) throws Exception {
        try {
            var s = mapper.readValue(json, PaymentSucceeded.class);
            return new Parsed(s.orderId(), true);
        } catch (Exception ignore) {
            var f = mapper.readValue(json, PaymentFailed.class);
            return new Parsed(f.orderId(), false);
        }
    }
}
