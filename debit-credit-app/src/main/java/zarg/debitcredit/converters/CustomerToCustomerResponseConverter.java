package zarg.debitcredit.converters;

import org.springframework.core.convert.converter.Converter;
import zarg.debitcredit.controllers.CustomerResponse;
import zarg.debitcredit.domain.Account;
import zarg.debitcredit.domain.Customer;

public class CustomerToCustomerResponseConverter implements Converter<Customer, CustomerResponse> {
    @Override
    public CustomerResponse convert(Customer customer) {
        return CustomerResponse.builder()
                .bid(customer.getBid())
                .givenName(customer.getGivenName())
                .surname(customer.getSurname())
                .emailAddress(customer.getEmailAddress())
                .accounts(customer.getAccounts().stream()
                        .map(Account::getBid)
                        .toList())
                .build();
    }
}
