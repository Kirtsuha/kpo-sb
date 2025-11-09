package hse.finance.service;

import hse.finance.domain.BankAccount;
import hse.finance.id_generation.IdGenerationContext;
import hse.finance.repository.proxy.BankAccountRepositoryProxy;
import hse.finance.repository.proxy.OperationRepositoryProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class BankAccountFacade {
    BankAccountRepositoryProxy accountRepository;
    OperationRepositoryProxy operationRepository;
    IdGenerationContext context;

    @Autowired
    public BankAccountFacade(BankAccountRepositoryProxy accountRepository,
                             OperationRepositoryProxy operationRepository,
                             IdGenerationContext context) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
        this.context = context;
    }

    public BankAccount createAccount(String name, BigDecimal balance) {
        BankAccount bankAccount = BankAccount.create(name, balance, context);
        accountRepository.add(bankAccount);
        return bankAccount;
    }

    public BankAccount get(String id) {
        return accountRepository.get(id);
    }

    public void remove(String id) {
        accountRepository.remove(id);
    }

    public ArrayList<BankAccount> getAll() {
        return accountRepository.getAll();
    }

    public void updateAccount(BankAccount account) {
        accountRepository.update(account);
    }
}
