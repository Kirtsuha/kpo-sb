package ru.gozon.common.events;

import java.time.Instant;
import java.util.UUID;

public sealed interface PaymentResult permits PaymentSucceeded, PaymentFailed {
    UUID eventId();
    UUID orderId();
    String userId();
    Instant createdAt();
}
