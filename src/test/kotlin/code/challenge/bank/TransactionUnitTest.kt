package code.challenge.bank

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TransactionUnitTest {

    @Test
    fun `handling a request for money to be deposited into an account`() {
        val checking = BankAccount.Checking(generateUuid(), BigDecimal(345))
        val request = TransactionRequest.Credit(checking, BigDecimal(120))
        val transaction = request.transact().get(0)

        assertEquals(TransactionStatus.Approved, transaction.status)
        assertEquals(request, transaction.request)

        val expectedBankAccount = checking.copy(balance = checking.balance + request.amount)
        assertEquals(expectedBankAccount, transaction.result)
    }

    @Test
    fun `handling a request to withdraw from an account`() {
        val check = BankAccount.Checking(iban = generateUuid(), balance = BigDecimal(2300))
        val savings = BankAccount.Savings(iban = generateUuid(), check, balance = BigDecimal(200))
        val request = TransactionRequest.Debit(savings, BigDecimal(120))
        val transaction = request.transact().get(0)

        assertEquals(TransactionStatus.Approved, transaction.status)
        assertEquals(request, transaction.request)

        val expectedBankAccount = savings.copy(balance = savings.balance - request.amount)
        assertEquals(expectedBankAccount, transaction.result)
    }
}
