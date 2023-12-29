package org.ael.ebankingback.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ael.ebankingback.dto.CustomerDTO;
import org.ael.ebankingback.exceptions.CustomerNotFoundException;
import org.ael.ebankingback.service.CustomerAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class CustomerRestController {

    private CustomerAccountService customerAccountService;

    @GetMapping("/customers")
    public List<CustomerDTO> customers(){
        return  customerAccountService.listCustomers();
    }

    @GetMapping("/customers/search")
    public List<CustomerDTO> searchCustomers(@RequestParam(name = "keyword",defaultValue = "") String keyword){
        return  customerAccountService.searchCustomers(keyword);
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) {
        try{
            return customerAccountService.getCustomer(customerId);
        }catch(CustomerNotFoundException e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(),e);
        }

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
        try {
            customerAccountService.deleteCustomer(id);
        }catch (CustomerNotFoundException e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(),e);
        }
    }

}
