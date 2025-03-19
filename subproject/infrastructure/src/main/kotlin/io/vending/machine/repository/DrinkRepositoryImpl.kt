package io.vending.machine.repository

import arrow.core.raise.Effect
import arrow.core.raise.effect
import io.vending.machine.domain.Drink

class DrinkRepositoryImpl : DrinkRepository {
    private val drinkStore = mutableListOf<Drink>()

    override fun save(drinks: List<Drink>): Effect<Nothing, Unit> =
        effect {
            drinkStore.addAll(drinks)
        }

    override fun retrieveAll(): Effect<Nothing, List<Drink>> =
        effect {
            drinkStore.toList()
        }
}
