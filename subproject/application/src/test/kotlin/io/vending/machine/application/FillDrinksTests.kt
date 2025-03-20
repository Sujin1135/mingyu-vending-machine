package io.vending.machine.application

import arrow.core.raise.toEither
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.vending.machine.domain.Drink
import io.vending.machine.domain.DrinkGenerator
import io.vending.machine.repository.DrinkRepository
import io.vending.machine.repository.DrinkRepositoryImpl
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

class FillDrinksTests :
    FreeSpec(),
    KoinTest {
    init {
        startKoin {
            modules(
                module {
                    single<DrinkRepository> { DrinkRepositoryImpl() }
                    single<FillDrinks> { FillDrinks(drinkRepository = get()) }
                },
            )
        }

        val fillDrinks by inject<FillDrinks>()

        "should added some drinks successfully" - {
            val drinks = listOf(DrinkGenerator.generate(), DrinkGenerator.generate(), DrinkGenerator.generate())
            fillDrinks(drinks).toEither().shouldBeRight()
        }

        "should raise an error cause input contained a invalid drink data as price" - {
            val invalidDrink = DrinkGenerator.generate().copy(price = Drink.Price(-5))
            val drinks = listOf(DrinkGenerator.generate(), invalidDrink)
            fillDrinks(drinks)
                .toEither()
                .shouldBeLeft()
                .shouldBeTypeOf<FillDrinks.Failure.InvalidDrinkContained>()
                .message shouldBe
                "the drink(${invalidDrink.name}) price can not be negative"
        }

        "should raise an error cause input contained a invalid drink data as amount" - {
            val invalidDrink = DrinkGenerator.generate().copy(amount = Drink.Amount(-5))
            val drinks = listOf(DrinkGenerator.generate(), invalidDrink)
            fillDrinks(drinks)
                .toEither()
                .shouldBeLeft()
                .shouldBeTypeOf<FillDrinks.Failure.InvalidDrinkContained>()
                .message shouldBe
                "the drink(${invalidDrink.name}) amount can not be negative"
        }
    }
}
