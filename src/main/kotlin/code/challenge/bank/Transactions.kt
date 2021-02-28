package code.challenge.bank

import java.math.BigDecimal

sealed class TransactionRequest {
    data class Debit(val account: BankAccount, val amount: BigDecimal) : TransactionRequest()
    data class Credit(val account: BankAccount, val amount: BigDecimal) : TransactionRequest()
    data class Transfer(val from: BankAccount, val to: BankAccount, val amount: BigDecimal) : TransactionRequest()
}
