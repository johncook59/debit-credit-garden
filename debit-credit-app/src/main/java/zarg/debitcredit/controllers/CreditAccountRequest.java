package zarg.debitcredit.controllers;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreditAccountRequest(
        @NotBlank(message = "accountId is mandatory")
        String accountId,
        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount) {
}
