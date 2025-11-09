package hse.finance.command;

import lombok.Getter;
@Getter

public class TimingCommandDecorator<T> implements Command<T>{
    private final Command<T> command;
    public long executionTime;
    public double executionTimeMs;
    public TimingCommandDecorator(Command<T> command) {
        this.command = command;
    }

    @Override
    public T execute() {
        long startTime = System.nanoTime();
        T ans = command.execute();
        long endTime = System.nanoTime();
        executionTime = endTime - startTime;
        executionTimeMs = executionTime / 1_000_000.0;
        return ans;
    }

}
