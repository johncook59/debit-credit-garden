package zarg.debitcredit.controllers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionResponse(
        @NotBlank(message = "bid is mandatory")
        String bid,
        @NotBlank(message = "Transaction direction is mandatory")
        String direction,
        @NotNull
        BigDecimal amount,
        @NotNull
        BigDecimal balance,
        @NotBlank(message = "userId is mandatory")
        String userId,
        @NotBlank(message = "accountId is mandatory")
        String accountId,
        @NotNull
        LocalDateTime processed) {
}
