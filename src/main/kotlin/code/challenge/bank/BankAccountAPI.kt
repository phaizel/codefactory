package code.challenge.bank

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

import code.challenge.bank.transactions.*

@RestController
@RequestMapping("/api/account")
class BankAccountAPI(private val accountService: AccountService) {

    @GetMapping("/{iban}")
    fun getAccountBalance(@PathVariable iban: String): AccountBalanceResponse =
        accountService.getBalanceForAccount(iban)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "This account does not exist")

}

@RestController
@RequestMapping("/api/transaction")
class TransactionAPI(private val txnService: TransactionService) {

    @PostMapping
    fun transaction(@RequestBody request: BankTransaction) =
        txnService.transact(request)?.let { HttpStatus.OK } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request.")
}

