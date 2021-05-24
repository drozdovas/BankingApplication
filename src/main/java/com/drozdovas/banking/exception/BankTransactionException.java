package com.drozdovas.banking.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankTransactionException extends Exception{

    private static final Logger LOGGER = LoggerFactory.getLogger(BankTransactionException.class);

    public BankTransactionException(String message) {
        super(message);

        LOGGER.error(message);
    }
}
