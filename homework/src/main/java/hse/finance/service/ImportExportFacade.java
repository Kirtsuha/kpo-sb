package hse.finance.service;

import hse.finance.dto.ImportStatistic;
import hse.finance.importexport.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class ImportExportFacade {
    private final BankAccountFacade accountFacade;
    private final CategoryFacade categoryFacade;
    private final OperationFacade operationFacade;
    private final DataImporterFactory importerFactory;

    @Autowired
    public ImportExportFacade(BankAccountFacade accountFacade,
                              CategoryFacade categoryFacade,
                              OperationFacade operationFacade,
                              DataImporterFactory importerFactory) {
        this.accountFacade = accountFacade;
        this.categoryFacade = categoryFacade;
        this.operationFacade = operationFacade;
        this.importerFactory = importerFactory;
    }

    public ImportStatistic importData(File file) {
        DataImporter importer = importerFactory.createImporter(file);
        return importer.importData(file);
    }

    public ImportStatistic importData(File file, String format) {
        DataImporter importer = importerFactory.createImporter(format);
        return importer.importData(file);
    }

    public ImportStatistic importFromJson(File file) {
        return importData(file, "json");
    }

    public ImportStatistic importFromYaml(File file) {
        return importData(file, "yaml");
    }

    public ImportStatistic importFromCsv(File file) {
        return importData(file, "csv");
    }

    public String exportToJson() {
        JsonExportVisitor visitor = new JsonExportVisitor();
        exportData(visitor);
        return visitor.getResult();
    }

    public String exportToYaml() {
        YamlExportVisitor visitor = new YamlExportVisitor();
        exportData(visitor);
        return visitor.getResult();
    }

    public String exportToCsv() {
        CsvExportVisitor visitor = new CsvExportVisitor();
        exportData(visitor);
        return visitor.getResult();
    }

    public void exportToFile(File file, String format) throws IOException {
        String data = exportToFormat(format);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data);
        }
    }

    public void exportToFile(File file) throws IOException {
        String fileName = file.getName().toLowerCase();
        String format;

        if (fileName.endsWith(".json")) {
            format = "json";
        } else if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
            format = "yaml";
        } else if (fileName.endsWith(".csv")) {
            format = "csv";
        } else {
            throw new IllegalArgumentException("Неизвестный формат файла: " + fileName);
        }

        exportToFile(file, format);
    }

    public String exportAccountsToJson() {
        JsonExportVisitor visitor = new JsonExportVisitor();
        accountFacade.getAll().forEach(account -> account.accept(visitor));
        return visitor.getResult();
    }


    public String exportCategoriesToJson() {
        JsonExportVisitor visitor = new JsonExportVisitor();
        categoryFacade.getAllCategories().forEach(category -> category.accept(visitor));
        return visitor.getResult();
    }

    public String exportOperationsToJson() {
        JsonExportVisitor visitor = new JsonExportVisitor();
        operationFacade.getAllOperations().forEach(operation -> operation.accept(visitor));
        return visitor.getResult();
    }

    private void exportData(ExportVisitor visitor) {
        accountFacade.getAll().forEach(account -> account.accept(visitor));
        categoryFacade.getAllCategories().forEach(category -> category.accept(visitor));
        operationFacade.getAllOperations().forEach(operation -> operation.accept(visitor));
    }


    private String exportToFormat(String format) {
        switch (format.toLowerCase()) {
            case "json":
                return exportToJson();
            case "yaml":
            case "yml":
                return exportToYaml();
            case "csv":
                return exportToCsv();
            default:
                throw new IllegalArgumentException("Неизвестный формат: " + format);
        }
    }

    public DataStats getDataStats() {
        int accountCount = accountFacade.getAll().size();
        int categoryCount = categoryFacade.getAllCategories().size();
        int operationCount = operationFacade.getAllOperations().size();

        return new DataStats(accountCount, categoryCount, operationCount);
    }

    public record DataStats(int accountCount, int categoryCount, int operationCount) {

        @Override
            public String toString() {
                return String.format("Счетов: %d, Категорий: %d, Операций: %d",
                        accountCount, categoryCount, operationCount);
            }
        }
}