package zarg.debitcredit.service;

import zarg.debitcredit.controllers.CustomerResponse;
import zarg.debitcredit.controllers.RegisterCustomerRequest;
import zarg.debitcredit.domain.Customer;

public interface CustomerService {
    Customer registerCustomer(RegisterCustomerRequest request);

    CustomerResponse findCustomerByBid(String customerBid);

    boolean isAccountOwner(String customerBid, String accountBid);
}
