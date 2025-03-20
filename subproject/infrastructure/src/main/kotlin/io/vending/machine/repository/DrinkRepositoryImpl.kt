package io.vending.machine.repository

import arrow.core.raise.Effect
import arrow.core.raise.effect
import io.vending.machine.domain.Base
import io.vending.machine.domain.Drink

class DrinkRepositoryImpl : DrinkRepository {
    private val drinkStore = mutableListOf<Drink>()

    override fun save(drinks: List<Drink>): Effect<Nothing, Unit> =
        effect {
            drinkStore.addAll(drinks)
        }

    override fun save(drink: Drink): Effect<Nothing, Unit> =
        effect {
            val result = drinkStore.first { it.id == drink.id }
            drinkStore.remove(result)
            drinkStore.add(drink)
        }

    override fun retrieveAll(): Effect<Nothing, List<Drink>> =
        effect {
            drinkStore.toList()
        }

    override fun retrieve(id: Base.Id): Effect<Nothing, Drink?> =
        effect {
            drinkStore.firstOrNull { it.id == id }
        }
}
