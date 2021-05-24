package com.drozdovas.banking.dto.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class AccountDto {

    String accountNumber;

    BigDecimal currentBalance;

    String accountName;
}
