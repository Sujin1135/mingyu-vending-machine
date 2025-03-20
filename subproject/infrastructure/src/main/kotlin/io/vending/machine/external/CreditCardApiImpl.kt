package io.vending.machine.external

import arrow.core.raise.Effect
import arrow.core.raise.effect
import io.vending.machine.domain.CreditCard

class CreditCardApiImpl : CreditCardApi {
    override fun pay(
        creditCard: CreditCard,
        transactionAmount: Int,
    ): Effect<CreditCardApi.PayFailure, Unit> =
        effect {
            TODO("Not yet implemented")
        }
}
