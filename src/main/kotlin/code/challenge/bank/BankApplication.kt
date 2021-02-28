package code.challenge.bank

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.UUID

@SpringBootApplication
class BankApplication

fun main(args: Array<String>) {
	runApplication<BankApplication>(*args)
}

fun generateUuid(): String = UUID.randomUUID().toString()
