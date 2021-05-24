package com.drozdovas.banking.model;

import com.drozdovas.banking.constants.Direction;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue
    private Long id;

    private Long transactionId;

    private BigDecimal transactionAmount;

    private String description;

    private Timestamp transactionDateTime;

    private String senderAccountNumber;

    private String receiverAccountNumber;

    private Direction direction;

    @ManyToOne
    @JoinColumn(name = "accountNumber")
    private Account account;
}
