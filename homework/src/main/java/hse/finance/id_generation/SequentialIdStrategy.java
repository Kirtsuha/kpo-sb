package hse.finance.id_generation;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component("sequential")
public class SequentialIdStrategy implements IdStrategy{
    private final Map<String, AtomicLong> sequence = new ConcurrentHashMap<>();
    public String generateId(String entity) {
        return entity + "_" + sequence.computeIfAbsent(entity, k -> new AtomicLong(1))
                .getAndIncrement();
    }

    @Override
    public String getType() {
        return "sequential";
    }
}
