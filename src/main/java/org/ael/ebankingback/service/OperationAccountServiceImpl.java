package org.ael.ebankingback.service;

import lombok.extern.slf4j.Slf4j;
import org.ael.ebankingback.dto.AccountHistoryDTO;
import org.ael.ebankingback.dto.AccountOperationDTO;
import org.ael.ebankingback.entities.AccountOperation;
import org.ael.ebankingback.entities.BankAccount;
import org.ael.ebankingback.exceptions.BankAccountNotFoundException;
import org.ael.ebankingback.mappers.BankAccountMapperImpl;
import org.ael.ebankingback.repositories.AccountOperationRepository;
import org.ael.ebankingback.repositories.BankAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class OperationAccountServiceImpl implements OperationAccountService{

    private final AccountOperationRepository accountOperationRepository;
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapperImpl dto;

    public OperationAccountServiceImpl(AccountOperationRepository accountOperationRepository, BankAccountRepository bankAccountRepository, BankAccountMapperImpl dto) {
        this.accountOperationRepository = accountOperationRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.dto = dto;
    }

    @Override
    public List<AccountOperationDTO> accountOperationHistory(String accountId){
        List<AccountOperation> bankAccountOperations = accountOperationRepository.findByBankAccountId(accountId);
        //bankAccountOperations.stream().map(accountOperation -> dto.fromAccountOperation(accountOperation)).collect(Collectors.toList());
        return bankAccountOperations.stream().map(dto::fromAccountOperation).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountOperationHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount == null) throw new BankAccountNotFoundException("Account Not Found");
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        //accountOperations.stream().map(accountOperation -> dto.fromAccountOperation(accountOperation)).collect(Collectors.toList());
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.stream().map(dto::fromAccountOperation).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());

        return accountHistoryDTO;
    }

}
