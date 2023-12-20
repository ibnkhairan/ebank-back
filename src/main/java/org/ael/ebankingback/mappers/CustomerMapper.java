package org.ael.ebankingback.mappers;

import org.ael.ebankingback.dto.CustomerDTO;
import org.ael.ebankingback.entities.Customer;

public interface CustomerMapper {
    CustomerDTO fromCustomer(Customer customer);

    Customer fromCustomerDTO(CustomerDTO customerDTO);



}
