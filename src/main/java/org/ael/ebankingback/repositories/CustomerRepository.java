package org.ael.ebankingback.repositories;

import org.ael.ebankingback.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    @Query("select c from Customer c where c.name like %:x% ")
   // @Query("select c from Customer c where c.name like :x ")
    List<Customer>searchCustomers(@Param("x") String keyword);
}
