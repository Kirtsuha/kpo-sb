package ru.gozon.orders.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.gozon.common.Headers;
import ru.gozon.orders.api.dto.CreateOrderRequest;
import ru.gozon.orders.api.dto.OrderResponse;
import ru.gozon.orders.service.OrdersAppService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    private final OrdersAppService service;

    public OrdersController(OrdersAppService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@RequestHeader(Headers.USER_ID) String userId,
                                @Valid @RequestBody CreateOrderRequest req) {
        return service.createOrder(userId, req);
    }

    @GetMapping
    public List<OrderResponse> list(@RequestHeader(Headers.USER_ID) String userId) {
        return service.listOrders(userId);
    }

    @GetMapping("/{orderId}")
    public OrderResponse get(@RequestHeader(Headers.USER_ID) String userId,
                             @PathVariable UUID orderId) {
        return service.getOrder(userId, orderId);
    }
}
