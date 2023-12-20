package org.ael.ebankingback.service;

import lombok.extern.slf4j.Slf4j;
import org.ael.ebankingback.dto.BankAccountDTO;
import org.ael.ebankingback.dto.CurrentBankAccountDTO;
import org.ael.ebankingback.dto.SavingBankAccountDTO;
import org.ael.ebankingback.entities.*;
import org.ael.ebankingback.enums.OperationType;
import org.ael.ebankingback.exceptions.BalanceNotSufficientException;
import org.ael.ebankingback.exceptions.BankAccountNotFoundException;
import org.ael.ebankingback.exceptions.CustomerNotFoundException;
import org.ael.ebankingback.mappers.BankAccountMapper;
import org.ael.ebankingback.repositories.AccountOperationRepository;
import org.ael.ebankingback.repositories.BankAccountRepository;
import org.ael.ebankingback.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapper bankAccountMapper;

    //Logger log = LoggerFactory.getLogger(this.getClass().getName()); ==> @Slf4j

    public BankAccountServiceImpl(CustomerRepository customerRepository, BankAccountRepository bankAccountRepository, AccountOperationRepository accountOperationRepository, BankAccountMapper bankAccountMapper) {
        this.customerRepository = customerRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.accountOperationRepository = accountOperationRepository;
        this.bankAccountMapper = bankAccountMapper;
    }


    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        log.info("sauvegarde du nouveau compte courant");
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer == null){
            throw new CustomerNotFoundException("Customer not found");
        }
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);
        CurrentAccount currentAccount1 = bankAccountRepository.save(currentAccount);

        return bankAccountMapper.fromCurrentBankAccount(currentAccount1);
    }

    @Override
    public SavingBankAccountDTO saveSavingtBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        log.info("sauvegarde du nouveau compte épargne");
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer == null){
            throw new CustomerNotFoundException("Customer not found");
        }
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);
        SavingAccount savingAccount1 = bankAccountRepository.save(savingAccount);

        return bankAccountMapper.fromSavingBankAccount(savingAccount1);
    }



    private BankAccountDTO getBank(BankAccount bankAccount){
        if (bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return bankAccountMapper.fromSavingBankAccount(savingAccount);
        } else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return bankAccountMapper.fromCurrentBankAccount(currentAccount);
        }
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        log.info("Récupération du compte:"+accountId);
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException(String.format("compte %s not found",accountId)));

        return getBank(bankAccount);
    }

    private BankAccount findBankAccountById(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException(String.format("compte %s not found")));

        return bankAccount;
    }

    private void operationEffectue(BankAccount bankAccount,double amount, String description,OperationType operationType){

        double balance =0;
        if(operationType.equals(OperationType.DEBIT))
            balance = bankAccount.getBalance()-amount;
        else
            balance = bankAccount.getBalance()+amount;

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(operationType);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(balance);
        bankAccountRepository.save(bankAccount);
    }


    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = findBankAccountById(accountId);

        if(bankAccount.getBalance() < amount)
            throw new BalanceNotSufficientException("Solde insuffisant");

        operationEffectue(bankAccount,amount,description,OperationType.DEBIT);

    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = findBankAccountById(accountId);

        operationEffectue(bankAccount,amount,description,OperationType.CREDIT);
    }

    @Override
    public void transfert(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource,amount,"transfert to "+accountIdDestination);
        credit(accountIdDestination,amount,"transfert from "+accountIdSource);
    }

    @Override
    public List<BankAccountDTO> bankAccountList() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            return getBank(bankAccount);

        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }


}
