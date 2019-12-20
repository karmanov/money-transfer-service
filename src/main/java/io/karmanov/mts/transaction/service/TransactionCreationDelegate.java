package io.karmanov.mts.transaction.service;

import io.karmanov.mts.transaction.dto.TransactionRequest;
import io.karmanov.mts.transaction.model.Transaction;
import org.eclipse.microprofile.faulttolerance.Retry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PessimisticLockException;
import java.time.temporal.ChronoUnit;

@ApplicationScoped
public class TransactionCreationDelegate {

    private final TransactionService transactionService;

    @Inject
    public TransactionCreationDelegate(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Retry(maxRetries = 10, delayUnit = ChronoUnit.SECONDS, delay = 2, retryOn = PessimisticLockException.class)
    public Transaction executeTransaction(TransactionRequest transactionRequest, Long delay) {
        return transactionService.executeTransaction(transactionRequest, delay);
    }
}
