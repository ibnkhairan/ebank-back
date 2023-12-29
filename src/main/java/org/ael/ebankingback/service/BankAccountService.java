package org.ael.ebankingback.service;

import org.ael.ebankingback.dto.BankAccountDTO;
import org.ael.ebankingback.dto.CurrentBankAccountDTO;
import org.ael.ebankingback.dto.CustomerDTO;
import org.ael.ebankingback.dto.SavingBankAccountDTO;
import org.ael.ebankingback.exceptions.BalanceNotSufficientException;
import org.ael.ebankingback.exceptions.BankAccountEmptyException;
import org.ael.ebankingback.exceptions.BankAccountNotFoundException;
import org.ael.ebankingback.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {

    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingtBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;

    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId,double amount,String description) throws BankAccountNotFoundException, BalanceNotSufficientException, BankAccountEmptyException;
    void credit(String accountId,double amount,String description) throws BankAccountNotFoundException;
    void transfert(String accountIdSource,String accountIdDestination,double amount) throws BankAccountNotFoundException, BalanceNotSufficientException, BankAccountEmptyException;
    List<BankAccountDTO> bankAccountList();


}
