## Project Description
A Java Banking Application that can be used for:
- Sending money between two predefined accounts with a positive starting balance
- Requesting account balance and list of transactions


## Technology Used
Spring Boot, Maven, JPA, Hibernate and HSQLDB Database

## Project Setup
- Requirement
    - JDK 1.8 or higher
    - Maven
  

Use the run configuration provided by any IDE and run/debug the app from there for development purposes. 

Endpoints:

- http://localhost:8080/api/accounts/send-money (HTTP:POST)
- http://localhost:8080/api/accounts/statement (HTTP:POST)
- http://localhost:8080/api/accounts (HTTP:POST)
- http://localhost:8080/api/accounts (HTTP:GET)

## Unit Testing
The unit test can be found on the following directory
```
src/test/java/com/drozdovas/banking/service/impl/
```

## Improvements
- Create separate controllers and test cases for Statements and Transactions
