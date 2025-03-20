package io.vending.machine.application

import arrow.core.raise.Effect
import arrow.core.raise.effect
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import io.vending.machine.domain.Base
import io.vending.machine.repository.DrinkRepository

class BuyDrink(
    private val drinkRepository: DrinkRepository,
) {
    sealed interface Failure {
        data object NotFound : Failure

        data object InsufficientCoin : Failure

        data object InsufficientQuantity : Failure
    }

    operator fun invoke(
        id: Base.Id,
        insertedCoin: Int,
    ): Effect<Failure, Int> =
        effect {
            val drink =
                ensureNotNull(drinkRepository.retrieve(id).bind()) {
                    raise(Failure.NotFound)
                }

            ensure(drink.sufficientQuantity) {
                raise(Failure.InsufficientQuantity)
            }

            ensure(drink.checkAvailableBuy(insertedCoin)) {
                raise(Failure.InsufficientCoin)
            }

            drinkRepository.save(drink.buy()).bind()

            insertedCoin - drink.price.value
        }
}
