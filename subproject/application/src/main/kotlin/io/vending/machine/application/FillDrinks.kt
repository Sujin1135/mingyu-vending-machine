package io.vending.machine.application

import arrow.core.raise.Effect
import arrow.core.raise.effect
import arrow.core.raise.ensure
import io.vending.machine.domain.Drink
import io.vending.machine.repository.DrinkRepository

class FillDrinks(
    private val drinkRepository: DrinkRepository,
) {
    sealed interface Failure {
        data class InvalidDrinkContained(
            val message: String,
        ) : Failure
    }

    operator fun invoke(drinks: List<Drink>): Effect<Failure, Unit> =
        effect {
            validate(drinks).bind()

            drinkRepository.save(drinks).bind()
        }

    private fun validate(drinks: List<Drink>): Effect<Failure, Unit> =
        effect {
            drinks.forEach {
                ensure(it.quantity.value > 0) {
                    raise(Failure.InvalidDrinkContained(message = "the drink(${it.name}) amount can not be negative"))
                }

                ensure(it.price.value > 0) {
                    raise(Failure.InvalidDrinkContained(message = "the drink(${it.name}) price can not be negative"))
                }
            }
        }
}
