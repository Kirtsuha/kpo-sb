package hse.finance.id_generation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IdGenerationContext {

    @Value("#{${id.strategies:{}}}")
    private Map<String, String> config;
    private final Map<String, IdStrategy> strategies;
    private final IdStrategy defaultStrategy;

    @Autowired
    public IdGenerationContext(List<IdStrategy> strategies, IdStrategy defaultStrategy) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(s -> s.getType(), s -> s));
        this.defaultStrategy = defaultStrategy;
    }

    public String generateId(String entityType) {
        String strategyName = config.get(entityType.toLowerCase());

        if (strategyName == null) {
            System.out.println("Не найдена стратегия для: " + entityType + ", используется стратегия по умолчанию");
            return defaultStrategy.generateId(entityType);
        }

        IdStrategy strategy = strategies.get(strategyName);
        if (strategy == null) {
            System.out.println("Стратегия '" + strategyName + "' не найдена, используется стратегия по умолчанию");
            return defaultStrategy.generateId(entityType);
        }

        return strategy.generateId(entityType);
    }
}
