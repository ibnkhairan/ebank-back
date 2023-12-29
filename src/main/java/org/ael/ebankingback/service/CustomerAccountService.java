package org.ael.ebankingback.service;

import lombok.NonNull;
import org.ael.ebankingback.dto.CustomerDTO;
import org.ael.ebankingback.exceptions.CustomerNotFoundException;

import java.util.List;

public interface CustomerAccountService {
    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(@NonNull Long customerId) throws CustomerNotFoundException;
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    List<CustomerDTO> listCustomers();

    List<CustomerDTO> searchCustomers(@NonNull String keyword);
}
