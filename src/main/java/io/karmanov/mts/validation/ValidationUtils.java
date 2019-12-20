package io.karmanov.mts.validation;

import io.karmanov.mts.account.model.Account;

import javax.ws.rs.BadRequestException;
import java.math.BigDecimal;

public class ValidationUtils {

    public static void validateAll(Account from, Account to, BigDecimal amount) {
        validateAmount(amount);
        validateAccounts(from, to);
        validateBalance(from, amount);
    }

    public static void validateBalance(Account from, BigDecimal amount) {
        int comparingResult = from.getBalance().compareTo(amount);
        if (comparingResult == -1) {
            throw new BadRequestException("Insufficient funds");
        }
    }

    private static void validateAccounts(Account from, Account to) {
        if (from.getId().equals(to.getId())) {
            throw new BadRequestException("From and to accounts should not be the same");
        }
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) == 0 || amount.compareTo(BigDecimal.ZERO) == -1) {
            throw new BadRequestException("Invalid amount");
        }
    }


}
