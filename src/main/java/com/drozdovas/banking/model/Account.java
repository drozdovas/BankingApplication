package com.drozdovas.banking.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue
    private Long accountId;

    @Column(unique=true)
    String accountNumber;

    BigDecimal currentBalance;

    String accountName;

    @OneToMany(mappedBy = "account")
    private List<Transaction> transactionList;

}
