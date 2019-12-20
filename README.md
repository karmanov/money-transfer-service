# Money Transfer Service

## Description
Service implementation providing REST API for creating money transfers between accounts

## Technology Stack
* Java 8
* H2
* JPA
* Quarkus
* Maven
* Docker

## Implementation Details
I was mainly focused on solving the problem of the concurrent modifications of the account balance.
I considered 4 possible solutions of the problem:
* Build application architecture based on events. This solution could be build based ordered queues, where messages that belong to the same group are delivered sequentially.
This is a good solution for cases when the event mechanism and infrastructure is in your control, but could be an overkill for test assigment.
* Application level transactions. I didn't use this solution because it's easy to get the deadlock.
* Control concurrency on application level. Wrap the code in synchronized block or use Reentrantlock etc. I didn't use this solution because it's not scaling horizontally.
* Locking on the database level. I selected this approach, and Pessimistic Locking to be more precise. 
With this approach, application lock accounts records in the database that participate in transaction. And if other thread or application will try to lock the same account records, 
PessimisticLockException will be thrown. Also there is a re-try mechanism implemented. 
In case if PessimisticLockException will be thrown, application will re-try to process same transaction request 10 times with 2 seconds timeout.
The pitfall of pessimistic locking is that it is vulnerable to DB locks. If the transaction, which has acquired the lock fails to release it, it can cause DB locks.
DB locks can be handled by adding timeouts when acquiring locks. If the acquired lock is not released in the specified time, it times out and the lock is released.
Also to avoid deadlocks application always will try to lock accounts in the same order.

## Things to improve:
* Validation. Currently following validations are checking for new transfer request:
   * It's not allowed to transfer negative amount;
   * It's not allowed to transfer zero amount;
   * It's not allowed to transfer null as account id or amount;
   * It's not allowed to transfer from and to same account; <br>
But if transaction request breaks few validations only first one will be shown to the client.
Need to implement logic to aggregate constrains.

* Move to Java 11
* Improve test coverage. Current coverage is 78%. Coverage report could be generated with following command:
```./mvnw clean verify```. Report will be located under ```target/site/jacoco/index.html```

* Improve API error responses
* Add currencies support
 

## Packaging and run the application
The application is packaged using ```./mvnw package ```. It produces executable jar file in ``` /target ```. The jar file name:  ```money-transfer-service-1.0-SNAPSHOT-runner.jar```
You can run the application using: ```java -jar target/money-transfer-service-1.0-SNAPSHOT-runner.jar``` 

### Docker
Dockerfile is used in order to build a container that runs the money transfer application.
Before building the docker image run: ```./mvnw package```. Then, build the image with:

```docker build -f src/main/docker/Dockerfile.jvm -t io.karmanov/money-transfer-service-jvm .```

Then run the container using:
```docker run -i --rm -p 8080:8080 io.karmanov/money-transfer-service-jvm```


## Service API

### Get Accounts
* **Description:** Retrieve accounts from the database 
* **URL**
`http://localhost:8080/api/v1/accounts`
* **Method** `GET`

#### **Success Response**
```$xslt
[
    {
        "id": "5282d152-4a82-4fe7-b278-3da0ce4a6650",
        "balance": 0.00
    },
    {
        "id": "e68fee19-1d7a-419d-bfc7-5dedf572e33b",
        "balance": 100.00
    },
    {
        "id": "9e4bd34d-7af1-456d-a4ed-77f5659fc54b",
        "balance": 200.00
    }
]
```

#### **Error Responses**
* **Condition:** If any server errors occurred (e.g. database connectivity issue). <br>
* **Code** `500 INTERNAL SERVER ERROR`

#### **Example**
```
curl -X GET http://localhost:8080/api/v1/accounts
```

### Get Account by ID
* **Description:** Retrieve account by ID from the database 
* **URL**
`http://localhost:8080/api/v1/accounts/{id}`
* **Method** `GET`

#### **Success Response**
```$xslt
{
    "id": "5282d152-4a82-4fe7-b278-3da0ce4a6650",
    "balance": 0.00
}
```

#### **Error Responses**
* **Condition:** If any server errors occurred (e.g. database connectivity issue). <br>
**Code** `500 INTERNAL SERVER ERROR`

* **Condition:** If account with specified id not found in the database. <br>
**Code** `404 NOT FOUND`

#### **Example**
```
curl -X GET http://localhost:8080/api/v1/accounts/5282d152-4a82-4fe7-b278-3da0ce4a6650
```

### Get Transactions
* **Description:** Retrieve transactions from the database 
* **URL**
`http://localhost:8080/api/v1/transactions`
* **Method** `GET`
#### **Success Response**
```$xslt
[
    {
        "id": "f81b9f73-5b50-4bf7-aa5d-b6b08baec059",
        "fromAccount": "9e4bd34d-7af1-456d-a4ed-77f5659fc54b",
        "toAccount": "5282d152-4a82-4fe7-b278-3da0ce4a6650",
        "amount": 1.00,
        "createdAt": "2019-12-20 13:25:40"
    },
    {
        "id": "e10b6fa4-67a1-47c8-8c6a-bbb876b1d81a",
        "fromAccount": "9e4bd34d-7af1-456d-a4ed-77f5659fc54b",
        "toAccount": "5282d152-4a82-4fe7-b278-3da0ce4a6650",
        "amount": 2.00,
        "createdAt": "2019-12-20 13:25:48"
    }
]
```
#### **Error Responses**
* **Condition:** If any server errors occurred (e.g. database connectivity issue). <br>
**Code** `500 INTERNAL SERVER ERROR`

#### **Example**
```
curl -X GET http://localhost:8080/api/v1/transactions
```

### Create a transactions
* **Description:** Create transaction. The endpoint has optional query param - "delay". This was implemented to simulate delay in transfer processing. 
* **URL**
`http://localhost:8080/api/v1/transactions`
* **Method** `POST`
* **Request body**
```
{
	"fromAccountId": "9e4bd34d-7af1-456d-a4ed-77f5659fc54b",
	"toAccountId": "5282d152-4a82-4fe7-b278-3da0ce4a6650",
	"amount": 2
}
```
#### **Success Response**
* **Condition:** If transaction timestamp is not older then configured shift(default value 60 seconds) <br>
* **Code** `201 CREATED`
#### **Error Responses**
* **Condition:** If any server errors occurred (e.g. database connectivity issue). <br>
**Code** `500 INTERNAL SERVER ERROR`

* **Condition:** If any of the specified accounts not found <br>
**Code** `404 NOT FOUND`

* **Condition:** If any of the request body validations failed <br>
**Code** `400 BAD REQUEST`

#### **Example**
```
curl -X POST \
  http://localhost:8080/api/v1/transactions \
  -H 'Content-Type: application/json' \
  -d '{
	"fromAccountId": "9e4bd34d-7af1-456d-a4ed-77f5659fc54b",
	"toAccountId": "5282d152-4a82-4fe7-b278-3da0ce4a6650",
	"amount": 20
}'

```

### Create a transactions with delay
* **Description:** Create transaction. The endpoint has optional query param - "delay". This was implemented to simulate slow transfer processing.
After locking accounts thread will sleep specified number of seconds. Any other requests where same accounts participate will get PessimisticLockException and will be re-tried.
* **URL**
`http://localhost:8080/api/v1/transactions?delay=10`
* **Method** `POST`
* **Request body**
```
{
	"fromAccountId": "9e4bd34d-7af1-456d-a4ed-77f5659fc54b",
	"toAccountId": "5282d152-4a82-4fe7-b278-3da0ce4a6650",
	"amount": 2
}
```
#### **Success Response**
* **Condition:** If transaction timestamp is not older then configured shift(default value 60 seconds) <br>
* **Code** `201 CREATED`
#### **Error Responses**
* **Condition:** If any server errors occurred (e.g. database connectivity issue). <br>
**Code** `500 INTERNAL SERVER ERROR`

* **Condition:** If any of the specified accounts not found <br>
**Code** `404 NOT FOUND`

* **Condition:** If any of the request body validations failed <br>
**Code** `400 BAD REQUEST`

#### **Example**
```
curl -X POST \
  http://localhost:8080/api/v1/transactions?delay=10 \
  -H 'Content-Type: application/json' \
  -d '{
	"fromAccountId": "9e4bd34d-7af1-456d-a4ed-77f5659fc54b",
	"toAccountId": "5282d152-4a82-4fe7-b278-3da0ce4a6650",
	"amount": 20
}'

```

## Environment
macOS Mojave (version 10.14.6) <br>
OpenJDK Runtime Environment (AdoptOpenJDK)(build 1.8.0_232-b09)