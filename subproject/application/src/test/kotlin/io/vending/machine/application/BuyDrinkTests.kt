package io.vending.machine.application

import arrow.core.raise.getOrNull
import arrow.core.raise.toEither
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.Test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.vending.machine.domain.Base
import io.vending.machine.domain.Drink
import io.vending.machine.domain.DrinkGenerator
import io.vending.machine.repository.DrinkRepository
import io.vending.machine.repository.DrinkRepositoryImpl
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.util.UUID

@Test
class BuyDrinkTests :
    FreeSpec(),
    KoinTest {
    init {
        startKoin {
            modules(
                module {
                    single<DrinkRepository> { DrinkRepositoryImpl() }
                    single<FillDrinks> { FillDrinks(drinkRepository = get()) }
                    single<ValidateBuyingAvailable> { ValidateBuyingAvailable(drinkRepository = get()) }
                    single<BuyDrink> { BuyDrink(drinkRepository = get(), validateBuyingAvailable = get()) }
                },
            )
        }

        val buyDrink by inject<BuyDrink>()
        val fillDrinks by inject<FillDrinks>()

        val drink = DrinkGenerator.generate().copy(amount = Drink.Amount(500))

        beforeTest {
            fillDrinks(listOf(drink)).getOrNull()
        }

        "should return change 0 coin when insert coins exact drink price" - {
            val sut = buyDrink(drink.id, drink.price.value).toEither().shouldBeRight()
            sut shouldBe 0
        }

        "should return change coins when insert more coins than drink price" - {
            val coin = drink.price.value + 1000
            val sut = buyDrink(drink.id, coin).toEither().shouldBeRight()
            sut shouldBe coin - drink.price.value
        }

        "should return left value cause not found data" - {
            buyDrink(Base.Id(UUID.randomUUID()), 1000).toEither().shouldBeLeft().shouldBeTypeOf<BuyDrink.Failure.NotFound>()
        }

        "should return left value cause insufficient coin" - {
            buyDrink(drink.id, drink.price.value - 100).toEither().shouldBeLeft().shouldBeTypeOf<BuyDrink.Failure.InsufficientCoin>()
        }

        "should return left value cause insufficient quantity" - {
            val initData = DrinkGenerator.generate().copy(amount = Drink.Amount(1))
            fillDrinks(listOf(initData)).toEither().shouldBeRight()

            buyDrink(initData.id, initData.price.value).toEither().shouldBeRight()
            buyDrink(initData.id, initData.price.value).toEither().shouldBeLeft().shouldBeTypeOf<BuyDrink.Failure.InsufficientQuantity>()
        }
    }
}
