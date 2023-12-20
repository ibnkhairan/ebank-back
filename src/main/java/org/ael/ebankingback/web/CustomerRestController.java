package org.ael.ebankingback.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ael.ebankingback.dto.CustomerDTO;
import org.ael.ebankingback.entities.Customer;
import org.ael.ebankingback.exceptions.CustomerNotFoundException;
import org.ael.ebankingback.service.BankAccountService;
import org.ael.ebankingback.service.CustomerAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class CustomerRestController {

    private CustomerAccountService customerAccountService;

    @GetMapping("/customers")
    public List<CustomerDTO> customers(){
        return  customerAccountService.listCustomers();
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotFoundException {
        return customerAccountService.getCustomer(customerId);
    }

    @PostMapping("/customers")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){

        return customerAccountService.saveCustomer(customerDTO);
    }

    @PutMapping("/customers/{customerId}")
    public CustomerDTO updateCustomer(@PathVariable Long customerId,@RequestBody CustomerDTO customerDTO){
                customerDTO.setId(customerId);
                return customerAccountService.updateCustomer(customerDTO);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable Long id){
        customerAccountService.deleteCustomer(id);
    }

}
