package ru.gozon.payments.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.gozon.common.events.PaymentFailed;
import ru.gozon.common.events.PaymentRequested;
import ru.gozon.common.events.PaymentSucceeded;
import ru.gozon.common.kafka.Topics;
import ru.gozon.payments.domain.InboxMessage;
import ru.gozon.payments.domain.PaymentTransaction;
import ru.gozon.payments.domain.PaymentTxStatus;
import ru.gozon.payments.outbox.OutboxMessage;
import ru.gozon.payments.repo.AccountRepository;
import ru.gozon.payments.repo.InboxRepository;
import ru.gozon.payments.repo.OutboxRepository;
import ru.gozon.payments.repo.PaymentTxRepository;

import java.time.Instant;
import java.util.UUID;

@Component
public class PaymentRequestConsumer {

    private final ObjectMapper mapper;
    private final InboxRepository inbox;
    private final AccountRepository accounts;
    private final PaymentTxRepository txs;
    private final OutboxRepository outbox;

    public PaymentRequestConsumer(ObjectMapper mapper,
                                  InboxRepository inbox,
                                  AccountRepository accounts,
                                  PaymentTxRepository txs,
                                  OutboxRepository outbox) {
        this.mapper = mapper;
        this.inbox = inbox;
        this.accounts = accounts;
        this.txs = txs;
        this.outbox = outbox;
    }

    @KafkaListener(topics = Topics.PAYMENT_REQUESTS)
    @Transactional
    public void onMessage(ConsumerRecord<String, String> rec, Acknowledgment ack) throws Exception {
        var now = Instant.now();
        var req = mapper.readValue(rec.value(), PaymentRequested.class);

        var existing = inbox.findById(req.eventId()).orElse(null);
        if (existing != null && existing.isProcessed()) {
            ack.acknowledge();
            return;
        }
        if (existing == null) {
            inbox.save(new InboxMessage(req.eventId(), "PaymentRequested", rec.value(), now));
        }

        // effectively exactly once за счёт UNIQUE(order_id)
        var already = txs.findByOrderId(req.orderId()).orElse(null);
        if (already != null) {
            enqueueResultForDuplicate(req, already.getStatus());
            markInboxProcessed(req.eventId(), now);
            ack.acknowledge();
            return;
        }

        var acc = accounts.lockByUserId(req.userId()).orElse(null);
        if (acc == null) {
            fail(req, "ACCOUNT_NOT_FOUND", now);
            markInboxProcessed(req.eventId(), now);
            ack.acknowledge();
            return;
        }

        if (acc.getBalance().compareTo(req.amount()) < 0) {
            txs.save(new PaymentTransaction(UUID.randomUUID(), req.orderId(), req.userId(), req.amount(), PaymentTxStatus.FAILED, now));
            fail(req, "INSUFFICIENT_FUNDS", now);
            markInboxProcessed(req.eventId(), now);
            ack.acknowledge();
            return;
        }

        acc.setBalance(acc.getBalance().subtract(req.amount()));
        txs.save(new PaymentTransaction(UUID.randomUUID(), req.orderId(), req.userId(), req.amount(), PaymentTxStatus.SUCCESS, now));
        succeed(req, now);

        markInboxProcessed(req.eventId(), now);
        ack.acknowledge();
    }

    private void markInboxProcessed(UUID eventId, Instant at) {
        inbox.findById(eventId).ifPresent(m -> m.markProcessed(at));
    }

    private void enqueueResultForDuplicate(PaymentRequested req, PaymentTxStatus st) {
        if (st == PaymentTxStatus.SUCCESS) succeed(req, Instant.now());
        else fail(req, "DUPLICATE", Instant.now());
    }

    private void succeed(PaymentRequested req, Instant now) {
        var evt = new PaymentSucceeded(UUID.randomUUID(), req.orderId(), req.userId(), now);
        writeOutbox(req.orderId(), "PaymentSucceeded", evt, now);
    }

    private void fail(PaymentRequested req, String reason, Instant now) {
        var evt = new PaymentFailed(UUID.randomUUID(), req.orderId(), req.userId(), reason, now);
        writeOutbox(req.orderId(), "PaymentFailed", evt, now);
    }

    private void writeOutbox(UUID orderId, String type, Object evt, Instant now) {
        try {
            var payload = mapper.writeValueAsString(evt);
            outbox.save(new OutboxMessage(UUID.randomUUID(), "Payment", orderId, type, payload, now));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot serialize event", e);
        }
    }
}
