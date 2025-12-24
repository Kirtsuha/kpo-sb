package ru.gozon.common.events;

import java.time.Instant;
import java.util.UUID;

public record PaymentFailed(
        UUID eventId,
        UUID orderId,
        String userId,
        String reason,
        Instant createdAt
) implements PaymentResult {}
