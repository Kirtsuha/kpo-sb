package hse.finance.service;

import hse.finance.repository.proxy.BankAccountRepositoryProxy;
import hse.finance.repository.proxy.CategoryRepositoryProxy;
import hse.finance.repository.proxy.OperationRepositoryProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CacheManagementFacade {
    private final BankAccountRepositoryProxy accountRepositoryProxy;
    private final CategoryRepositoryProxy categoryRepositoryProxy;
    private final OperationRepositoryProxy operationRepositoryProxy;

    @Autowired
    public CacheManagementFacade(BankAccountRepositoryProxy accountRepositoryProxy,
                                 CategoryRepositoryProxy categoryRepositoryProxy,
                                 OperationRepositoryProxy operationRepositoryProxy) {
        this.accountRepositoryProxy = accountRepositoryProxy;
        this.categoryRepositoryProxy = categoryRepositoryProxy;
        this.operationRepositoryProxy = operationRepositoryProxy;
    }


    public void refreshAllCaches() {
        accountRepositoryProxy.refreshCache();
        categoryRepositoryProxy.refreshCache();
        operationRepositoryProxy.refreshCache();
        System.out.println("Все кэши обновлены");
    }

    public void clearAllCaches() {
        accountRepositoryProxy.clearCache();
        categoryRepositoryProxy.clearCache();
        operationRepositoryProxy.clearCache();
        System.out.println("Все кэши очищены");
    }


    public CacheStats getCacheStats() {
        return new CacheStats(
                accountRepositoryProxy.getAll().size(),
                categoryRepositoryProxy.getAll().size(),
                operationRepositoryProxy.getAll().size()
        );
    }

    public record CacheStats(int accountsInCache, int categoriesInCache, int operationsInCache) {

        @Override
            public String toString() {
                return String.format(
                        "Кэш: счетов=%d, категорий=%d, операций=%d",
                        accountsInCache, categoriesInCache, operationsInCache
                );
            }
        }
}