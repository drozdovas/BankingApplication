package com.drozdovas.banking.service;

import com.drozdovas.banking.model.request.TransferBalanceRequest;
import com.drozdovas.banking.dto.model.AccountDto;
import com.drozdovas.banking.dto.model.AccountStatement;
import com.drozdovas.banking.dto.model.TransactionDto;
import com.drozdovas.banking.model.Account;
import com.drozdovas.banking.exception.BankTransactionException;

import java.util.List;

public interface AccountService {

    List<AccountDto> findAll();

    Account findByAccountNumber(String accountNumber);

    AccountDto save(AccountDto accountDto)throws BankTransactionException;

    TransactionDto sendMoney(TransferBalanceRequest transferBalanceRequest) throws BankTransactionException;

    AccountStatement getStatement(String accountNumber)throws BankTransactionException;
}
