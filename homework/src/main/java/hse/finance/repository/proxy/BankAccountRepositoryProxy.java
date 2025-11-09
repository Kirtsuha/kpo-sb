package hse.finance.repository.proxy;

import hse.finance.domain.BankAccount;
import hse.finance.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BankAccountRepositoryProxy {
    private final BankAccountRepository realRepository;
    private final List<BankAccount> cache = new ArrayList<>();
    private boolean cacheLoaded = false;

    @Autowired
    public BankAccountRepositoryProxy(BankAccountRepository realRepository) {
        this.realRepository = realRepository;
    }

    private void ensureCacheLoaded() {
        if (!cacheLoaded) {
            cache.clear();
            cache.addAll(realRepository.getAll());
            cacheLoaded = true;
            System.out.println("Кэш счетов загружен: " + cache.size() + " записей");
        }
    }

    public void add(BankAccount bankAccount) {
        realRepository.add(bankAccount);
        cache.removeIf(acc -> acc.getId().equals(bankAccount.getId()));
        cache.add(bankAccount);
    }

    public BankAccount get(String id) {
        ensureCacheLoaded();
        return cache.stream()
                .filter(acc -> acc.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void remove(String id) {
        realRepository.remove(id);
        cache.removeIf(acc -> acc.getId().equals(id));
    }

    public BankAccount update(BankAccount account) {
        realRepository.update(account.getId(), account);
        cache.removeIf(acc -> acc.getId().equals(account.getId()));
        cache.add(account);
        return account;
    }

    public ArrayList<BankAccount> getAll() {
        ensureCacheLoaded();
        return new ArrayList<>(cache);
    }

    public void refreshCache() {
        cacheLoaded = false;
        ensureCacheLoaded();
    }

    public void clearCache() {
        cache.clear();
        cacheLoaded = false;
    }
}