package org.ael.ebankingback.web;


import org.ael.ebankingback.dto.*;
import org.ael.ebankingback.exceptions.BalanceNotSufficientException;
import org.ael.ebankingback.exceptions.BankAccountNotFoundException;
import org.ael.ebankingback.service.BankAccountService;
import org.ael.ebankingback.service.OperationAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public BankAccountDTO getBankAccountDTO(@PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
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
            @RequestParam(name = "size",defaultValue = "5")int size) throws BankAccountNotFoundException {
        return operationAccountService.getAccountOperationHistory(accountId,page,size);
    }

    @PostMapping("/accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {

        this.bankAccountService.debit(debitDTO.getAccountId(), debitDTO.getAmount(), debitDTO.getDescription());

        return debitDTO;
    }

    @PostMapping("/accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFoundException {

        this.bankAccountService.credit(creditDTO.getAccountId(), creditDTO.getAmount(), creditDTO.getDescription());

        return creditDTO;
    }

    @PostMapping("/accounts/transfert")
    public void credit(@RequestBody TransfertRequestDTO transfertRequestDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {

        this.bankAccountService.transfert(transfertRequestDTO.getAccountIdSource(),
                                          transfertRequestDTO.getAccountDestination(),
                                          transfertRequestDTO.getAmount());

    }
}
