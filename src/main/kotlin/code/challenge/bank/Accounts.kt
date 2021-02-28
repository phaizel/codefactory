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

    data class Checking(override val iban: String, override val balance: BigDecimal) : BankAccount()
    data class Savings(override val iban: String, private val referenceAccount: Checking, override val balance: BigDecimal) : BankAccount() {
        override val attributes: List<AccountAttribute>
            get() = listOf(AccountAttribute.ReferenceAccount(referenceAccount))
    }
    data class PrivateLoan(override val iban: String, override val balance: BigDecimal) : BankAccount() {
        override val attributes: List<AccountAttribute>
            get() = listOf(AccountAttribute.NoWithdrawal)
    }
}
