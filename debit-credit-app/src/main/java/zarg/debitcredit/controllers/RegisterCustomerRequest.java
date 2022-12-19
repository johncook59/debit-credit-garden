package zarg.debitcredit.controllers;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RegisterCustomerRequest(
        @NotBlank(message = "givenName is mandatory")
        String givenName,
        @NotBlank(message = "surname is mandatory")
        String surname,
        @NotBlank(message = "emailAddress is mandatory")
        String emailAddress,
        @NotBlank(message = "password is mandatory")
        String password,
        @DecimalMin("0.01")
        BigDecimal initialBalance) {
}
