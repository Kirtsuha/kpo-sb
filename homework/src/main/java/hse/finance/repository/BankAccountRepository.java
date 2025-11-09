package hse.finance.repository;

import hse.finance.domain.BankAccount;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class BankAccountRepository {
    private final Map<String, BankAccount> bankAccountMap = new HashMap<>();

    public void add(BankAccount bankAccount) {
        bankAccountMap.put(bankAccount.getId(), bankAccount);
    }

    public BankAccount get(String id) {
        return bankAccountMap.get(id);
    }

    public void remove(String id) {
        bankAccountMap.remove(id);
    }

    public void update(String id, BankAccount newBankAccount) {
        bankAccountMap.put(id, newBankAccount);
    }

    public ArrayList<BankAccount> getAll() {
        return new ArrayList<>(bankAccountMap.values());
    }
}
