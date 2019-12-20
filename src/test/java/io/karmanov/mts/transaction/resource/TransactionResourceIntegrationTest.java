package io.karmanov.mts.transaction.resource;

import io.karmanov.mts.transaction.dto.TransactionRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class TransactionResourceIntegrationTest {

    private static final String FROM_ACCOUNT_ID = "9e4bd34d-7af1-456d-a4ed-77f5659fc54b";
    private static final String TO_ACCOUNT_ID = "e68fee19-1d7a-419d-bfc7-5dedf572e33b";


    @Test
    public void transaction_list_all() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setFromAccountId(UUID.fromString(FROM_ACCOUNT_ID));
        transactionRequest.setToAccountId(UUID.fromString(TO_ACCOUNT_ID));
        transactionRequest.setAmount(BigDecimal.ONE);

        //@formatter:off
        given()
                .contentType(ContentType.JSON)
                .body(transactionRequest)
                .when()
                .post("/api/v1/transactions")
                .then()
                .statusCode(201);
        //@formatter:off


        //@formatter:off
        given()
                .when()
                .get("/api/v1/transactions")
                .then()
                .statusCode(200)
                .body("size()", is(1));
        //@formatter:off
    }

    @Test
    public void make_transaction_ok() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setFromAccountId(UUID.fromString(FROM_ACCOUNT_ID));
        transactionRequest.setToAccountId(UUID.fromString(TO_ACCOUNT_ID));
        transactionRequest.setAmount(BigDecimal.ONE);

        //@formatter:off
        given()
                .contentType(ContentType.JSON)
                .body(transactionRequest)
                .when()
                .post("/api/v1/transactions")
                .then()
                .statusCode(201);
        //@formatter:off
    }

    @Test
    public void make_transaction_insufficient_funds() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setFromAccountId(UUID.fromString(FROM_ACCOUNT_ID));
        transactionRequest.setToAccountId(UUID.fromString(TO_ACCOUNT_ID));
        transactionRequest.setAmount(BigDecimal.valueOf(10000));

        //@formatter:off
        given()
                .contentType(ContentType.JSON)
                .body(transactionRequest)
                .when()
                .post("/api/v1/transactions")
                .then()
                .statusCode(400);
        //@formatter:off
    }

    @Test
    public void make_transaction_same_account() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setFromAccountId(UUID.fromString(FROM_ACCOUNT_ID));
        transactionRequest.setToAccountId(UUID.fromString(FROM_ACCOUNT_ID));
        transactionRequest.setAmount(BigDecimal.ONE);

        //@formatter:off
        given()
                .contentType(ContentType.JSON)
                .body(transactionRequest)
                .when()
                .post("/api/v1/transactions")
                .then()
                .statusCode(400);
        //@formatter:off
    }

    @Test
    public void make_transaction_zero() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setFromAccountId(UUID.fromString(FROM_ACCOUNT_ID));
        transactionRequest.setToAccountId(UUID.fromString(TO_ACCOUNT_ID));
        transactionRequest.setAmount(BigDecimal.ZERO);

        //@formatter:off
        given()
                .contentType(ContentType.JSON)
                .body(transactionRequest)
                .when()
                .post("/api/v1/transactions")
                .then()
                .statusCode(400);
        //@formatter:off
    }

    @Test
    public void make_transaction_negative() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setFromAccountId(UUID.fromString(FROM_ACCOUNT_ID));
        transactionRequest.setToAccountId(UUID.fromString(TO_ACCOUNT_ID));
        transactionRequest.setAmount(BigDecimal.valueOf(-1));

        //@formatter:off
        given()
                .contentType(ContentType.JSON)
                .body(transactionRequest)
                .when()
                .post("/api/v1/transactions")
                .then()
                .statusCode(400);
        //@formatter:off
    }

}