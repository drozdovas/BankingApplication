package com.drozdovas.banking.repository;

import com.drozdovas.banking.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByAccountNumberEquals(String accountNumber);

}
