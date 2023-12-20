package org.ael.ebankingback.repositories;

import org.ael.ebankingback.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {
}
