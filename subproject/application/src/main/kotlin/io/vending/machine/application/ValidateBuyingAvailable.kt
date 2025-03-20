package io.vending.machine.application

import arrow.core.raise.Effect
import arrow.core.raise.effect
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import io.vending.machine.domain.Base
import io.vending.machine.domain.Drink
import io.vending.machine.repository.DrinkRepository

class ValidateBuyingAvailable(
    private val drinkRepository: DrinkRepository,
) {
    sealed interface Failure {
        data object NotFound : Failure

        data object InsufficientQuantity : Failure
    }

    operator fun invoke(id: Base.Id): Effect<Failure, Drink> =
        effect {
            val drink =
                ensureNotNull(drinkRepository.retrieve(id).bind()) {
                    raise(Failure.NotFound)
                }

            ensure(drink.sufficientQuantity) {
                raise(Failure.InsufficientQuantity)
            }

            drink
        }
}
