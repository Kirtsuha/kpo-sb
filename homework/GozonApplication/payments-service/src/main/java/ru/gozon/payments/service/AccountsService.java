package ru.gozon.payments.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gozon.common.api.NotFoundException;
import ru.gozon.payments.api.dto.BalanceResponse;
import ru.gozon.payments.domain.AccountEntity;
import ru.gozon.payments.repo.AccountRepository;

import java.math.BigDecimal;

@Service
public class AccountsService {

    private final AccountRepository accounts;

    public AccountsService(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Transactional
    public BalanceResponse createAccount(String userId) {
        if (accounts.existsById(userId)) {
            throw new IllegalStateException("Account already exists");
        }
        var a = new AccountEntity(userId, BigDecimal.ZERO);
        accounts.save(a);
        return new BalanceResponse(userId, a.getBalance());
    }

    @Transactional
    public BalanceResponse topUp(String userId, BigDecimal amount) {
        var a = accounts.lockByUserId(userId).orElseThrow(() -> new NotFoundException("Account not found"));
        a.setBalance(a.getBalance().add(amount));
        return new BalanceResponse(userId, a.getBalance());
    }

    @Transactional(readOnly = true)
    public BalanceResponse getBalance(String userId) {
        var a = accounts.findById(userId).orElseThrow(() -> new NotFoundException("Account not found"));
        return new BalanceResponse(userId, a.getBalance());
    }
}
