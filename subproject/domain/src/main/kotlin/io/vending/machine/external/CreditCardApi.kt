package io.vending.machine.external

import arrow.core.raise.Effect
import io.vending.machine.domain.CreditCard

interface CreditCardApi {
    sealed interface PayFailure {
        data object InsufficientBalance : PayFailure

        data object InvalidCardNumber : PayFailure
    }

    fun pay(
        creditCard: CreditCard,
        transactionAmount: Int,
    ): Effect<PayFailure, Unit>
}
