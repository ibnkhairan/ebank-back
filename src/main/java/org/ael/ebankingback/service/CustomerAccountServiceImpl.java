package org.ael.ebankingback.service;

import lombok.extern.slf4j.Slf4j;
import org.ael.ebankingback.dto.CustomerDTO;
import org.ael.ebankingback.entities.Customer;
import org.ael.ebankingback.exceptions.CustomerNotFoundException;
import org.ael.ebankingback.mappers.CustomerMapper;
import org.ael.ebankingback.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class CustomerAccountServiceImpl implements CustomerAccountService{

    private CustomerRepository customerRepository;
    private CustomerMapper customerMapper;

    public CustomerAccountServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {

        log.info("sauvegarde du nouveau customer");
        Customer customer=customerMapper.fromCustomerDTO(customerDTO);
        Customer customer1 = customerRepository.save(customer);
        return customerMapper.fromCustomer(customer1);

          /*
        List<CustomerDTO>customerDTOList = new ArrayList<>();
        for(Customer customer : customerList){
            CustomerDTO customerDTO = dtoMapper.fromCustomer(customer);
            customerDTOList.add(customerDTO);
        }*/
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("customer not found"));
        return customerMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {

        log.info("Mise à jour du customer");
        Customer customer=customerMapper.fromCustomerDTO(customerDTO);
        Customer customer1 = customerRepository.save(customer);
        return customerMapper.fromCustomer(customer1);
    }

    @Override
    public void deleteCustomer(Long customerId){
        customerRepository.deleteById(customerId);
    }


    @Override
    public List<CustomerDTO> listCustomers() {
        log.info("Recuperer la liste des clients");
        List<Customer> customerList = customerRepository.findAll();
        List<CustomerDTO> customerDTOList = customerList.stream()
                .map(customer -> customerMapper.fromCustomer(customer))
                .collect(Collectors.toList());
        /*
        List<CustomerDTO>customerDTOList = new ArrayList<>();
        for(Customer customer : customerList){
            CustomerDTO customerDTO = dtoMapper.fromCustomer(customer);
            customerDTOList.add(customerDTO);
        }*/
        return customerDTOList;
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        log.info("Chercher la liste des clients");
        List<Customer> customers = customerRepository.searchCustomers(keyword);
        List<CustomerDTO> customerDTOS = customers.stream().map(cust -> customerMapper.fromCustomer(cust)).collect(Collectors.toList());
        return customerDTOS;
    }
}
