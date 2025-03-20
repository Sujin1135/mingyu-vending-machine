package io.vending.machine.application

import arrow.core.raise.effect
import arrow.core.raise.getOrNull
import arrow.core.raise.toEither
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.coEvery
import io.mockk.mockk
import io.vending.machine.domain.Base
import io.vending.machine.domain.CreditCardGenerator
import io.vending.machine.domain.Drink
import io.vending.machine.domain.DrinkGenerator
import io.vending.machine.external.CreditCardApi
import io.vending.machine.repository.DrinkRepository
import io.vending.machine.repository.DrinkRepositoryImpl
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.util.UUID

class BuyDrinkWithCreditCardTests :
    FreeSpec(),
    KoinTest {
    init {
        // mocking external api call
        val creditCardApi = mockk<CreditCardApi>()

        startKoin {
            modules(
                module {
                    single<DrinkRepository> { DrinkRepositoryImpl() }
                    single<FillDrinks> { FillDrinks(drinkRepository = get()) }
                    single<ValidateBuyingAvailable> { ValidateBuyingAvailable(drinkRepository = get()) }
                    single<BuyDrinkWithCreditCard> {
                        BuyDrinkWithCreditCard(
                            drinkRepository = get(),
                            validateBuyingAvailable = get(),
                            creditCardApi = creditCardApi,
                        )
                    }
                },
            )
        }

        val fillDrinks by inject<FillDrinks>()
        val buyDrinkWithCreditCard by inject<BuyDrinkWithCreditCard>()

        val drink = DrinkGenerator.generate().copy(amount = Drink.Amount(500))
        val creditCard = CreditCardGenerator.generate()

        beforeTest {
            fillDrinks(listOf(drink)).getOrNull()
        }

        "should return effect right successfully" - {
            coEvery {
                creditCardApi.pay(creditCard, drink.price.value)
            } returns effect { }

            buyDrinkWithCreditCard(drink.id, creditCard).toEither().shouldBeRight()
        }

        "should raise the failure cause not found a drink" - {
            buyDrinkWithCreditCard(Base.Id(UUID.randomUUID()), creditCard)
                .toEither()
                .shouldBeLeft()
                .shouldBeTypeOf<BuyDrinkWithCreditCard.Failure.NotFound>()
        }

        "should raise the failure cause insufficient quantity of the drink" - {
            val oneDrink = DrinkGenerator.generate().copy(amount = Drink.Amount(1))

            fillDrinks(listOf(oneDrink)).toEither().shouldBeRight()

            coEvery {
                creditCardApi.pay(creditCard, oneDrink.price.value)
            } returns effect { }

            buyDrinkWithCreditCard(oneDrink.id, creditCard).toEither().shouldBeRight()
            buyDrinkWithCreditCard(oneDrink.id, creditCard)
                .toEither()
                .shouldBeLeft()
                .shouldBeTypeOf<BuyDrinkWithCreditCard.Failure.InsufficientQuantity>()
        }

        "should raise the failure cause insufficient balance of the card" - {
            coEvery {
                creditCardApi.pay(creditCard, drink.price.value)
            } returns
                effect {
                    raise(CreditCardApi.PayFailure.InsufficientBalance)
                }

            buyDrinkWithCreditCard(drink.id, creditCard)
                .toEither()
                .shouldBeLeft()
                .shouldBeTypeOf<BuyDrinkWithCreditCard.Failure.InsufficientBalance>()
        }

        "should raise the failure cause invalid card number" - {
            coEvery {
                creditCardApi.pay(creditCard, drink.price.value)
            } returns
                effect {
                    raise(CreditCardApi.PayFailure.InvalidCardNumber)
                }

            buyDrinkWithCreditCard(drink.id, creditCard)
                .toEither()
                .shouldBeLeft()
                .shouldBeTypeOf<BuyDrinkWithCreditCard.Failure.InvalidCardNumber>()
        }
    }
}
