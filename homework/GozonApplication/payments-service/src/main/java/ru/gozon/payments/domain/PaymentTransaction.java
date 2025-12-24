package ru.gozon.payments.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {

    @Id
    private UUID id;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentTxStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected PaymentTransaction() {}

    public PaymentTransaction(UUID id, UUID orderId, String userId, BigDecimal amount, PaymentTxStatus status, Instant createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getOrderId() { return orderId; }
    public PaymentTxStatus getStatus() { return status; }
}
