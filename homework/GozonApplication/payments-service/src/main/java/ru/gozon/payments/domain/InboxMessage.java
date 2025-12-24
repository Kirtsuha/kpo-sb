package ru.gozon.payments.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inbox")
public class InboxMessage {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    protected InboxMessage() {}

    public InboxMessage(UUID eventId, String type, String payload, Instant receivedAt) {
        this.eventId = eventId;
        this.type = type;
        this.payload = payload;
        this.receivedAt = receivedAt;
    }

    public UUID getEventId() { return eventId; }
    public boolean isProcessed() { return processedAt != null; }
    public void markProcessed(Instant at) { this.processedAt = at; }
}
