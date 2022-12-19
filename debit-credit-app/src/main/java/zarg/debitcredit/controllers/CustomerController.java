package zarg.debitcredit.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import zarg.debitcredit.service.CustomerService;

import java.util.Map;

@RestController
@RequestMapping("customer")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;
    private final ConversionService conversionService;

    public CustomerController(CustomerService customerService, ConversionService conversionService) {
        this.customerService = customerService;
        this.conversionService = conversionService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CustomerResponse findByCustomerId(@RequestParam("customerId") @NotNull String bid) {
        log.debug("Finding " + bid);
        return conversionService.convert(customerService.findCustomerByBid(bid), CustomerResponse.class);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CustomerResponse register(@RequestBody @Valid RegisterCustomerRequest request) {
        log.debug("Registering {} {}", request.givenName(), request.surname());

        return conversionService.convert(customerService.registerCustomer(request), CustomerResponse.class);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ErrorHandlerUtils.mapExceptions(ex);
    }
}
