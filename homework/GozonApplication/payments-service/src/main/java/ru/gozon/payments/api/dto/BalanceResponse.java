package ru.gozon.payments.api.dto;

import java.math.BigDecimal;

public record BalanceResponse(String userId, BigDecimal balance) {}
