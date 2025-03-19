package io.vending.machine.application

import arrow.core.raise.toEither
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.vending.machine.config.appModule
import io.vending.machine.domain.Drink
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class FillDrinksTests :
    FreeSpec(),
    KoinTest {
    init {
        startKoin { modules(appModule) }

        val fillDrinks by inject<FillDrinks>()

        "should added some drinks successfully" - {
            val drinks = listOf(Drink.of(Drink.Name("coke"), Drink.Price(1100), Drink.Amount(5)))
            fillDrinks(drinks).toEither().shouldBeRight()
        }
    }
}
