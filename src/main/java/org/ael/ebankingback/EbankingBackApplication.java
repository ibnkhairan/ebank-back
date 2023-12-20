package org.ael.ebankingback;

import org.ael.ebankingback.dto.BankAccountDTO;
import org.ael.ebankingback.dto.CustomerDTO;
import org.ael.ebankingback.entities.*;
import org.ael.ebankingback.enums.AccountStatus;
import org.ael.ebankingback.enums.OperationType;
import org.ael.ebankingback.exceptions.BalanceNotSufficientException;
import org.ael.ebankingback.exceptions.BankAccountNotFoundException;
import org.ael.ebankingback.exceptions.CustomerNotFoundException;
import org.ael.ebankingback.repositories.AccountOperationRepository;
import org.ael.ebankingback.repositories.BankAccountRepository;
import org.ael.ebankingback.repositories.CustomerRepository;
import org.ael.ebankingback.service.BankAccountService;
import org.ael.ebankingback.service.CustomerAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackApplication {

    public static void main(String[] args) {

        SpringApplication.run(EbankingBackApplication.class, args);
    }


    //@Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService, CustomerAccountService customerAccountService){
        return args -> {
                Stream.of("Hassan","Mohamed","Imane").forEach(name->{
                    CustomerDTO customer = new CustomerDTO();
                    customer.setName(name);
                    customer.setEmail(name+"@gmail.com");

                    customerAccountService.saveCustomer(customer);
                });
            customerAccountService.listCustomers().forEach(customer -> {
                    try {
                        bankAccountService.saveCurrentBankAccount(Math.random()*9000,9000, customer.getId());
                        bankAccountService.saveSavingtBankAccount(Math.random()*120000,5.5, customer.getId());


                    } catch (CustomerNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
            for(BankAccountDTO bankAccount : bankAccounts){
                for(int i=0;i<10;i++){
                    bankAccountService.credit(bankAccount.getId(), 10000+Math.random()*120000,"Crédit");
                    bankAccountService.debit(bankAccount.getId(), 1000+Math.random()*9000,"Débit");
                }
            }
        };
    }

    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository){
        return args -> {
            Stream.of("Hassan","Yassine","Aicha").forEach(name->{
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                customerRepository.save(customer);
            });
            customerRepository.findAll().forEach(cust ->
            {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*90000);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(cust);
                currentAccount.setOverDraft(9000);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random()*90000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(cust);
                savingAccount.setInterestRate(5.5);
                bankAccountRepository.save(savingAccount);
            });

            bankAccountRepository.findAll().forEach(bankAccount -> {
                for(int i=0;i<10;i++){
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random()*12000);
                    accountOperation.setType(Math.random()>0.5 ? OperationType.DEBIT: OperationType.CREDIT);
                    accountOperation.setBankAccount(bankAccount);
                    accountOperationRepository.save(accountOperation);
                }
            });
            bankAccountRepository.findAll().forEach(bankAccount -> {
                System.out.println("****************************************");
                System.out.println(bankAccount.getId());
                System.out.println(bankAccount.getBalance());
                System.out.println(bankAccount.getStatus());
                System.out.println(bankAccount.getCreatedAt());
                System.out.println(bankAccount.getCustomer().getName());
                System.out.println(bankAccount.getCustomer().getEmail());
                System.out.println(bankAccount.getClass().getSimpleName());
                if(bankAccount instanceof CurrentAccount){
                    System.out.println("Over Draft => "+((CurrentAccount) bankAccount).getOverDraft());
                }else if(bankAccount instanceof SavingAccount){
                    System.out.println("Rate => "+((SavingAccount) bankAccount).getInterestRate());
                }

            /*    bankAccount.getAccountOperations().forEach(accountOperation -> {
                    System.out.println(accountOperation.getType()+"\t"+accountOperation.getOperationDate()+"\t"+accountOperation.getAmount());
                });*/
                System.out.println("********************************************");
            });
        };
    }
}
