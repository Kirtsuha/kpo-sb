package ru.gozon.payments.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.gozon.common.Headers;
import ru.gozon.payments.api.dto.BalanceResponse;
import ru.gozon.payments.api.dto.TopUpRequest;
import ru.gozon.payments.service.AccountsService;

@RestController
@RequestMapping("/payments")
public class AccountsController {

    private final AccountsService service;

    public AccountsController(AccountsService service) {
        this.service = service;
    }

    @PostMapping("/account")
    @ResponseStatus(HttpStatus.CREATED)
    public BalanceResponse createAccount(@RequestHeader(Headers.USER_ID) String userId) {
        return service.createAccount(userId);
    }

    @PostMapping("/topup")
    public BalanceResponse topUp(@RequestHeader(Headers.USER_ID) String userId,
                                 @Valid @RequestBody TopUpRequest req) {
        return service.topUp(userId, req.amount());
    }

    @GetMapping("/balance")
    public BalanceResponse balance(@RequestHeader(Headers.USER_ID) String userId) {
        return service.getBalance(userId);
    }
}
