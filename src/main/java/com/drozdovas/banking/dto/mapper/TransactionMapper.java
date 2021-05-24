package com.drozdovas.banking.dto.mapper;

import com.drozdovas.banking.dto.model.TransactionDto;
import com.drozdovas.banking.model.Transaction;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionMapper {
    public static TransactionDto toTransactionDto(Transaction transaction){
        return TransactionDto.builder()
                .transactionId(transaction.getTransactionId())
                .description(transaction.getDescription())
                .transactionAmount(transaction.getTransactionAmount())
                .transactionDateTime(transaction.getTransactionDateTime())
                .senderAccountNumber(transaction.getSenderAccountNumber())
                .receiverAccountNumber(transaction.getReceiverAccountNumber())
                .direction(transaction.getDirection())
                .build();
    }

    public static List<TransactionDto> toTransactionDtoList(List<Transaction> transactionList){
        return transactionList.stream()
                .map(transaction -> toTransactionDto(transaction))
                .collect(Collectors.toList());
    }

}
