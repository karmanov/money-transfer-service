package io.karmanov.mts.account.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class AccountResourceIntegrationTest {

    @Test
    public void account_list_all() {
        //@formatter:off
        given()
                .when()
                .get("/api/v1/accounts")
                .then()
                .statusCode(200)
                .body("size()", is(3));
        //@formatter:off
    }

    @Test
    public void account_find_by_id() {
        //@formatter:off
        given()
            .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
        .when()
            .get("/api/v1/accounts/9e4bd34d-7af1-456d-a4ed-77f5659fc54b")
        .then()
            .statusCode(200)
            .body("id", is("9e4bd34d-7af1-456d-a4ed-77f5659fc54b"))
            .body("balance", is(new BigDecimal(198).setScale(2)));
        //@formatter:off
    }

    @Test
    public void account_find_by_id_not_found() {
        //@formatter:off
        given()
            .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
        .when()
            .get("/api/v1/accounts/9e4bd34d-7af1-456d-a4ed-77f5659fc54c")
        .then()
            .statusCode(404);
        //@formatter:off
    }
}