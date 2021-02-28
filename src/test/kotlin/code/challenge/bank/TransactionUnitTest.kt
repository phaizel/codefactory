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
}
