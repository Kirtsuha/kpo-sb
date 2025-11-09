package hse.finance.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RecalculationDTO {
    private final int checkedAccounts;
    private final int correctedAccounts;
    private final List<String> corrections = new ArrayList<>();
    private final BigDecimal totalDiscrepancy;

    public RecalculationDTO(int checkedAccounts, int correctedAccounts,
                            List<String> corrections, BigDecimal totalDiscrepancy) {
        this.checkedAccounts = checkedAccounts;
        this.correctedAccounts = correctedAccounts;
        this.corrections.addAll(corrections);
        this.totalDiscrepancy = totalDiscrepancy;
    }

    public int getCheckedAccounts() { return checkedAccounts; }
    public int getCorrectedAccounts() { return correctedAccounts; }
    public List<String> getCorrections() { return new ArrayList<>(corrections); }
    public BigDecimal getTotalDiscrepancy() { return totalDiscrepancy; }

    public boolean hasCorrections() {
        return correctedAccounts > 0;
    }

    @Override
    public String toString() {
        return String.format(
                "Проверено счетов: %d, Исправлено: %d, Общая расхождение: %.2f",
                checkedAccounts, correctedAccounts, totalDiscrepancy
        );
    }
}