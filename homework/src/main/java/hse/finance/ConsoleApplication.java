package hse.finance;

import hse.finance.command.CategorySummaryCommand;
import hse.finance.command.FinancialSummaryCommand;
import hse.finance.command.TimingCommandDecorator;
import hse.finance.domain.BankAccount;
import hse.finance.domain.Category;
import hse.finance.domain.Operation;
import hse.finance.domain.TransactionType;
import hse.finance.dto.CategorySummaryDTO;
import hse.finance.dto.FinancialSummaryDTO;
import hse.finance.dto.ImportStatistic;
import hse.finance.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Scanner;

@SpringBootApplication
public class ConsoleApplication implements CommandLineRunner {

    @Autowired
    private BankAccountFacade accountFacade;

    @Autowired
    private CategoryFacade categoryFacade;

    @Autowired
    private OperationFacade operationFacade;

    @Autowired
    private AnalyticsFacade analyticsFacade;

    @Autowired
    private ImportExportFacade importExportFacade;

    @Autowired
    private BalanceRecalculationFacade balanceFacade;

    @Autowired
    private CacheManagementFacade cacheFacade;

    public static void main(String[] args) {
        SpringApplication.run(ConsoleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        System.out.println("=== БАНКОВСКОЕ ПРИЛОЖЕНИЕ ===");
        System.out.println("Демонстрация паттернов проектирования");

        initializeTestData();

        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    demonstrateDomainModel();
                    break;
                case "2":
                    demonstrateFacades();
                    break;
                case "3":
                    demonstrateCommandsAndDecorators();
                    break;
                case "4":
                    demonstrateImportExport();
                    break;
                case "5":
                    demonstrateAnalytics();
                    break;
                case "6":
                    demonstrateBalanceRecalculation();
                    break;
                case "7":
                    demonstrateCache();
                    break;
                case "8":
                    manageBankAccounts(scanner);
                    break;
                case "9":
                    manageCategories(scanner);
                    break;
                case "10":
                    manageOperations(scanner);
                    break;
                case "0":
                    System.out.println("Выход...");
                    return;
                default:
                    System.out.println("Неверный выбор");
            }

            System.out.println("\nНажмите Enter для продолжения...");
            scanner.nextLine();
        }
    }

    private void printMenu() {
        System.out.println("\n=== ГЛАВНОЕ МЕНЮ ===");
        System.out.println("1. Доменная модель (Счета, Категории, Операции)");
        System.out.println("2. Фасады");
        System.out.println("3. Команды + Декораторы");
        System.out.println("4. Импорт/Экспорт (Шаблонный метод + Посетитель)");
        System.out.println("5. Аналитика");
        System.out.println("6. Пересчет баланса");
        System.out.println("7. Прокси-кэш");
        System.out.println("8. Управление счетами");
        System.out.println("9. Управление категориями");
        System.out.println("10. Управление операциями");
        System.out.println("0. Выход");
        System.out.print("Выберите пункт: ");
    }

    private void initializeTestData() {
        System.out.println("\n--- Инициализация тестовых данных ---");

        Category salaryCategory = categoryFacade.createCategory("Зарплата", TransactionType.INCOME);
        Category foodCategory = categoryFacade.createCategory("Еда", TransactionType.EXPENSE);
        Category rentCategory = categoryFacade.createCategory("Аренда", TransactionType.EXPENSE);

        BankAccount mainAccount = accountFacade.createAccount("Основной счет", new BigDecimal("100000"));
        BankAccount savingsAccount = accountFacade.createAccount("Накопительный", new BigDecimal("50000"));

        operationFacade.createOperation(TransactionType.INCOME, salaryCategory.getId(),
                mainAccount.getId(), new BigDecimal("50000"),
                LocalDate.now().minusDays(10), "Зарплата за месяц");

        operationFacade.createOperation(TransactionType.EXPENSE, foodCategory.getId(),
                mainAccount.getId(), new BigDecimal("5000"),
                LocalDate.now().minusDays(5), "Продукты");

        operationFacade.createOperation(TransactionType.EXPENSE, rentCategory.getId(),
                mainAccount.getId(), new BigDecimal("30000"),
                LocalDate.now().minusDays(3), "Аренда квартиры");

        System.out.println("Тестовые данные созданы!");
    }

    private void demonstrateDomainModel() {
        System.out.println("\n=== ДОМЕННАЯ МОДЕЛЬ ===");

        System.out.println("\n--- Счета ---");
        accountFacade.getAll().forEach(account ->
                System.out.printf("Счет: %s, Баланс: %.2f%n",
                        account.getName(), account.getBalance()));

        System.out.println("\n--- Категории ---");
        categoryFacade.getAllCategories().forEach(category ->
                System.out.printf("Категория: %s, Тип: %s%n",
                        category.getName(), category.getType().getDescription()));

        System.out.println("\n--- Операции ---");
        operationFacade.getAllOperations().forEach(operation ->
                System.out.printf("Операция: %s, Сумма: %.2f, Дата: %s%n",
                        operation.getDescription(), operation.getAmount(), operation.getDate()));
    }

    private void demonstrateFacades() {
        System.out.println("\n=== ФАСАДЫ ===");

        System.out.println("\n--- BankAccountFacade ---");
        BankAccount newAccount = accountFacade.createAccount("Тестовый счет", new BigDecimal("1000"));
        System.out.println("Создан новый счет: " + newAccount.getName());

        System.out.println("\n--- CategoryFacade ---");
        Category newCategory = categoryFacade.createCategory("Развлечения", TransactionType.EXPENSE);
        System.out.println("Создана новая категория: " + newCategory.getName());

        System.out.println("\n--- OperationFacade ---");
        var accounts = accountFacade.getAll();
        var categories = categoryFacade.getAllCategories();

        if (!accounts.isEmpty() && !categories.isEmpty()) {
            operationFacade.createOperation(TransactionType.EXPENSE,
                    categories.get(0).getId(),
                    accounts.get(0).getId(),
                    new BigDecimal("2000"),
                    LocalDate.now(),
                    "Тестовая операция через фасад");
            System.out.println("Создана тестовая операция через фасад");
        }
    }

    private void demonstrateCommandsAndDecorators() {
        System.out.println("\n=== КОМАНДЫ + ДЕКОРАТОРЫ ===");

        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        System.out.println("\n--- Команда финансовой сводки ---");
        FinancialSummaryCommand financialCommand = new FinancialSummaryCommand(
                operationFacade, startDate, endDate
        );
        FinancialSummaryDTO financialResult = financialCommand.execute();
        System.out.printf("Доходы: %.2f, Расходы: %.2f, Баланс: %.2f%n",
                financialResult.totalIncome(),
                financialResult.totalExpense(),
                financialResult.totalChange());

        System.out.println("\n--- Команда с TimingDecorator ---");
        CategorySummaryCommand categoryCommand = new CategorySummaryCommand(
                operationFacade,
                startDate, endDate,
                categoryFacade.getAllCategories()
        );
        TimingCommandDecorator<CategorySummaryDTO> timedCommand =
                new TimingCommandDecorator<>(categoryCommand);

        CategorySummaryDTO categoryResult = timedCommand.execute();
        System.out.printf("Аналитика по категориям выполнена за %.2f мс%n",
                timedCommand.getExecutionTimeMs());
        System.out.println("Категорий проанализировано: " +
                categoryResult.categoryAnalytics().size());
    }

    private void demonstrateImportExport() {
        System.out.println("\n=== ИМПОРТ/ЭКСПОРТ ===");

        // Показываем текущую директорию
        String currentDir = System.getProperty("user.dir");
        System.out.println("📁 Текущая директория: " + currentDir);

        try {
            // 1. Демонстрация экспорта во все форматы
            demonstrateExport();

            // 2. Демонстрация импорта из всех форматов
            demonstrateImport();

        } catch (Exception e) {
            System.out.println("❌ Ошибка при импорте/экспорте: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void demonstrateExport() {
        System.out.println("\n--- ЭКСПОРТ ДАННЫХ ---");

        try {
            // Экспорт в JSON
            System.out.println("\n📊 Экспорт в JSON:");
            String jsonData = importExportFacade.exportToJson();
            System.out.println("Данные JSON (первые 200 символов):");
            System.out.println(jsonData.length() > 200 ? jsonData.substring(0, 200) + "..." : jsonData);

            File jsonFile = new File("export_data.json");
            importExportFacade.exportToFile(jsonFile, "json");
            System.out.println("✅ JSON файл создан: " + jsonFile.getAbsolutePath());
            System.out.println("📏 Размер: " + jsonFile.length() + " байт");

            // Экспорт в CSV
            System.out.println("\n📊 Экспорт в CSV:");
            String csvData = importExportFacade.exportToCsv();
            System.out.println("Данные CSV (первые 200 символов):");
            System.out.println(csvData.length() > 200 ? csvData.substring(0, 200) + "..." : csvData);

            File csvFile = new File("export_data.csv");
            importExportFacade.exportToFile(csvFile, "csv");
            System.out.println("✅ CSV файл создан: " + csvFile.getAbsolutePath());
            System.out.println("📏 Размер: " + csvFile.length() + " байт");

            // Экспорт в YAML
            System.out.println("\n📊 Экспорт в YAML:");
            String yamlData = importExportFacade.exportToYaml();
            System.out.println("Данные YAML (первые 200 символов):");
            System.out.println(yamlData.length() > 200 ? yamlData.substring(0, 200) + "..." : yamlData);

            File yamlFile = new File("export_data.yaml");
            importExportFacade.exportToFile(yamlFile, "yaml");
            System.out.println("✅ YAML файл создан: " + yamlFile.getAbsolutePath());
            System.out.println("📏 Размер: " + yamlFile.length() + " байт");

            System.out.println("\n🎉 ВСЕ ФАЙЛЫ ЭКСПОРТА СОЗДАНЫ!");
            System.out.println("📍 Они находятся в: " + System.getProperty("user.dir"));

        } catch (Exception e) {
            System.out.println("❌ Ошибка экспорта: " + e.getMessage());
        }
    }

    private void demonstrateImport() {
        System.out.println("\n--- ИМПОРТ ДАННЫХ ---");

        try {
            // Импорт из JSON
            File jsonFile = new File("export_data.json");
            if (jsonFile.exists()) {
                System.out.println("\n📥 Импорт из JSON:");
                ImportStatistic jsonStats = importExportFacade.importFromJson(jsonFile);
                System.out.println("Результат импорта JSON: " + jsonStats);
            } else {
                System.out.println("❌ JSON файл для импорта не найден: " + jsonFile.getAbsolutePath());
            }

            // Импорт из CSV
            File csvFile = new File("export_data.csv");
            if (csvFile.exists()) {
                System.out.println("\n📥 Импорт из CSV:");
                ImportStatistic csvStats = importExportFacade.importFromCsv(csvFile);
                System.out.println("Результат импорта CSV: " + csvStats);
            } else {
                System.out.println("❌ CSV файл для импорта не найден: " + csvFile.getAbsolutePath());
            }

            // Импорт из YAML
            File yamlFile = new File("export_data.yaml");
            if (yamlFile.exists()) {
                System.out.println("\n📥 Импорт из YAML:");
                ImportStatistic yamlStats = importExportFacade.importFromYaml(yamlFile);
                System.out.println("Результат импорта YAML: " + yamlStats);
            } else {
                System.out.println("❌ YAML файл для импорта не найден: " + yamlFile.getAbsolutePath());
            }

        } catch (Exception e) {
            System.out.println("❌ Ошибка импорта: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void demonstrateAnalytics() {
        System.out.println("\n=== АНАЛИТИКА ===");

        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        FinancialSummaryDTO summary = analyticsFacade.getFinancialSummary(startDate, endDate);
        System.out.printf("\nФинансовая сводка (%s - %s):%n", startDate, endDate);
        System.out.printf("Общие доходы: %.2f%n", summary.totalIncome());
        System.out.printf("Общие расходы: %.2f%n", summary.totalExpense());
        System.out.printf("Баланс: %.2f%n", summary.totalChange());

        CategorySummaryDTO categoryReport = analyticsFacade.getCategorySummary(startDate, endDate);
        System.out.println("\nАналитика по категориям:");
        categoryReport.categoryAnalytics().forEach(stat ->
                System.out.printf("  %s: %.2f (%.1f%%)%n",
                        stat.category().getName(), stat.amount(), stat.percentage()));
    }

    private void demonstrateBalanceRecalculation() {
        System.out.println("\n=== ПЕРЕСЧЕТ БАЛАНСА ===");

        var result = balanceFacade.recalculateBalancesWithTiming();

        System.out.println("Результат пересчета балансов:");
        System.out.println("Проверено счетов: " + result.getCheckedAccounts());
        System.out.println("Исправлено счетов: " + result.getCorrectedAccounts());
        System.out.println("Общее расхождение: " + result.getTotalDiscrepancy());

        if (result.hasCorrections()) {
            System.out.println("\nКорректировки:");
            result.getCorrections().forEach(System.out::println);
        } else {
            System.out.println("Расхождений не найдено!");
        }
    }

    private void demonstrateCache() {
        System.out.println("\n=== ПРОКСИ-КЭШ ===");

        var cacheStats = cacheFacade.getCacheStats();
        System.out.println("Статистика кэша:");
        System.out.println("Счетов в кэше: " + cacheStats.accountsInCache());
        System.out.println("Категорий в кэше: " + cacheStats.categoriesInCache());
        System.out.println("Операций в кэше: " + cacheStats.operationsInCache());

        System.out.println("\n--- Обновление кэша ---");
        cacheFacade.refreshAllCaches();

        var newStats = cacheFacade.getCacheStats();
        System.out.println("После обновления:");
        System.out.println("Счетов в кэше: " + newStats.accountsInCache());
        System.out.println("Категорий в кэше: " + newStats.categoriesInCache());
        System.out.println("Операций в кэше: " + newStats.operationsInCache());
    }

    private void manageBankAccounts(Scanner scanner) {
        while (true) {
            System.out.println("\n=== УПРАВЛЕНИЕ СЧЕТАМИ ===");
            System.out.println("1. Показать все счета");
            System.out.println("2. Создать счет");
            System.out.println("3. Обновить счет");
            System.out.println("4. Удалить счет");
            System.out.println("5. Показать операции по счету");
            System.out.println("0. Назад");
            System.out.print("Выберите пункт: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showAllAccounts();
                    break;
                case "2":
                    createAccount(scanner);
                    break;
                case "3":
                    updateAccount(scanner);
                    break;
                case "4":
                    deleteAccount(scanner);
                    break;
                case "5":
                    showAccountOperations(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Неверный выбор");
            }
        }
    }

    private void showAllAccounts() {
        System.out.println("\n--- Все счета ---");
        var accounts = accountFacade.getAll();
        if (accounts.isEmpty()) {
            System.out.println("Счетов нет");
            return;
        }

        accounts.forEach(account ->
                System.out.printf("ID: %s, Название: %s, Баланс: %.2f%n",
                        account.getId(), account.getName(), account.getBalance())
        );
    }

    private void createAccount(Scanner scanner) {
        System.out.print("Введите название счета: ");
        String name = scanner.nextLine();

        System.out.print("Введите начальный баланс: ");
        try {
            BigDecimal balance = new BigDecimal(scanner.nextLine());
            BankAccount account = accountFacade.createAccount(name, balance);
            System.out.println("Счет создан: " + account.getName());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: неверный формат числа");
        } catch (Exception e) {
            System.out.println("Ошибка при создании счета: " + e.getMessage());
        }
    }

    private void updateAccount(Scanner scanner) {
        showAllAccounts();
        System.out.print("Введите ID счета для обновления: ");
        String accountId = scanner.nextLine();

        var accountOpt = accountFacade.getAll().stream()
                .filter(a -> a.getId().equals(accountId))
                .findFirst();

        if (accountOpt.isEmpty()) {
            System.out.println("Счет не найден");
            return;
        }

        BankAccount account = accountOpt.get();

        System.out.print("Введите новое название счета (текущее: " + account.getName() + "): ");
        String newName = scanner.nextLine();

        System.out.print("Введите новый баланс (текущий: " + account.getBalance() + "): ");
        try {
            BigDecimal newBalance = new BigDecimal(scanner.nextLine());
            account.setName(newName);
            account.setBalance(newBalance);

            accountFacade.updateAccount(account);
            System.out.println("Счет обновлен");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: неверный формат числа");
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении счета: " + e.getMessage());
        }
    }

    private void deleteAccount(Scanner scanner) {
        showAllAccounts();
        System.out.print("Введите ID счета для удаления: ");
        String accountId = scanner.nextLine();

        try {
            accountFacade.remove(accountId);
            System.out.println("Счет удален");
        } catch (Exception e) {
            System.out.println("Ошибка при удалении счета: " + e.getMessage());
        }
    }

    private void showAccountOperations(Scanner scanner) {
        showAllAccounts();
        System.out.print("Введите ID счета: ");
        String accountId = scanner.nextLine();

        try {
            var operations = operationFacade.getOperationsByAccountId(accountId);
            if (operations.isEmpty()) {
                System.out.println("Операций по счету нет");
                return;
            }

            System.out.println("\n--- Операции по счету ---");
            operations.forEach(op ->
                    System.out.printf("Операция: %s, Сумма: %.2f, Дата: %s%n",
                            op.getDescription(), op.getAmount(), op.getDate())
            );
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }


    private void manageOperations(Scanner scanner) {
        while (true) {
            System.out.println("\n=== УПРАВЛЕНИЕ ОПЕРАЦИЯМИ ===");
            System.out.println("1. Показать все операции");
            System.out.println("2. Создать операцию");
            System.out.println("3. Обновить операцию");
            System.out.println("4. Удалить операцию");
            System.out.println("0. Назад");
            System.out.print("Выберите пункт: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showAllOperations();
                    break;
                case "2":
                    createOperation(scanner);
                    break;
                case "3":
                    updateOperation(scanner);
                    break;
                case "4":
                    deleteOperation(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Неверный выбор");
            }
        }
    }

    private void showAllOperations() {
        System.out.println("\n--- Все операции ---");
        var operations = operationFacade.getAllOperations();
        if (operations.isEmpty()) {
            System.out.println("Операций нет");
            return;
        }

        operations.forEach(op ->
                System.out.printf("ID: %s, Описание: %s, Сумма: %.2f, Дата: %s%n",
                        op.getId(), op.getDescription(), op.getAmount(), op.getDate())
        );
    }

    private void createOperation(Scanner scanner) {
        try {
            System.out.println("\n--- Доступные счета ---");
            var accounts = accountFacade.getAll();
            if (accounts.isEmpty()) {
                System.out.println("Нет доступных счетов. Сначала создайте счет.");
                return;
            }
            accounts.forEach(acc -> System.out.printf("ID: %s, Название: %s%n", acc.getId(), acc.getName()));

            System.out.print("Введите ID счета: ");
            String accountId = scanner.nextLine();

            System.out.println("\n--- Доступные категории ---");
            var categories = categoryFacade.getAllCategories();
            if (categories.isEmpty()) {
                System.out.println("Нет доступных категорий. Сначала создайте категории.");
                return;
            }
            categories.forEach(cat -> System.out.printf("ID: %s, Название: %s, Тип: %s%n",
                    cat.getId(), cat.getName(), cat.getType().getDescription()));

            System.out.print("Введите ID категории: ");
            String categoryId = scanner.nextLine();

            System.out.print("Выберите тип (1 - Доход, 2 - Расход): ");
            String typeChoice = scanner.nextLine();
            TransactionType type = "1".equals(typeChoice) ? TransactionType.INCOME : TransactionType.EXPENSE;

            System.out.print("Введите сумму: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

            System.out.print("Введите описание: ");
            String description = scanner.nextLine();

            LocalDate date = LocalDate.now();

            operationFacade.createOperation(type, categoryId, accountId, amount, date, description);
            System.out.println("Операция создана успешно");

        } catch (NumberFormatException e) {
            System.out.println("Ошибка: неверный формат числа");
        } catch (Exception e) {
            System.out.println("Ошибка при создании операции: " + e.getMessage());
        }
    }

    private void updateOperation(Scanner scanner) {
        showAllOperations();
        System.out.print("Введите ID операции для обновления: ");
        String operationId = scanner.nextLine();

        var operationOpt = operationFacade.getAllOperations().stream()
                .filter(op -> op.getId().equals(operationId))
                .findFirst();

        if (operationOpt.isEmpty()) {
            System.out.println("Операция не найдена");
            return;
        }

        Operation currentOperation = operationOpt.get();

        try {
            System.out.printf("Текущий тип: %s%n", currentOperation.getType());
            System.out.print("Изменить тип? (1 - Да, 2 - Нет): ");
            String changeType = scanner.nextLine();
            TransactionType newType = currentOperation.getType();
            if ("1".equals(changeType)) {
                System.out.print("Выберите тип (1 - Доход, 2 - Расход): ");
                String typeChoice = scanner.nextLine();
                newType = "1".equals(typeChoice) ? TransactionType.INCOME : TransactionType.EXPENSE;
            }

            System.out.printf("Текущий счет: %s%n", currentOperation.getBankAccountId());
            System.out.print("Изменить счет? (1 - Да, 2 - Нет): ");
            String changeAccount = scanner.nextLine();
            String newAccountId = currentOperation.getBankAccountId();
            if ("1".equals(changeAccount)) {
                System.out.println("\n--- Доступные счета ---");
                var accounts = accountFacade.getAll();
                accounts.forEach(acc -> System.out.printf("ID: %s, Название: %s%n", acc.getId(), acc.getName()));
                System.out.print("Введите новый ID счета: ");
                newAccountId = scanner.nextLine();
            }

            System.out.printf("Текущая категория: %s%n", currentOperation.getCategoryId());
            System.out.print("Изменить категорию? (1 - Да, 2 - Нет): ");
            String changeCategory = scanner.nextLine();
            String newCategoryId = currentOperation.getCategoryId();
            if ("1".equals(changeCategory)) {
                System.out.println("\n--- Доступные категории ---");
                var categories = categoryFacade.getAllCategories();
                categories.forEach(cat -> System.out.printf("ID: %s, Название: %s, Тип: %s%n",
                        cat.getId(), cat.getName(), cat.getType().getDescription()));
                System.out.print("Введите новый ID категории: ");
                newCategoryId = scanner.nextLine();
            }

            System.out.printf("Текущая сумма: %.2f%n", currentOperation.getAmount());
            System.out.print("Введите новую сумму (или Enter чтобы оставить текущую): ");
            String amountInput = scanner.nextLine();
            BigDecimal newAmount = currentOperation.getAmount();
            if (!amountInput.trim().isEmpty()) {
                newAmount = new BigDecimal(amountInput);
            }

            System.out.printf("Текущая дата: %s%n", currentOperation.getDate());
            System.out.print("Изменить дату? (1 - Да, 2 - Нет): ");
            String changeDate = scanner.nextLine();
            LocalDate newDate = currentOperation.getDate();
            if ("1".equals(changeDate)) {
                System.out.print("Введите новую дату (гггг-мм-дд): ");
                String dateInput = scanner.nextLine();
                newDate = LocalDate.parse(dateInput);
            }

            System.out.printf("Текущее описание: %s%n", currentOperation.getDescription());
            System.out.print("Введите новое описание (или Enter чтобы оставить текущее): ");
            String newDescription = scanner.nextLine();
            if (newDescription.trim().isEmpty()) {
                newDescription = currentOperation.getDescription();
            }

            Operation updatedOperation = operationFacade.updateOperation(
                    operationId, newType, newCategoryId, newAccountId,
                    newAmount, newDate, newDescription
            );

            System.out.printf("Операция обновлена: %s, Сумма: %.2f, Дата: %s%n",
                    updatedOperation.getDescription(), updatedOperation.getAmount(), updatedOperation.getDate());

        } catch (NumberFormatException e) {
            System.out.println("Ошибка: неверный формат числа");
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении операции: " + e.getMessage());
        }
    }

    private void deleteOperation(Scanner scanner) {
        showAllOperations();
        System.out.print("Введите ID операции для удаления: ");
        String operationId = scanner.nextLine();

        try {
            operationFacade.remove(operationId);
            System.out.println("Операция удалена");
        } catch (Exception e) {
            System.out.println("Ошибка при удалении операции: " + e.getMessage());
        }
    }


    private void manageCategories(Scanner scanner) {
        while (true) {
            System.out.println("\n=== УПРАВЛЕНИЕ КАТЕГОРИЯМИ ===");
            System.out.println("1. Показать все категории");
            System.out.println("2. Создать категорию");
            System.out.println("3. Обновить категорию");
            System.out.println("4. Удалить категорию");
            System.out.println("0. Назад");
            System.out.print("Выберите пункт: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showAllCategories();
                    break;
                case "2":
                    createCategory(scanner);
                    break;
                case "3":
                    updateCategory(scanner);
                    break;
                case "4":
                    deleteCategory(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Неверный выбор");
            }
        }
    }

    private void showAllCategories() {
        System.out.println("\n--- Все категории ---");
        var categories = categoryFacade.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("Категорий нет");
            return;
        }

        categories.forEach(category ->
                System.out.printf("ID: %s, Название: %s, Тип: %s%n",
                        category.getId(), category.getName(), category.getType().getDescription())
        );
    }

    private void createCategory(Scanner scanner) {
        System.out.print("Введите название категории: ");
        String name = scanner.nextLine();

        System.out.print("Выберите тип (1 - Доход, 2 - Расход): ");
        String typeChoice = scanner.nextLine();

        TransactionType type;
        if ("1".equals(typeChoice)) {
            type = TransactionType.INCOME;
        } else if ("2".equals(typeChoice)) {
            type = TransactionType.EXPENSE;
        } else {
            System.out.println("Неверный выбор типа");
            return;
        }

        try {
            Category category = categoryFacade.createCategory(name, type);
            System.out.println("Категория создана: " + category.getName());
        } catch (Exception e) {
            System.out.println("Ошибка при создании категории: " + e.getMessage());
        }
    }

    private void updateCategory(Scanner scanner) {
        showAllCategories();
        System.out.print("Введите ID категории для обновления: ");
        String categoryId = scanner.nextLine();

        var categoryOpt = categoryFacade.getAllCategories().stream()
                .filter(c -> c.getId().equals(categoryId))
                .findFirst();

        if (categoryOpt.isEmpty()) {
            System.out.println("Категория не найдена");
            return;
        }

        Category currentCategory = categoryOpt.get();

        try {
            System.out.printf("Текущее название: %s%n", currentCategory.getName());
            System.out.print("Введите новое название категории (или Enter чтобы оставить текущее): ");
            String newName = scanner.nextLine();
            if (newName.trim().isEmpty()) {
                newName = currentCategory.getName();
            }

            System.out.printf("Текущий тип: %s%n", currentCategory.getType().getDescription());
            System.out.print("Изменить тип? (1 - Да, 2 - Нет): ");
            String changeType = scanner.nextLine();

            TransactionType newType = currentCategory.getType();
            if ("1".equals(changeType)) {
                System.out.print("Выберите тип (1 - Доход, 2 - Расход): ");
                String typeChoice = scanner.nextLine();
                newType = "1".equals(typeChoice) ? TransactionType.INCOME : TransactionType.EXPENSE;
            }

            Category updatedCategory = categoryFacade.updateCategory(categoryId, newName, newType);

            System.out.printf("Категория обновлена: %s, Тип: %s%n",
                    updatedCategory.getName(), updatedCategory.getType().getDescription());

        } catch (Exception e) {
            System.out.println("Ошибка при обновлении категории: " + e.getMessage());
        }
    }

    private void deleteCategory(Scanner scanner) {
        showAllCategories();
        System.out.print("Введите ID категории для удаления: ");
        String categoryId = scanner.nextLine();

        try {
            categoryFacade.remove(categoryId);
            System.out.println("Категория удалена");
        } catch (Exception e) {
            System.out.println("Ошибка при удалении категории: " + e.getMessage());
        }
    }

}