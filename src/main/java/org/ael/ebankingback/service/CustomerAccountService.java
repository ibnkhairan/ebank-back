package org.ael.ebankingback.service;

import org.ael.ebankingback.dto.CustomerDTO;
import org.ael.ebankingback.exceptions.CustomerNotFoundException;

import java.util.List;

public interface CustomerAccountService {
    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    List<CustomerDTO> listCustomers();

    List<CustomerDTO> searchCustomers(String keyword);
}
