package zarg.debitcredit.controllers;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DebitAccountRequest(@NotNull @DecimalMin("0.01") BigDecimal amount) {
}
