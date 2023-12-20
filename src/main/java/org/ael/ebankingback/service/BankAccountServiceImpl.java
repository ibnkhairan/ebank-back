package org.ael.ebankingback.service;

import lombok.extern.slf4j.Slf4j;
import org.ael.ebankingback.dto.BankAccountDTO;
import org.ael.ebankingback.dto.CurrentBankAccountDTO;
import org.ael.ebankingback.dto.CustomerDTO;
import org.ael.ebankingback.dto.SavingBankAccountDTO;
import org.ael.ebankingback.entities.*;
import org.ael.ebankingback.enums.OperationType;
import org.ael.ebankingback.exceptions.BalanceNotSufficientException;
import org.ael.ebankingback.exceptions.BankAccountNotFoundException;
import org.ael.ebankingback.exceptions.CustomerNotFoundException;
import org.ael.ebankingback.mappers.BankAccountMapperImpl;
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
    private BankAccountMapperImpl dtoMapper;

    //Logger log = LoggerFactory.getLogger(this.getClass().getName()); ==> @Slf4j

    public BankAccountServiceImpl(CustomerRepository customerRepository, BankAccountRepository bankAccountRepository, AccountOperationRepository accountOperationRepository, BankAccountMapperImpl dtoMapper) {
        this.customerRepository = customerRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.accountOperationRepository = accountOperationRepository;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {

        log.info("sauvegarde du nouveau customer");
        Customer customer=dtoMapper.fromCustomerDTO(customerDTO);
        Customer customer1 = customerRepository.save(customer);
        return dtoMapper.fromCustomer(customer1);
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

        return dtoMapper.fromCurrentBankAccount(currentAccount1);
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

        return dtoMapper.fromSavingBankAccount(savingAccount1);
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        log.info("Recuperer la liste des clients");
        List<Customer> customerList = customerRepository.findAll();
        List<CustomerDTO> customerDTOList = customerList.stream()
                                                        .map(customer -> dtoMapper.fromCustomer(customer))
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
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        log.info("Récupération du compte:"+accountId);
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException(String.format("compte %s not found",accountId)));

        if (bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        }else{
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }


    }

    private BankAccount findBankAccountById(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException(String.format("compte %s not found")));

        return bankAccount;
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = findBankAccountById(accountId);

        if(bankAccount.getBalance() < amount)
            throw new BalanceNotSufficientException("Solde insuffisant");

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);

    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = findBankAccountById(accountId);
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
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
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }

        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("customer not found"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {

        log.info("Mise à jour du customer");
        Customer customer=dtoMapper.fromCustomerDTO(customerDTO);
        Customer customer1 = customerRepository.save(customer);
        return dtoMapper.fromCustomer(customer1);
    }

    @Override
    public void deleteCustomer(Long customerId){
        customerRepository.deleteById(customerId);
    }
}
