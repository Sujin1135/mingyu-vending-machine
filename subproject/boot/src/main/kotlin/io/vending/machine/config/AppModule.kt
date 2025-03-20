package io.vending.machine.config

import io.vending.machine.application.BuyDrinkWithCoin
import io.vending.machine.application.BuyDrinkWithCreditCard
import io.vending.machine.application.FillDrinks
import io.vending.machine.application.ValidateBuyingAvailable
import io.vending.machine.external.CreditCardApi
import io.vending.machine.external.CreditCardApiImpl
import io.vending.machine.repository.DrinkRepository
import io.vending.machine.repository.DrinkRepositoryImpl
import org.koin.dsl.module

val appModule =
    module {
        single<DrinkRepository> { DrinkRepositoryImpl() }
        single<FillDrinks> { FillDrinks(drinkRepository = get()) }
        single<ValidateBuyingAvailable> { ValidateBuyingAvailable(drinkRepository = get()) }
        single<CreditCardApi> { CreditCardApiImpl() }
        single<BuyDrinkWithCoin> { BuyDrinkWithCoin(drinkRepository = get(), validateBuyingAvailable = get()) }
        single<BuyDrinkWithCreditCard> {
            BuyDrinkWithCreditCard(
                drinkRepository = get(),
                validateBuyingAvailable = get(),
                creditCardApi = get(),
            )
        }
    }
