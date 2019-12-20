package io.karmanov.mts.transaction.dto;

import java.math.BigDecimal;
import java.util.StringJoiner;
import java.util.UUID;

public class TransactionRequest {

    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;

    public UUID getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(UUID fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public UUID getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(UUID toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TransactionRequest.class.getSimpleName() + "[", "]")
                .add("fromAccountId=" + fromAccountId)
                .add("toAccountId=" + toAccountId)
                .add("amount=" + amount)
                .toString();
    }
}
