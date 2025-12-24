package ru.gozon.payments.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gozon.payments.domain.PaymentTransaction;

import java.util.Optional;
import java.util.UUID;

public interface PaymentTxRepository extends JpaRepository<PaymentTransaction, UUID> {
    Optional<PaymentTransaction> findByOrderId(UUID orderId);
}
