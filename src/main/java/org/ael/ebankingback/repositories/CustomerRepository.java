package org.ael.ebankingback.repositories;

import org.ael.ebankingback.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
}
