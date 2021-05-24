package com.drozdovas.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.drozdovas.banking.model.request.AccountStatementRequest;
import com.drozdovas.banking.model.request.TransferBalanceRequest;
import com.drozdovas.banking.dto.mapper.AccountMapper;
import com.drozdovas.banking.dto.mapper.TransactionMapper;
import com.drozdovas.banking.dto.model.AccountDto;
import com.drozdovas.banking.dto.model.AccountStatement;
import com.drozdovas.banking.model.Account;
import com.drozdovas.banking.model.Transaction;
import com.drozdovas.banking.service.impl.AccountServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Arrays;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
@WebAppConfiguration
public class AccountControllerTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountServiceImpl accountService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).dispatchOptions(true).build();
    }

    @Test
    public void givenWebApplicationContext_whenServletContext_thenItProvidesAccountController() {
        final ServletContext servletContext = webApplicationContext.getServletContext();
        Assert.assertNotNull(servletContext);
        Assert.assertTrue(servletContext instanceof MockServletContext);
        Assert.assertNotNull(webApplicationContext.getBean("accountController"));
    }

    @Test
    public void givenGetAllAccountURI_whenMockMVC_thenVerifyResponse() throws Exception {
        Account account = Account.builder()
                .accountNumber("1")
                .currentBalance(new BigDecimal(1000))
                .build();

        doReturn(Arrays.asList(AccountMapper.toAccountDto(account))).when(accountService).findAll();

        mockMvc.perform(get("/api/accounts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"OK\",\"payload\":[{\"accountNumber\":\"1\",\"currentBalance\":1000,\"accountName\":null}]}"));
    }

    @Test
    public void createAccountCheck() throws Exception {
        Account accountFirst = Account.builder()
                .accountNumber("1")
                .currentBalance(new BigDecimal(1000))
                .build();

        doReturn(AccountMapper.toAccountDto(accountFirst)).when(accountService).save(AccountMapper.toAccountDto(accountFirst));
        doReturn(Arrays.asList(AccountMapper.toAccountDto(accountFirst))).when(accountService).findAll();

        mockMvc.perform(
                post("/api/accounts")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(
                                asJsonString(
                                        AccountDto.builder()
                                                .accountNumber("1")
                                                .currentBalance(new BigDecimal(1000))
                                                .build()
                                )
                        )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"OK\",\"payload\":{\"accountNumber\":\"1\",\"currentBalance\":1000,\"accountName\":null}}"));
    }

    @Test
    public void sendMoneyCheck() throws Exception{
        Account fromAccount = Account.builder()
                .accountNumber("1")
                .currentBalance(new BigDecimal(1000))
                .build();

        Account toAccount = Account.builder()
                .accountNumber("2")
                .currentBalance(new BigDecimal(2000))
                .build();

        doReturn(AccountMapper.toAccountDto(fromAccount)).when(accountService).save(AccountMapper.toAccountDto(fromAccount));
        doReturn(fromAccount).when(accountService).findByAccountNumber(fromAccount.getAccountNumber());

        doReturn(AccountMapper.toAccountDto(toAccount)).when(accountService).save(AccountMapper.toAccountDto(toAccount));
        doReturn(toAccount).when(accountService).findByAccountNumber(toAccount.getAccountNumber());

        TransferBalanceRequest transferBalanceRequest = new TransferBalanceRequest(
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                new BigDecimal(10)
        );

        Transaction withdrawTransaction = Transaction.builder()
                .account(fromAccount)
                .transactionAmount(transferBalanceRequest.getAmount())
                .transactionDateTime(new Timestamp(System.currentTimeMillis()))
                .description("Credited to account no " + transferBalanceRequest.getToAccountNumber())
                .build();

        doReturn(TransactionMapper.toTransactionDto(withdrawTransaction)).when(accountService).sendMoney(transferBalanceRequest);

        mockMvc.perform(
                post("/api/accounts/send-money")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(
                                asJsonString(transferBalanceRequest)
                        ))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getStatementCheck() throws Exception{
        Account account = Account.builder()
                .accountNumber("1")
                .currentBalance(new BigDecimal(1000))
                .build();

        doReturn(AccountMapper.toAccountDto(account)).when(accountService).save(AccountMapper.toAccountDto(account));
        doReturn(account).when(accountService).findByAccountNumber(account.getAccountNumber());
        doReturn(new AccountStatement(account.getCurrentBalance(), null)).when(accountService).getStatement(account.getAccountNumber());

        mockMvc.perform(
                post("/api/accounts/statement")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(
                                asJsonString(
                                        AccountStatementRequest.builder()
                                        .accountNumber(account.getAccountNumber())
                                        .build()
                                )
                        ))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"OK\",\"payload\":{\"currentBalance\":1000,\"transactionHistory\":null}}"));
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
            ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
