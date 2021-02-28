package code.challenge.bank

import java.math.BigDecimal
import java.time.OffsetDateTime

sealed class TransactionRequest {
    data class Debit(val account: BankAccount, val amount: BigDecimal) : TransactionRequest()
    data class Credit(val account: BankAccount, val amount: BigDecimal) : TransactionRequest()
    data class Transfer(val from: BankAccount, val to: BankAccount, val amount: BigDecimal) : TransactionRequest()

    // simplistic; we are likely to have speciliazed implementations for each of these transaction requests.
    fun transact(): List<Transaction> = when(this){
        is Credit -> {
            listOf(
                Transaction(this, TransactionStatus.Approved, this.account.updatedBalance(this.account.balance + amount))
            )
        }
        is Debit -> {
            val hasNoWithdrawalAttribute = this.account.attributes.any { it == AccountAttribute.NoWithdrawal }
            if (hasNoWithdrawalAttribute) {
                listOf(Transaction(this, TransactionStatus.WITHDRAWAL_FORBIDDEN, this.account))
            } else {
                listOf(Transaction(this, TransactionStatus.Approved, this.account.updatedBalance(this.account.balance - this.amount)))
            }
        }
        is Transfer -> listOf(
            Transaction(this, TransactionStatus.Approved, this.from.updatedBalance(this.from.balance - this.amount)),
            Transaction(this, TransactionStatus.Approved, this.to.updatedBalance(this.to.balance + amount))
        )
    }
}

sealed class TransactionStatus {
    object Approved : TransactionStatus()
    class Declined(vararg val reasons: String) : TransactionStatus()
    object Executed : TransactionStatus()
    companion object {
        val WITHDRAWAL_FORBIDDEN = Declined("Withdrawal Forbidden")
    }
}

data class Transaction (
    val request: TransactionRequest,
    val status: TransactionStatus,
    val result: BankAccount,
    val dateTimeStamp: OffsetDateTime = OffsetDateTime.now()
)
