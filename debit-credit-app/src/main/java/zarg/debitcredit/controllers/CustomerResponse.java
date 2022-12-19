package zarg.debitcredit.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record CustomerResponse(
        @NotBlank(message = "id is mandatory")
        String bid,
        @NotBlank(message = "givenName is mandatory")
        String givenName,
        @NotBlank(message = "surname is mandatory")
        String surname,
        @NotBlank(message = "emailAddress is mandatory")
        String emailAddress,
        List<String> accounts) {
}
