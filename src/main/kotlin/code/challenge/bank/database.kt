package code.challenge.bank

import java.math.BigDecimal
import kotlin.collections.*

import code.challenge.bank.*

object database {

    private val checking1 = BankAccount.Checking("DE89370400440532013000", BigDecimal(353))
    private val checking2 = BankAccount.Checking("DE89370400440532013001", BigDecimal(3451))
    private val checking3 = BankAccount.Checking("DE89370400440532013002", BigDecimal(5067))
    private val checking4 = BankAccount.Checking("DE89370400440532013003", BigDecimal(422))

    private val loan1 = BankAccount.PrivateLoan("DE83453450440532013000", BigDecimal(-459))
    private val loan2 = BankAccount.PrivateLoan("DE89453450440532013091", BigDecimal(-8904))
    private val loan3 = BankAccount.PrivateLoan("DE89453450440532013012", BigDecimal(-6405))
    private val loan4 = BankAccount.PrivateLoan("DE89453450440532013073", BigDecimal(-605))
    private val loan5 = BankAccount.PrivateLoan("DE89453450440532013045", BigDecimal(-4017))

    private val savings1 = BankAccount.Savings("DE945344405320138014",checking1, BigDecimal(374))
    private val savings2 = BankAccount.Savings("DE945345005320138078",checking4, BigDecimal(7312))

    val BANK_ACCOUNTS = mutableSetOf(
        checking1,checking2,checking3,checking4,loan1 ,loan2 , loan3, loan4, loan5,savings1, savings2
    )

    val TRANSACTIONS = mutableSetOf<Transaction>()
}
