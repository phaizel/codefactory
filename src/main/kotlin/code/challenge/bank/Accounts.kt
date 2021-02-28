package code.challenge.bank

import java.math.BigDecimal

sealed class AccountAttribute {
    data class ReferenceAccount(val account: BankAccount) : AccountAttribute()
    object NoWithdrawal : AccountAttribute()
}

sealed class BankAccount {
    abstract val iban: String
    open val balance: BigDecimal = BigDecimal.ZERO
    open val attributes: List<AccountAttribute> = emptyList()
    abstract fun updatedBalance(bal: BigDecimal): BankAccount

    data class Checking(override val iban: String, override val balance: BigDecimal) : BankAccount(){
        override fun updatedBalance(bal: BigDecimal): BankAccount = this.copy(balance = bal)
    }
    data class Savings(override val iban: String, private val referenceAccount: Checking, override val balance: BigDecimal) : BankAccount() {
        override fun updatedBalance(bal: BigDecimal): BankAccount = this.copy(balance = bal)
        override val attributes: List<AccountAttribute>
            get() = listOf(AccountAttribute.ReferenceAccount(referenceAccount))
    }
    data class PrivateLoan(override val iban: String, override val balance: BigDecimal) : BankAccount() {
        override fun updatedBalance(bal: BigDecimal): BankAccount = this.copy(balance = bal)
        override val attributes: List<AccountAttribute>
            get() = listOf(AccountAttribute.NoWithdrawal)
    }
}
