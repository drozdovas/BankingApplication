package com.drozdovas.banking.dto.model;

import com.drozdovas.banking.constants.Direction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
public class TransactionDto {

    private Long transactionId;

    private String description;

    private String senderAccountNumber;

    private String receiverAccountNumber;

    private BigDecimal transactionAmount;

    private Timestamp transactionDateTime;

    private Direction direction;
}
