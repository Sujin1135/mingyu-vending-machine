package io.vending.machine.repository

import arrow.core.raise.Effect
import io.vending.machine.domain.Base
import io.vending.machine.domain.Drink

interface DrinkRepository {
    fun save(drinks: List<Drink>): Effect<Nothing, Unit>

    fun save(drink: Drink): Effect<Nothing, Unit>

    fun retrieveAll(): Effect<Nothing, List<Drink>>

    fun retrieve(id: Base.Id): Effect<Nothing, Drink?>
}
