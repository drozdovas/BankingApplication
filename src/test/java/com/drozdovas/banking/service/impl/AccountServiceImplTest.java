package com.drozdovas.banking.service.impl;

import com.drozdovas.banking.model.request.TransferBalanceRequest;
import com.drozdovas.banking.dto.mapper.AccountMapper;
import com.drozdovas.banking.exception.BankTransactionException;
import com.drozdovas.banking.model.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class AccountServiceImplTest {

    @TestConfiguration
    static class AccountServiceTestContextConfiguration {
        @Bean(name="accountServiceImpl")
        public AccountServiceImpl accountServiceImplTest() {
            return new AccountServiceImpl();

        }
    }

    @Autowired
    private AccountServiceImpl accountService;

    @Test
    public void sendMoneyTest() throws BankTransactionException{
        Account account1 = Account.builder()
                .accountNumber("1")
                .currentBalance(new BigDecimal(1000))
                .build();

        Account account2 = Account.builder()
                .accountNumber("2")
                .currentBalance(new BigDecimal(2000))
                .build();

        accountService.save(AccountMapper.toAccountDto(account1));
        accountService.save(AccountMapper.toAccountDto(account2));

        TransferBalanceRequest transferBalanceRequest =
                new TransferBalanceRequest(
                        account1.getAccountNumber(),
                        account2.getAccountNumber(),
                        new BigDecimal(100)
                );

        accountService.sendMoney(transferBalanceRequest);
        assertThat(
                accountService
                        .findByAccountNumber(account1.getAccountNumber())
                        .getCurrentBalance())
                        .isEqualTo(new BigDecimal(900));
        assertThat(
                accountService
                        .findByAccountNumber(account2.getAccountNumber())
                        .getCurrentBalance())
                        .isEqualTo(new BigDecimal(2100));

    }

    @Test
    public void getStatement() throws BankTransactionException {
        Account account1 = Account.builder()
                .accountNumber("1")
                .currentBalance(new BigDecimal(1000))
                .build();

        Account account2 = Account.builder()
                .accountNumber("2")
                .currentBalance(new BigDecimal(2000))
                .build();

        accountService.save(AccountMapper.toAccountDto(account1));
        accountService.save(AccountMapper.toAccountDto(account2));

        TransferBalanceRequest transferBalanceRequest =
                new TransferBalanceRequest(
                        account1.getAccountNumber(),
                        account2.getAccountNumber(),
                        new BigDecimal(100)
                );

        accountService.sendMoney(transferBalanceRequest);

        assertThat(accountService.getStatement(account1.getAccountNumber())
                .getCurrentBalance())
                .isEqualTo(new BigDecimal(900));

        accountService.sendMoney(transferBalanceRequest);

        assertThat(accountService.getStatement(account1.getAccountNumber())
                .getCurrentBalance()).isEqualTo(new BigDecimal(800));

        assertThat(accountService.getStatement(account2.getAccountNumber())
                .getCurrentBalance()).isEqualTo(new BigDecimal(2200));

    }

}
