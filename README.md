# Code Challenge
To run application:
```bash
gradlew bootRun
``` 
The application runs on port 8080.

## REST API Request

### Deposit money into a specified bank account

POST /api/transaction
```json
{
    "type": "Deposit",
    "iban": "DE945344405320138014",
    "amount": 651
}
```

### Transfer some money across two bank accounts Response

POST /api/transaction
```json
{
    "type": "Deposit",
    "iban": "DE89370400440532013003",
    "amount": 578,
    "to": "DE89370400440532013001"
}
```

### Show current balance of the specific bank account
GET /api/accounts/{iban}

```No body```

### Show a transaction history
GET /api/accounts/{iban}/transactions
```No body```


