package hse.finance.importexport;

import hse.finance.service.BankAccountFacade;
import hse.finance.service.CategoryFacade;
import hse.finance.service.OperationFacade;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DataImporterFactory {
    private final BankAccountFacade accountFacade;
    private final CategoryFacade categoryFacade;
    private final OperationFacade operationFacade;

    public DataImporterFactory(BankAccountFacade accountFacade,
                               CategoryFacade categoryFacade,
                               OperationFacade operationFacade) {
        this.accountFacade = accountFacade;
        this.categoryFacade = categoryFacade;
        this.operationFacade = operationFacade;
    }

    public DataImporter createImporter(String format) {
        switch (format.toLowerCase()) {
            case "json":
                return new JsonDataImporter(accountFacade, categoryFacade, operationFacade);
            case "yaml":
            case "yml":
                return new YamlDataImporter(accountFacade, categoryFacade, operationFacade);
            case "csv":
                return new CsvDataImporter(accountFacade, categoryFacade, operationFacade);
            default:
                throw new IllegalArgumentException("Неизвестный формат: " + format);
        }
    }

    public DataImporter createImporter(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".json")) {
            return createImporter("json");
        } else if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
            return createImporter("yaml");
        } else if (fileName.endsWith(".csv")) {
            return createImporter("csv");
        } else {
            throw new IllegalArgumentException("Неизвестный формат файла: " + fileName);
        }
    }
}