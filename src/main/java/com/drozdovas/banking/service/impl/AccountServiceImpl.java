package com.drozdovas.banking.service.impl;

import com.drozdovas.banking.model.request.TransferBalanceRequest;
import com.drozdovas.banking.dto.mapper.AccountMapper;
import com.drozdovas.banking.dto.mapper.TransactionMapper;
import com.drozdovas.banking.dto.model.AccountDto;
import com.drozdovas.banking.dto.model.AccountStatement;
import com.drozdovas.banking.dto.model.TransactionDto;

import com.drozdovas.banking.model.Account;
import com.drozdovas.banking.constants.Direction;
import com.drozdovas.banking.model.Transaction;
import com.drozdovas.banking.exception.BankTransactionException;
import com.drozdovas.banking.repository.AccountRepository;
import com.drozdovas.banking.repository.TransactionRepository;
import com.drozdovas.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public AccountDto save(AccountDto accountDto) throws BankTransactionException {
        Account account = AccountMapper.toAccount(accountDto);
        checkValidityAndThrowExceptionIfInvalidAccountCreateRequest(account);
        accountRepository.save(account);
        return AccountMapper.toAccountDto(accountRepository.findByAccountNumberEquals(account.getAccountNumber()));
    }

    @Override
    public List<AccountDto> findAll() {
        return AccountMapper.toAccountDtoList(accountRepository.findAll());
    }

    @Override
    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumberEquals(accountNumber);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void withdrawAmount(Account account, BigDecimal amount) throws BankTransactionException {
        BigDecimal newBalance = account.getCurrentBalance().subtract(amount);
        checkValidityAndThrowExceptionIfInsufficientBalance(newBalance, account);
        account.setCurrentBalance(newBalance);
        accountRepository.save(account);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void depositAmount(Account account, BigDecimal amount) {
        BigDecimal newBalance = account.getCurrentBalance().add(amount);
        account.setCurrentBalance(newBalance);
        accountRepository.save(account);
    }

    @Override
    @Transactional(rollbackFor = BankTransactionException.class)
    public TransactionDto sendMoney(TransferBalanceRequest transferBalanceRequest) throws BankTransactionException {
        Account fromAccount = findByAccountNumber(transferBalanceRequest.getFromAccountNumber());
        Account toAccount = findByAccountNumber(transferBalanceRequest.getToAccountNumber());

        synchronized (this) {
            checkValidityAndThrowExceptionIfInvalidSendMoneyRequest(fromAccount,toAccount,transferBalanceRequest);
            withdrawAmount(fromAccount, transferBalanceRequest.getAmount());
            depositAmount(toAccount, transferBalanceRequest.getAmount());
            Transaction transaction = transactBalance(fromAccount,toAccount,transferBalanceRequest);

            return TransactionMapper.toTransactionDto(transaction);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Transaction transactBalance(Account fromAccount,Account toAccount,TransferBalanceRequest transferBalanceRequest) {
        Transaction withdrawTransaction = Transaction.builder()
                .account(fromAccount)
                .transactionAmount(transferBalanceRequest.getAmount())
                .transactionDateTime(new Timestamp(System.currentTimeMillis()))
                .senderAccountNumber(fromAccount.getAccountNumber())
                .receiverAccountNumber(toAccount.getAccountNumber())
                .direction(Direction.OUT)
                .description("Credited to account no " + transferBalanceRequest.getToAccountNumber())
                .build();

        withdrawTransaction = transactionRepository.save(withdrawTransaction);
        withdrawTransaction.setTransactionId(withdrawTransaction.getId());
        transactionRepository.save(withdrawTransaction);

        Transaction depositTransaction = Transaction.builder()
                .account(toAccount)
                .transactionAmount(transferBalanceRequest.getAmount())
                .transactionDateTime(new Timestamp(System.currentTimeMillis()))
                .transactionId(withdrawTransaction.getId())
                .senderAccountNumber(fromAccount.getAccountNumber())
                .receiverAccountNumber(toAccount.getAccountNumber())
                .direction(Direction.IN)
                .description("Credited from account no " + transferBalanceRequest.getFromAccountNumber())
                .build();

        transactionRepository.save(depositTransaction);
        return withdrawTransaction;
    }

    @Override
    public AccountStatement getStatement(String accountNumber) throws BankTransactionException {
        Account account = accountRepository.findByAccountNumberEquals(accountNumber);

        if (account == null) {
            throw new BankTransactionException("Account not found " + accountNumber);
        }

        return new AccountStatement(
                account.getCurrentBalance(),
                account.getTransactionList() == null ? null :
                        TransactionMapper.toTransactionDtoList(account.getTransactionList())
        );
    }

    private void checkValidityAndThrowExceptionIfInvalidAccountCreateRequest(Account account) throws BankTransactionException {
        Account existingAccount = accountRepository.findByAccountNumberEquals(account.getAccountNumber());

        if (existingAccount != null) {
            throw new BankTransactionException("Account already exists.");
        }

        if(account.getCurrentBalance().compareTo(BigDecimal.ZERO) == -1){
            throw new BankTransactionException("Can not create account with a negative balance.");
        }
    }

    private void checkValidityAndThrowExceptionIfInvalidSendMoneyRequest(Account fromAccount, Account toAccount, TransferBalanceRequest transferBalanceRequest) throws BankTransactionException {
        if (!checkIfNegativeTransferAmountProvided(transferBalanceRequest.getAmount())) {
            throw new BankTransactionException("Transfer amount needs to be more than 0.");
        }

        if (checkIfAccountNotExist(fromAccount)) {
            throw new BankTransactionException("From Account Number '" + transferBalanceRequest.getFromAccountNumber() + "' not found.");
        }

        if (checkIfAccountNotExist(toAccount)) {
            throw new BankTransactionException("To Account Number '" + transferBalanceRequest.getToAccountNumber() + "' not found.");
        }

        if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())) {
            throw new BankTransactionException("You Cannot Send Money To Same Account.");
        }
    }

    private Boolean checkIfNegativeTransferAmountProvided(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    private Boolean checkIfAccountNotExist(Account account) {
        if (account == null)
            return true;
        else
            return false;
    }

    public void checkValidityAndThrowExceptionIfInsufficientBalance(BigDecimal newBalance, Account account) throws BankTransactionException {
        if (newBalance.compareTo(BigDecimal.ZERO) == -1) {
            throw new BankTransactionException(
                    "The balance in the account number '" + account.getAccountNumber() +
                     "' is not enough (current balance: " + account.getCurrentBalance() + ")");
        }
    }
}
