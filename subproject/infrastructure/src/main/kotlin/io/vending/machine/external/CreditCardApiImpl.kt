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
            // TODO: 실제로 결제 로직을 연동하진 않으며 과제 특성상 무조건 성공하도록 되어있음
            println("The payment has been successfully completed.")
        }
}
