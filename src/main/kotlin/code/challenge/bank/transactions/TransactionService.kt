package code.challenge.bank.transactions

import org.springframework.stereotype.Service
import code.challenge.bank.*
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.math.BigDecimal

@Service
class TransactionService(
    private val accountRepo: AccountRepo,
    private val transactionRepo: TransactionRepo) {

    fun transact(txnRq: BankTransaction): Set<Transaction>? {
        val account = accountRepo.findOneByIBAN(txnRq.iban) ?: return null
        val to =txnRq.to
        val toAcc = if (to != null ) accountRepo.findOneByIBAN(to) else null
        val transactions = BankTransaction.toTxnRequest(txnRq, account, toAcc).transact().toSet()

        return if (transactionRepo.save(transactions)) {
            transactions.forEach { accountRepo.update(it.result) }
            transactions
        } else {
            null
        }
    }
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = BankTransaction.DepositRequest::class, name="Deposit"),
    JsonSubTypes.Type(value = BankTransaction.WithdrawalRequest::class, name="Withdrawal"),
    JsonSubTypes.Type(value = BankTransaction.TransferRequest::class, name="Transfer")
)
sealed class BankTransaction {
    abstract val iban: String
    abstract val amount: BigDecimal
    open val to: String? = null

    data class DepositRequest(override val iban: String, override val amount: BigDecimal ) : BankTransaction()
    data class WithdrawalRequest(override val iban: String, override val amount: BigDecimal ) : BankTransaction()
    data class TransferRequest(override val iban: String, override val amount: BigDecimal, override val to: String) : BankTransaction()

    companion object {
        fun toTxnRequest(usrTxnRq: BankTransaction, account: BankAccount, to: BankAccount?) = when (usrTxnRq) {
            is DepositRequest -> TransactionRequest.Credit(account, usrTxnRq.amount)
            is WithdrawalRequest -> TransactionRequest.Debit(account, usrTxnRq.amount)
            is TransferRequest ->
                if (to != null ) TransactionRequest.Transfer(account, to, usrTxnRq.amount)
                else TransactionRequest.Credit(account, BigDecimal.ZERO) // mmmmh? find a better solution
        }
    }
}

@Service
class TransactionRepo {
    fun save(transactions: Collection<Transaction>) = database.TRANSACTIONS.addAll(transactions)
}
