package io.karmanov.mts.validation;

import io.karmanov.mts.account.model.Account;
import org.junit.jupiter.api.Test;

import javax.ws.rs.BadRequestException;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationUtilsTest {

    @Test
    void validate_all_success() {
        Account from = buildAccount(UUID.randomUUID(), new BigDecimal(10));
        Account to = buildAccount(UUID.randomUUID(), BigDecimal.ZERO);
        ValidationUtils.validateAll(from, to, new BigDecimal(5));
    }

    @Test
    void validate_insufficient_funds() {
        Account from = buildAccount(UUID.randomUUID(), new BigDecimal(10));
        Account to = buildAccount(UUID.randomUUID(), BigDecimal.ZERO);
        assertThrows(BadRequestException.class, () -> ValidationUtils.validateAll(from, to, new BigDecimal(150)), "Insufficient funds");
    }

    @Test
    void validate_same_account() {
        Account account = buildAccount(UUID.randomUUID(), new BigDecimal(10));
        assertThrows(BadRequestException.class, () -> ValidationUtils.validateAll(account, account, new BigDecimal(150)), "From and to accounts should not be the same");
    }

    @Test
    void validate_transfer_zero() {
        Account from = buildAccount(UUID.randomUUID(), new BigDecimal(10));
        Account to = buildAccount(UUID.randomUUID(), new BigDecimal(50));
        assertThrows(BadRequestException.class, () -> ValidationUtils.validateAll(from, to, BigDecimal.ZERO), "Invalid amount");
    }

    @Test
    void validate_transfer_negative() {
        Account from = buildAccount(UUID.randomUUID(), new BigDecimal(10));
        Account to = buildAccount(UUID.randomUUID(), new BigDecimal(50));
        assertThrows(BadRequestException.class, () -> ValidationUtils.validateAll(from, to, BigDecimal.valueOf(-10)), "Invalid amount");
    }

    private Account buildAccount(UUID id, BigDecimal balance) {
        Account account = new Account();
        account.setId(id);
        account.setBalance(balance);
        return account;
    }
}