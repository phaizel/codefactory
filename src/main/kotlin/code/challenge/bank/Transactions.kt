package code.challenge.bank

import java.math.BigDecimal
import java.time.OffsetDateTime

sealed class TransactionRequest {
    data class Debit(val account: BankAccount, val amount: BigDecimal) : TransactionRequest()
    data class Credit(val account: BankAccount, val amount: BigDecimal) : TransactionRequest()
    data class Transfer(val from: BankAccount, val to: BankAccount, val amount: BigDecimal) : TransactionRequest()
}

sealed class TransactionStatus {
    object Approved : TransactionStatus()
    class Declined(vararg val reasons: String) : TransactionStatus()
    object Executed : TransactionStatus()
}

data class Transaction (
    val request: TransactionRequest,
    val status: TransactionStatus,
    val result: BankAccount,
    val dateTimeStamp: OffsetDateTime = OffsetDateTime.now()
)
