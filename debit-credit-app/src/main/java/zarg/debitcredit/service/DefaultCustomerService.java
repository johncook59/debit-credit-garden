package zarg.debitcredit.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zarg.debitcredit.controllers.CustomerResponse;
import zarg.debitcredit.controllers.RegisterCustomerRequest;
import zarg.debitcredit.dao.CustomerDao;
import zarg.debitcredit.domain.Account;
import zarg.debitcredit.domain.Customer;

import java.util.Collections;

@Service
class DefaultCustomerService implements CustomerService {

    private final CustomerDao customerDao;
    private final ConversionService conversionService;

    DefaultCustomerService(CustomerDao customerDao, ConversionService conversionService) {
        this.customerDao = customerDao;
        this.conversionService = conversionService;
    }

    @Transactional
    @Override
    public Customer registerCustomer(RegisterCustomerRequest request) {

        Account account = Account.builder()
                .balance(request.initialBalance())
                .name("Current")
                .build();
        Customer customer = Customer.builder()
                .givenName(request.givenName())
                .surname(request.surname())
                .emailAddress(request.emailAddress())
                .password(request.password())
                .accounts(Collections.singletonList(account))
                .build();

        return customerDao.save(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse findCustomerByBid(String customerBid) {
        return conversionService.convert(customerDao.findByBid(customerBid)
                .orElseThrow(() -> new EntityNotFoundException(customerBid)), CustomerResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAccountOwner(String customerBid, String accountBid) {
        return customerDao.findByBidAndAccountBid(customerBid, accountBid).isPresent();
    }
}
