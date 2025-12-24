package ru.gozon.common.events;

import java.time.Instant;
import java.util.UUID;

public record PaymentSucceeded(
        UUID eventId,
        UUID orderId,
        String userId,
        Instant createdAt
) implements PaymentResult {}
