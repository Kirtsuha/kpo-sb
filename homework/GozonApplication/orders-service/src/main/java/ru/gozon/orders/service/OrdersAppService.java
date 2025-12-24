package ru.gozon.orders.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import ru.gozon.common.api.NotFoundException;
import org.springframework.transaction.annotation.Transactional;
import ru.gozon.common.events.PaymentRequested;
import ru.gozon.orders.api.dto.CreateOrderRequest;
import ru.gozon.orders.api.dto.OrderResponse;
import ru.gozon.orders.domain.OrderEntity;
import ru.gozon.orders.domain.OrderStatus;
import ru.gozon.orders.outbox.OutboxMessage;
import ru.gozon.orders.repo.OrderRepository;
import ru.gozon.orders.repo.OutboxRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrdersAppService {

    private final OrderRepository orders;
    private final OutboxRepository outbox;
    private final ObjectMapper mapper;

    public OrdersAppService(OrderRepository orders, OutboxRepository outbox, ObjectMapper mapper) {
        this.orders = orders;
        this.outbox = outbox;
        this.mapper = mapper;
    }

    @Transactional
    public OrderResponse createOrder(String userId, CreateOrderRequest req) {
        var now = Instant.now();
        var orderId = UUID.randomUUID();

        var order = new OrderEntity(orderId, userId, req.amount(), req.description(), OrderStatus.NEW, now);
        orders.save(order);

        var evt = new PaymentRequested(UUID.randomUUID(), orderId, userId, req.amount(), now);
        try {
            var payload = mapper.writeValueAsString(evt);
            outbox.save(new OutboxMessage(UUID.randomUUID(), "Order", orderId, "PaymentRequested", payload, now));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot serialize event", e);
        }

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders(String userId) {
        return orders.findAllByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(String userId, UUID orderId) {
        var order = orders.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        return toResponse(order);
    }

    private OrderResponse toResponse(OrderEntity e) {
        return new OrderResponse(e.getId(), e.getAmount(), e.getDescription(), e.getStatus(), e.getCreatedAt());
    }
}
