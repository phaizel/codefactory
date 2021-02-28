package code.challenge.bank

import java.math.BigDecimal

sealed class BankAccount {
    abstract val iban: String
    open val balance: BigDecimal = BigDecimal.ZERO
    data class Checking(override val iban: String, override val balance: BigDecimal) : BankAccount()
    data class Savings(override val iban: String, override val balance: BigDecimal) : BankAccount()
    data class PrivateLoan(override val iban: String, override val balance: BigDecimal) : BankAccount()
}
