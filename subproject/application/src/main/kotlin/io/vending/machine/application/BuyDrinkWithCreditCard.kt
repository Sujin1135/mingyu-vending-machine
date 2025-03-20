package io.vending.machine.application

import arrow.core.raise.Effect
import arrow.core.raise.effect
import arrow.core.raise.mapError
import io.vending.machine.domain.Base
import io.vending.machine.domain.CreditCard
import io.vending.machine.external.CreditCardApi
import io.vending.machine.repository.DrinkRepository

class BuyDrinkWithCreditCard(
    private val drinkRepository: DrinkRepository,
    private val validateBuyingAvailable: ValidateBuyingAvailable,
    private val creditCardApi: CreditCardApi,
) {
    sealed interface Failure {
        data object NotFound : Failure

        data object InsufficientQuantity : Failure

        data object InsufficientBalance : Failure

        data object InvalidCardNumber : Failure
    }

    operator fun invoke(
        id: Base.Id,
        creditCard: CreditCard,
    ): Effect<Failure, Unit> =
        effect {
            val drink =
                validateBuyingAvailable(id)
                    .mapError {
                        when (it) {
                            ValidateBuyingAvailable.Failure.NotFound -> Failure.NotFound
                            ValidateBuyingAvailable.Failure.InsufficientQuantity -> Failure.InsufficientQuantity
                        }
                    }.bind()

            creditCardApi
                .pay(creditCard, drink.price.value)
                .mapError {
                    when (it) {
                        CreditCardApi.PayFailure.InvalidCardNumber -> Failure.InvalidCardNumber
                        CreditCardApi.PayFailure.InsufficientBalance -> Failure.InsufficientBalance
                    }
                }.bind()

            drinkRepository.save(drink.buy()).bind()
        }
}
