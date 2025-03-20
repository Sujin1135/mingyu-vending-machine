package io.vending.machine.config

import io.vending.machine.application.BuyDrink
import io.vending.machine.application.BuyDrinkWithCreditCard
import io.vending.machine.application.FillDrinks
import io.vending.machine.repository.DrinkRepository
import io.vending.machine.repository.DrinkRepositoryImpl
import org.koin.dsl.module

val appModule =
    module {
        single<DrinkRepository> { DrinkRepositoryImpl() }
        single<FillDrinks> { FillDrinks(drinkRepository = get()) }
        single<BuyDrink> { BuyDrink(drinkRepository = get()) }
        single<BuyDrinkWithCreditCard> { BuyDrinkWithCreditCard(buyDrink = get()) }
    }
