package hse.finance.id_generation;

public interface IdStrategy {
    String generateId(String entity);
    String getType();
}