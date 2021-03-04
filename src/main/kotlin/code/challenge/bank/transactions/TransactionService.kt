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

        val transactions = toTxnRequest(txnRq, account).transact().toSet()

        return if (transactionRepo.save(transactions)) {
            transactions.forEach { accountRepo.update(it.result) }
            transactions
        } else {
            null
        }
    }

    private fun toTxnRequest(usrTxnRq: BankTransaction, account: BankAccount) = when (usrTxnRq) {
        is BankTransaction.DepositRequest -> TransactionRequest.Credit(account, usrTxnRq.amount)
        is BankTransaction.WithdrawalRequest -> TransactionRequest.Debit(account, usrTxnRq.amount)
        is BankTransaction.TransferRequest -> {
            val to = usrTxnRq.to
            val toAcc = accountRepo.findOneByIBAN(to)
            if (toAcc != null) TransactionRequest.Transfer(account, toAcc, usrTxnRq.amount)
            else TransactionRequest.Credit(account, BigDecimal.ZERO) // mmmmh? find a better solution
        }
    }
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = BankTransaction.DepositRequest::class, name = "Deposit"),
    JsonSubTypes.Type(value = BankTransaction.WithdrawalRequest::class, name = "Withdrawal"),
    JsonSubTypes.Type(value = BankTransaction.TransferRequest::class, name = "Transfer")
)
sealed class BankTransaction {
    abstract val iban: String
    abstract val amount: BigDecimal
    open val to: String? = null

    data class DepositRequest(override val iban: String, override val amount: BigDecimal ) : BankTransaction()
    data class WithdrawalRequest(override val iban: String, override val amount: BigDecimal ) : BankTransaction()
    data class TransferRequest(override val iban: String, override val amount: BigDecimal, override val to: String) : BankTransaction()
}

@Service
class TransactionRepo {
    fun save(transactions: Collection<Transaction>) = database.TRANSACTIONS.addAll(transactions)
    fun findAll(iban: String) = database.TRANSACTIONS.filter { it.result.iban == iban }.toSet()
}
