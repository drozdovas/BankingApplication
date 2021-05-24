package com.drozdovas.banking.model.request;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountStatementRequest {

    private String accountNumber;

}
