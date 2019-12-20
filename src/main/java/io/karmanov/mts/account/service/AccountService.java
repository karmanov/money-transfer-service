package io.karmanov.mts.account.service;

import io.karmanov.mts.account.dao.AccountRepository;
import io.karmanov.mts.account.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static io.karmanov.mts.validation.ValidationUtils.validateBalance;

@ApplicationScoped
public class AccountService {

    private final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account findByIdForUpdate(UUID id) {
        log.debug(">> findByIdForUpdate({})", id);
        Account account = accountRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Account with id " + id + " not found"));
        log.debug("<< findByIdForUpdate()");
        return account;
    }

    public Account findById(UUID id) {
        log.debug(">> findById({})", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account with id " + id + " not found"));
        log.debug("<< findById()");
        return account;
    }

    public Account debit(Account account, BigDecimal amount) {
        return updateAccountBalance(account, amount);
    }

    public Account credit(Account account, BigDecimal amount) {
        validateBalance(account, amount);
        return updateAccountBalance(account, amount.negate());
    }

    public List<Account> listAll() {
        log.debug(">> listAll()");
        List<Account> accounts = accountRepository.listAll();
        log.debug("<< listAll()");
        return accounts;
    }

    private Account updateAccountBalance(Account account, BigDecimal amount) {
        BigDecimal balance = account.getBalance();
        BigDecimal newBalance = balance.add(amount);
        account.setBalance(newBalance);
        accountRepository.persist(account);
        return account;
    }


}
