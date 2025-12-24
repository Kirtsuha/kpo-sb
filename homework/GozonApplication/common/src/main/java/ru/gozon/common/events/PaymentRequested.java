package ru.gozon.common.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentRequested(
        UUID eventId,
        UUID orderId,
        String userId,
        BigDecimal amount,
        Instant createdAt
) {}
