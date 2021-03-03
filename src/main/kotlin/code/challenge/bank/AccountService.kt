package code.challenge.bank

import java.math.BigDecimal
import org.springframework.stereotype.Service
import code.challenge.bank.transactions.*

@Service
class AccountService(private val repo: AccountRepo, private val transactionRepo: TransactionRepo) {
    fun getBalanceForAccount(iban: String): AccountBalanceResponse? =
        repo.findOneByIBAN(iban)?.let(AccountBalanceResponse::fromAccount)

    fun getTxnHistory(iban: String): TransactionHistoryResponse =
        TransactionHistoryResponse(transactionRepo.findAll(iban))
}

@Service
class AccountRepo {
    fun findOneByIBAN(iban: String): BankAccount? = database.BANK_ACCOUNTS.find { it.iban == iban }
    fun update(account: BankAccount): BankAccount {
        database.BANK_ACCOUNTS.removeIf { it.iban == account.iban }
        database.BANK_ACCOUNTS.add(account)
        return account
    }
}


data class AccountBalanceResponse(val iban: String, val balance: BigDecimal) {
    companion object {
        fun fromAccount(account: BankAccount) = AccountBalanceResponse(account.iban, account.balance)
    }
}

data class TransactionHistoryResponse(val transactions: Set<Transaction>)

