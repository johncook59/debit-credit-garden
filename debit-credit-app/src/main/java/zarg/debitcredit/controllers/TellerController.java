package zarg.debitcredit.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import zarg.debitcredit.service.TellerService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teller")
@Slf4j
class TellerController {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    private final TellerService teller;
    private final ConversionService conversionService;

    public TellerController(TellerService teller, ConversionService conversionService) {
        this.teller = teller;
        this.conversionService = conversionService;
    }

    @GetMapping(value = "/{customerId}/{accountId}/balance")
    @ResponseBody
    public BalanceResponse balance(@PathVariable("customerId") String customerId,
                                   @PathVariable("accountId") String accountBid) {
        log.debug("Requesting balance for account {}", accountBid);
        return new BalanceResponse(accountBid, teller.balance(customerId, accountBid));
    }

    @PutMapping(value = "/{customerId}/credit")
    @ResponseBody
    public TransactionResponse credit(@PathVariable("customerId") String customerId,
                                      @RequestBody @Valid CreditAccountRequest request) {
        log.debug("Requesting {} credit to account {}", request.amount(), request.accountId());
        return conversionService.convert(teller.credit(customerId, request.accountId(), request.amount()),
                TransactionResponse.class);
    }

    @PutMapping(value = "/{customerId}/{accountId}/debit")
    @ResponseBody
    public TransactionResponse debit(@PathVariable("customerId") String customerId,
                                     @PathVariable("accountId") String accountId,
                                     @RequestBody @Valid DebitAccountRequest request) {
        log.debug("Requesting {} debit from account {}", request.amount(), accountId);
        return conversionService.convert(teller.debit(customerId, accountId, request.amount()), TransactionResponse.class);
    }

    @GetMapping(value = "/{customerId}/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, List<TransactionResponse>> transactions(
            @PathVariable("customerId") String customerId,
            @RequestParam(name = "from", required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime to) {

        if (from == null) {
            from = LocalDateTime.now(ZoneOffset.UTC).minus(1, ChronoUnit.DAYS);
        }

        if (to == null) {
            to = LocalDateTime.now(ZoneOffset.UTC);
        }

        log.debug("Finding transactions for {} from {}, to {}", customerId, from, to);

        return teller.findTransactions(customerId, from, to).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream()
                        .map(t -> Optional.ofNullable(conversionService.convert(t, TransactionResponse.class))
                                .orElseThrow(() -> new IllegalArgumentException("Unable to convert transaction " + t.getBid())))
                        .toList()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ErrorHandlerUtils.mapExceptions(ex);
    }
}
