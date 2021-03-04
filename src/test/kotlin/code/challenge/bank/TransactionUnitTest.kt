package code.challenge.bank

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

import code.challenge.bank.transactions.*

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

    @Test
    fun `handling a request to withdraw from an account with a NoWithdrawal attribute`() {
        val loan = BankAccount.PrivateLoan(iban = generateUuid(), balance = BigDecimal(-2300))
        val request = TransactionRequest.Debit(loan, BigDecimal(120))
        val transaction = request.transact().get(0)

        assertEquals(TransactionStatus.WITHDRAWAL_FORBIDDEN, transaction.status)
        assertEquals(request, transaction.request)
        assertEquals(loan, transaction.result)
    }

    @Test
    fun `handling a request to transfer money from one account to another`() {
        val check1 = BankAccount.Checking(iban = generateUuid(), balance = BigDecimal(200))
        val check2 = BankAccount.Checking(iban = generateUuid(), balance = BigDecimal(300))
        val request = TransactionRequest.Transfer(check2, check1, BigDecimal(129))
        val transactions = request.transact()

        val transactionResultForCheck1 = transactions.find { it.result.iban == check1.iban }!!
        assertEquals(TransactionStatus.Approved, transactionResultForCheck1.status)
        assertEquals(request, transactionResultForCheck1.request)

        val expectedBankAccount1 = check1.copy(balance = check1.balance + request.amount)
        assertEquals(expectedBankAccount1, transactionResultForCheck1.result)

        val transactionResultForCheck2 = transactions.find { it.result.iban == check2.iban }!!
        assertEquals(TransactionStatus.Approved, transactionResultForCheck2.status)
        assertEquals(request, transactionResultForCheck2.request)

        val expectedBankAccount2 = check2.copy(balance = check2.balance - request.amount)
        assertEquals(expectedBankAccount2, transactionResultForCheck2.result)
    }

    @Test
    fun `handling a request to transfer money from a savings account to an unlinked account`() {
        val check2 = BankAccount.Checking(iban = generateUuid(), balance = BigDecimal(300))

        val check1 = BankAccount.Checking(iban = generateUuid(), balance = BigDecimal(2300))
        val savings = BankAccount.Savings(iban = generateUuid(), check1, balance = BigDecimal(200))
        val request = TransactionRequest.Transfer(savings, check2, BigDecimal(129))
        val transactions = request.transact()

        assertEquals(1, transactions.size)
        val transaction = request.transact().get(0)

        assertEquals(TransactionStatus.TRANSFER_FORBIDDEN, transaction.status)
        assertEquals(request, transaction.request)
        assertEquals(savings, transaction.result)
    }

    @Test
    fun `handling a request to transfer money from a linked account to the reference account`() {
        val check = BankAccount.Checking(iban = generateUuid(), balance = BigDecimal(200))
        val savings = BankAccount.Savings(iban = generateUuid(), check, balance = BigDecimal(300))
        val request = TransactionRequest.Transfer(savings, check, BigDecimal(200))
        val transactions = request.transact()

        val transactionResultForCheck = transactions.find { it.result.iban == check.iban }!!
        assertEquals(TransactionStatus.Approved, transactionResultForCheck.status)
        assertEquals(request, transactionResultForCheck.request)

        val expectedCheckAccount = check.copy(balance = check.balance + request.amount)
        assertEquals(expectedCheckAccount, transactionResultForCheck.result)

        val transactionResultForSavings = transactions.find { it.result.iban == savings.iban }!!
        assertEquals(TransactionStatus.Approved, transactionResultForSavings.status)
        assertEquals(request, transactionResultForSavings.request)

        val expectedSavingsAccount = savings.copy(balance = savings.balance - request.amount)
        assertEquals(expectedSavingsAccount, transactionResultForSavings.result)
    }

    @Test
    fun `handling a request to transfer money from an unlinked account to a savings account`() {
        val check2 = BankAccount.Checking(iban = generateUuid(), balance = BigDecimal(300))
        val check = BankAccount.Checking(iban = generateUuid(), balance = BigDecimal(200))
        val savings = BankAccount.Savings(iban = generateUuid(), check, balance = BigDecimal(300))
        val request = TransactionRequest.Transfer(check2, savings, BigDecimal(200))
        val transactions = request.transact()

        val transactionResultForSavings = transactions.find { it.result.iban == savings.iban }!!
        assertEquals(TransactionStatus.Approved, transactionResultForSavings.status)
        assertEquals(request, transactionResultForSavings.request)

        val expectedSavingsAccount = savings.copy(balance = savings.balance + request.amount)
        assertEquals(expectedSavingsAccount, transactionResultForSavings.result)

        val transactionResultForChecking2 = transactions.find { it.result.iban == check2.iban }!!
        assertEquals(TransactionStatus.Approved, transactionResultForChecking2.status)
        assertEquals(request, transactionResultForChecking2.request)

        val expectedChecking2Account = check2.copy(balance = check2.balance - request.amount)
        assertEquals(expectedChecking2Account, transactionResultForChecking2.result)
    }
}
