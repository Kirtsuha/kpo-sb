package hse.finance.id_generation;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component("timestamp")
public class TimestampIdStrategy implements IdStrategy {

    @Override
    public String generateId(String Entity) {
        return "ID_" +
                System.currentTimeMillis() +
                ThreadLocalRandom.current().nextInt(1, 1000);
    }

    @Override
    public String getType() {
        return "timestamp";
    }
}
