package hse.finance.id_generation;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("uuid")
@Primary
public class UUIDStrategy implements IdStrategy{
    @Override
    public String generateId(String entity) {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getType() {
        return "uuid";
    }
}

