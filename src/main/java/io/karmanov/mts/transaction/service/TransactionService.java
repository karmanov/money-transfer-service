package io.karmanov.mts.transaction.service;

import io.karmanov.mts.account.model.Account;
import io.karmanov.mts.account.service.AccountService;
import io.karmanov.mts.transaction.dao.TransactionRepository;
import io.karmanov.mts.transaction.dto.TransactionRequest;
import io.karmanov.mts.transaction.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static io.karmanov.mts.validation.ValidationUtils.validateAll;

@ApplicationScoped
public class TransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionService.class);

    final AccountService accountService;
    final TransactionRepository transactionRepository;

    public TransactionService(AccountService accountService, TransactionRepository transactionRepository) {
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction executeTransaction(TransactionRequest transactionRequest, Long delay) {
        log.info(">> executeTransaction({})", transactionRequest);
        Account fromAccount;
        Account toAccount;
        if (transactionRequest.getFromAccountId().compareTo(transactionRequest.getToAccountId()) > 0) {
            fromAccount = accountService.findByIdForUpdate(transactionRequest.getFromAccountId());
            toAccount = accountService.findByIdForUpdate(transactionRequest.getToAccountId());
        } else {
            toAccount = accountService.findByIdForUpdate(transactionRequest.getToAccountId());
            fromAccount = accountService.findByIdForUpdate(transactionRequest.getFromAccountId());
        }

        validateAll(fromAccount, toAccount, transactionRequest.getAmount());

        delay(delay);

        accountService.debit(toAccount, transactionRequest.getAmount());
        accountService.credit(fromAccount, transactionRequest.getAmount());
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount.getId());
        transaction.setToAccount(toAccount.getId());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setAmount(transactionRequest.getAmount());
        transactionRepository.persist(transaction);
        log.info("<< executeTransaction()");
        return transaction;
    }

    public List<Transaction> listAll() {
        log.debug(">> listAll()");
        List<Transaction> transactions = transactionRepository.listAll();
        log.debug("<< listAll()");
        return transactions;
    }

    /**
     * Simulates delays in the transaction execution
     *
     * @param delay number of seconds to waite before execution
     */
    private void delay(Long delay) {
        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException e) {
            log.error("-- delay()", e);
        }
    }
}
