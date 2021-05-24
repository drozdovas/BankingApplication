package com.drozdovas.banking.controller;
import com.drozdovas.banking.model.request.AccountStatementRequest;
import com.drozdovas.banking.model.request.TransferBalanceRequest;
import com.drozdovas.banking.dto.model.AccountDto;
import com.drozdovas.banking.dto.response.Response;
import com.drozdovas.banking.exception.BankTransactionException;
import com.drozdovas.banking.service.AccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping()
    public Response<Object> create(@RequestBody AccountDto account) {
        try {
            AccountDto newAccount = accountService.save(account);
            return Response.ok().setPayload(newAccount);
        } catch (BankTransactionException e) {
            return Response.exception().setErrors(e.getMessage());
        }
    }

    @GetMapping()
    public Response getAll() {
        return Response.ok().setPayload(accountService.findAll());
    }

    @PostMapping("/send-money")
    public Response sendMoney(@RequestBody TransferBalanceRequest transferBalanceRequest) {
        try {
            return Response.ok().setPayload(accountService.sendMoney(transferBalanceRequest));
        } catch (BankTransactionException e) {
            return Response.exception().setErrors(e.getMessage());
        }
    }

    @PostMapping("/statement")
    public Response getStatement(@RequestBody AccountStatementRequest accountStatementRequest) {
        try {
            return Response.ok().setPayload(accountService.getStatement(accountStatementRequest.getAccountNumber()));
        } catch (BankTransactionException e) {
            return Response.exception().setErrors(e.getMessage());
        }
    }
}
