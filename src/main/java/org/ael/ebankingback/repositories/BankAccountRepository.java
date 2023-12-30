package org.ael.ebankingback.repositories;

import org.ael.ebankingback.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {

    List<BankAccount>findBankAccountByCustomerId(final Long customerId);
}
