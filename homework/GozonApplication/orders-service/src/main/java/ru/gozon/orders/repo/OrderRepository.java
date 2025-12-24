package ru.gozon.orders.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gozon.orders.domain.OrderEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findAllByUserIdOrderByCreatedAtDesc(String userId);
    Optional<OrderEntity> findByIdAndUserId(UUID id, String userId);
}
