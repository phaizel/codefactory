package code.challenge.bank

import java.math.BigDecimal
import org.springframework.stereotype.Service

@Service
class AccountService(private val repo: AccountRepo) {
    fun getBalanceForAccount(iban: String): AccountBalanceResponse? =
        repo.findOneByIBAN(iban)?.let(AccountBalanceResponse::fromAccount)
}

@Service
class AccountRepo {
    fun findOneByIBAN(iban: String): BankAccount? = database.BANK_ACCOUNTS.find { it.iban == iban }
}


data class AccountBalanceResponse(val iban: String, val balance: BigDecimal) {
    companion object {
        fun fromAccount(account: BankAccount) = AccountBalanceResponse(account.iban, account.balance)
    }
}

