package org.ael.ebankingback.web;


import lombok.extern.slf4j.Slf4j;
import org.ael.ebankingback.dto.*;
import org.ael.ebankingback.exceptions.BalanceNotSufficientException;
import org.ael.ebankingback.exceptions.BankAccountEmptyException;
import org.ael.ebankingback.exceptions.BankAccountNotFoundException;
import org.ael.ebankingback.service.BankAccountService;
import org.ael.ebankingback.service.OperationAccountService;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingFormatArgumentException;

@Slf4j
@RestController
@CrossOrigin("*")
public class BankAccountRestController {

    private final BankAccountService bankAccountService;
    private final OperationAccountService operationAccountService;

    public BankAccountRestController(BankAccountService bankAccountService, OperationAccountService operationAccountService) {
        this.bankAccountService = bankAccountService;
        this.operationAccountService = operationAccountService;
    }

    @GetMapping("/accounts/{accountId}")
    public BankAccountDTO getBankAccountDTO(@PathVariable String accountId) {
        try{
            return bankAccountService.getBankAccount(accountId);
        }catch (BankAccountNotFoundException e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(),e);
        }

    }

    @GetMapping("/accounts")
    public List<BankAccountDTO> listAccounts(){
        return bankAccountService.bankAccountList();
    }


    @GetMapping("/accounts/{accountId}/operations")
    public List<AccountOperationDTO> getHistory(@PathVariable String accountId){
        return operationAccountService.accountOperationHistory(accountId);
    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    public AccountHistoryDTO getAccountHistory(
            @PathVariable String accountId,
            @RequestParam(name="page",defaultValue = "0")  int page,
            @RequestParam(name = "size",defaultValue = "5")int size){
        try{
            return operationAccountService.getAccountOperationHistory(accountId,page,size);
        }catch(BankAccountNotFoundException e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(),e);
        }

    }

    @PostMapping("/accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) {
        try{
            this.bankAccountService.debit(debitDTO.getAccountId(), debitDTO.getAmount(), debitDTO.getDescription());
            return debitDTO;
        }catch(BankAccountNotFoundException e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(),e);
        }catch (BalanceNotSufficientException e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(),e);
        }catch (BankAccountEmptyException e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(),e);
        }


    }

    @PostMapping("/accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) {
        try{
            this.bankAccountService.credit(creditDTO.getAccountId(), creditDTO.getAmount(), creditDTO.getDescription());
            return creditDTO;
        }catch(BankAccountNotFoundException e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(),e);
        }

    }

    @PostMapping("/accounts/transfert")
    public void transfert(@RequestBody TransfertRequestDTO transfertRequestDTO){
        try{
            this.bankAccountService.transfert(transfertRequestDTO.getAccountSource(),
                    transfertRequestDTO.getAccountDestination(),
                    transfertRequestDTO.getAmount());

        }catch(MissingFormatArgumentException |BankAccountNotFoundException| BalanceNotSufficientException | BankAccountEmptyException e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    @GetMapping("/customer-accounts/{customerId}")
    public List<BankAccountDTO> getCustomerAccounts(@PathVariable Long customerId){
        List<BankAccountDTO> customerAccounts = new ArrayList<>();
        try{
            customerAccounts = this.bankAccountService.getCustomerAccounts(customerId);
            return customerAccounts;
        }catch ( BankAccountEmptyException e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(),e);
        }catch (BankAccountNotFoundException e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }
}
