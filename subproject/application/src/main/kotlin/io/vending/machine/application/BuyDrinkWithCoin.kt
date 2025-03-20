package io.vending.machine.application

import arrow.core.raise.Effect
import arrow.core.raise.effect
import arrow.core.raise.ensure
import arrow.core.raise.mapError
import io.vending.machine.domain.Base
import io.vending.machine.repository.DrinkRepository

class BuyDrinkWithCoin(
    private val drinkRepository: DrinkRepository,
    private val validateBuyingAvailable: ValidateBuyingAvailable,
) {
    sealed interface Failure {
        data object NotFound : Failure

        data object InsufficientQuantity : Failure

        data object InsufficientCoin : Failure
    }

    operator fun invoke(
        id: Base.Id,
        insertedCoin: Int,
    ): Effect<Failure, Int> =
        effect {
            val drink =
                validateBuyingAvailable(id)
                    .mapError {
                        when (it) {
                            ValidateBuyingAvailable.Failure.NotFound -> Failure.NotFound
                            ValidateBuyingAvailable.Failure.InsufficientQuantity -> Failure.InsufficientQuantity
                        }
                    }.bind()

            ensure(drink.checkAvailableBuy(insertedCoin)) {
                raise(Failure.InsufficientCoin)
            }

            drinkRepository.save(drink.buy()).bind()

            insertedCoin - drink.price.value
        }
}
