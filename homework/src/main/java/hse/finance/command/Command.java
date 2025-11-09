package hse.finance.command;

public interface Command<T> {
    T execute();
}
