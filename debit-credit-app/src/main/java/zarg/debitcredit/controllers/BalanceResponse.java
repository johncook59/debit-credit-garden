package zarg.debitcredit.controllers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BalanceResponse(
        @NotBlank(message = "accountId is mandatory")
        String accountId,
        @NotNull
        BigDecimal balance) {
}
