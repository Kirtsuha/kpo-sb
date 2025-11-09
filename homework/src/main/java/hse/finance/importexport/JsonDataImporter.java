package hse.finance.importexport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hse.finance.domain.BankAccount;
import hse.finance.domain.Category;
import hse.finance.domain.Operation;
import hse.finance.service.BankAccountFacade;
import hse.finance.service.CategoryFacade;
import hse.finance.service.OperationFacade;

import java.util.Arrays;
import java.util.Map;

public class JsonDataImporter extends DataImporter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonDataImporter(BankAccountFacade accountFacade,
                            CategoryFacade categoryFacade,
                            OperationFacade operationFacade) {
        super(accountFacade, categoryFacade, operationFacade);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void parseData(String rawData) throws Exception {
        Map<String, Object> data = objectMapper.readValue(
                rawData,
                new TypeReference<Map<String, Object>>() {}
        );

        if (data.containsKey("accounts")) {
            String accountsJson = objectMapper.writeValueAsString(data.get("accounts"));
            BankAccount[] accounts = objectMapper.readValue(accountsJson, BankAccount[].class);
            accountsToImport.addAll(Arrays.asList(accounts));
        }

        if (data.containsKey("categories")) {
            String categoriesJson = objectMapper.writeValueAsString(data.get("categories"));
            Category[] categories = objectMapper.readValue(categoriesJson, Category[].class);
            categoriesToImport.addAll(Arrays.asList(categories));
        }

        if (data.containsKey("operations")) {
            String operationsJson = objectMapper.writeValueAsString(data.get("operations"));
            Operation[] operations = objectMapper.readValue(operationsJson, Operation[].class);
            operationsToImport.addAll(Arrays.asList(operations));
        }
    }
}