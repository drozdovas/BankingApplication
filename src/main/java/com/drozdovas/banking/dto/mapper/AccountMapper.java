package com.drozdovas.banking.dto.mapper;

import com.drozdovas.banking.dto.model.AccountDto;
import com.drozdovas.banking.model.Account;

import java.util.List;
import java.util.stream.Collectors;

public class AccountMapper {
    public static AccountDto toAccountDto(Account account){
        return AccountDto.builder()
                .accountNumber(account.getAccountNumber())
                .accountName(account.getAccountName())
                .currentBalance(account.getCurrentBalance())
                .build();
    }
    public static List<AccountDto> toAccountDtoList(List<Account> accountList){
        return accountList.stream()
                .map(account -> toAccountDto(account))
                .collect(Collectors.toList());
    }

    public static Account toAccount(AccountDto accountDto){
        return Account.builder()
                .accountNumber(accountDto.getAccountNumber())
                .accountName(accountDto.getAccountName())
                .currentBalance(accountDto.getCurrentBalance())
                .build();
    }
}