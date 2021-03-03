package code.challenge.bank.transactions

import org.springframework.stereotype.Service
import code.challenge.bank.*
import java.math.BigDecimal

@Service
class TransactionService(
    private val accountRepo: AccountRepo,
    private val transactionRepo: TransactionRepo) {

    fun transact(txnRq: UserTransactionRequest): Set<Transaction>? {
        val account = accountRepo.findOneByIBAN(txnRq.iban) ?: return null

        val transactions = UserTransactionRequest.toTxnRequest(txnRq, account).transact().toSet()

        return if (transactionRepo.save(transactions)) {
            transactions.forEach { accountRepo.update(it.result) }
            transactions
        } else {
            null
        }
    }
}

enum class UserTransactionType { Deposit, Withdrawal, Transfer }

data class UserTransactionRequest(val transactionType: UserTransactionType, val iban: String, val amount: BigDecimal){
    companion object {
        fun toTxnRequest(usrTxnRq: UserTransactionRequest, account: BankAccount) = when (usrTxnRq.transactionType) {
            UserTransactionType.Deposit -> TransactionRequest.Credit(account, usrTxnRq.amount)
            UserTransactionType.Withdrawal -> TransactionRequest.Debit(account, usrTxnRq.amount)
            else -> TransactionRequest.Credit(account, BigDecimal.ZERO) // placeholder
        }
    }
}

@Service
class TransactionRepo {
    fun save(transactions: Collection<Transaction>) = database.TRANSACTIONS.addAll(transactions)
}
