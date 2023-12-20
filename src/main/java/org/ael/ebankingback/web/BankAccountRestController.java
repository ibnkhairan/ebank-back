package org.ael.ebankingback.web;


import org.ael.ebankingback.dto.AccountHistoryDTO;
import org.ael.ebankingback.dto.AccountOperationDTO;
import org.ael.ebankingback.dto.BankAccountDTO;
import org.ael.ebankingback.exceptions.BankAccountNotFoundException;
import org.ael.ebankingback.service.BankAccountService;
import org.ael.ebankingback.service.OperationAccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
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
}
