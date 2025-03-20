package io.vending.machine

import arrow.core.raise.Effect
import arrow.core.raise.effect
import arrow.core.raise.fold
import arrow.core.raise.get
import arrow.core.raise.mapError
import io.vending.machine.application.BuyDrink
import io.vending.machine.application.BuyDrinkWithCreditCard
import io.vending.machine.application.FillDrinks
import io.vending.machine.config.appModule
import io.vending.machine.domain.Base
import io.vending.machine.domain.CreditCard
import io.vending.machine.domain.Drink
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.get
import java.util.UUID

enum class InsertStatus {
    CREDIT_CARD,
    COIN,
    NONE,
}

const val HELP_DESCRIPTION =
    "----- 명령어 목록 -----\n" +
        "    insertCash - 현금 투입\n" +
        "    insertCard - 카드 투입\n" +
        "    cancel - 현금 or 카드 투입 취소\n" +
        "    buy - 음료 구매\n" +
        "    exit - 자판기 종료"

var totalInsertedCoins = 0
var insertedCreditCard: CreditCard? = null
var drinks = mutableListOf<Drink>()
var shouldKeepRun = true

suspend fun main() {
    startKoin {
        modules(appModule)
    }

    drinks = init().get().toMutableList()

    println(HELP_DESCRIPTION)

    while (shouldKeepRun) {
        shouldKeepRun = processCommand(readCommand().get()).get()
    }
}

private fun init(): Effect<Nothing, List<Drink>> =
    effect {
        val drinks =
            listOf(
                Drink.of(Drink.Name("콜라"), Drink.Price(1100), Drink.Quantity(100)),
                Drink.of(Drink.Name("물"), Drink.Price(600), Drink.Quantity(100)),
                Drink.of(Drink.Name("커피"), Drink.Price(700), Drink.Quantity(100)),
            )

        val fillDrinks = get<FillDrinks>(FillDrinks::class.java)

        fillDrinks(drinks)
            .mapError {
                when (it) {
                    is FillDrinks.Failure.InvalidDrinkContained -> throw RuntimeException(it.message)
                }
            }.get()
        drinks
    }

private fun readCommand(): Effect<Nothing, String> =
    effect {
        if (totalInsertedCoins > 0) {
            println("투입된 금액: $totalInsertedCoins")
        } else if (insertedCreditCard != null) {
            println("카드 투입됨")
        } else {
            println("투입된 금액 혹은 카드가 없음")
        }
        print("명령어를 입력하세요: ")
        readLine() ?: ""
    }

private fun processCommand(command: String): Effect<Nothing, Boolean> =
    effect {
        when (command) {
            "help" -> {
                println(HELP_DESCRIPTION)
                true
            }
            "insertCash" -> {
                insertCash().get()
                true
            }
            "insertCard" -> {
                insertCard().get()
                true
            }
            "buy" -> {
                buy().get()
                true
            }
            "cancel" -> {
                totalInsertedCoins = 0
                insertedCreditCard = null
                true
            }
            "exit" -> {
                println("프로그램을 종료합니다.")
                false
            }
            else -> {
                println("알 수 없는 명령어입니다: $command")
                println("'help' 명령어를 사용하여 사용 가능한 명령어를 확인하세요.")
                true
            }
        }
    }

private fun insertCash(): Effect<Nothing, Unit> =
    effect {
        if (checkCurrentInsertStatus(InsertStatus.CREDIT_CARD).bind()) {
            println("카드가 투입되어 있어 현금을 투입할 수 없습니다.")
        } else {
            println("현금 : 100원 / 500원 / 1,000원 / 5,000원 / 10,000원권 사용가능")
            print("투입 금액을 입력하세요: ")
            val input = readLine()
            if (input == null) {
                println("정확한 투입 금액을 입력해야 합니다.")
            } else {
                val insertedCoin = input.toIntOrNull()

                if (insertedCoin == null) {
                    println("입력된 금액 형식이 올바르지 않습니다. 입력된 금액: $insertedCoin")
                } else {
                    totalInsertedCoins += insertedCoin
                }
            }
        }
    }

private fun insertCard(): Effect<Nothing, Unit> =
    effect {
        if (checkCurrentInsertStatus(InsertStatus.COIN).bind()) {
            println("현금이 투입되어 있어 카드를 투입할 수 없습니다.")
        } else {
            print("카드사를 입력해주세요: ")
            val companyName = readLine()
            if (companyName == null) {
                println("정확한 카드사 명칭을 입력하지 않았습니다.")
            } else {
                print("카드번호를 xxxx-xxxx-xxxx-xxxx 형식에 맞게 입력해주세요: ")
                val inputCardNumber = readLine()

                if (inputCardNumber == null) {
                    println("투입 금액은 정수로 입력해야 합니다.")
                } else {
                    val cardNumbers = inputCardNumber.split("-").mapNotNull { it.toIntOrNull() }

                    if (cardNumbers.size != 4) {
                        println("카드번호 형식이 올바르지 않습니다.")
                    } else {
                        insertedCreditCard =
                            CreditCard(
                                CreditCard.CompanyName(companyName),
                                CreditCard.CardNumberPart1(cardNumbers[0]),
                                CreditCard.CardNumberPart2(cardNumbers[1]),
                                CreditCard.CardNumberPart3(cardNumbers[2]),
                                CreditCard.CardNumberPart4(cardNumbers[3]),
                            )
                    }
                }
            }
        }
    }

private fun buy(): Effect<Nothing, Unit> =
    effect {
        if (insertedCreditCard == null && totalInsertedCoins <= 0) {
            println("카드나 현금을 넣어주세요.")
        } else {
            val buyDrink = get<BuyDrink>(BuyDrink::class.java)
            val buyDrinkWithCreditCard = get<BuyDrinkWithCreditCard>(BuyDrinkWithCreditCard::class.java)

            println("-----구매가능 목록-----")
            drinks.forEach {
                println("${it.name.value} / ID: ${it.id.value} / 가격: ${it.price.value}")
            }
            println("--------------------")
            print("구매하실 음료의 ID를 입력하세요: ")
            val inputId = readLine()?.trim()

            if (inputId == null) {
                println("음료 ID가 입력되지 않았습니다.")
            } else {
                val drinkId = Base.Id(UUID.fromString(inputId))

                if (insertedCreditCard != null) {
                    buyDrinkWithCreditCard(drinkId, insertedCreditCard!!).fold(
                        recover = {
                            when (it) {
                                BuyDrinkWithCreditCard.Failure.NotFound -> println("해당 제품은 존재하지 않습니다.")
                                BuyDrinkWithCreditCard.Failure.InsufficientQuantity -> println("수량이 부족합니다.")
                                BuyDrinkWithCreditCard.Failure.InsufficientBalance -> println("카드에 잔액이 부족합니다.")
                                BuyDrinkWithCreditCard.Failure.InvalidCardNumber -> println("유효하지 않은 카드입니다.")
                            }
                        },
                        transform = {
                            println("성공적으로 구매하였습니다.")
                        },
                    )
                } else {
                    buyDrink(drinkId, totalInsertedCoins).fold(
                        recover = {
                            when (it) {
                                BuyDrink.Failure.NotFound -> println("해당 제품은 존재하지 않습니다.")
                                BuyDrink.Failure.InsufficientQuantity -> println("수량이 부족합니다.")
                                BuyDrink.Failure.InsufficientCoin -> println("투입금액이 부족합니다.")
                            }
                        },
                        transform = {
                            totalInsertedCoins = it
                            println("성공적으로 구매하였습니다.")
                        },
                    )
                }
            }
        }
    }

private fun checkCurrentInsertStatus(expectedStatus: InsertStatus): Effect<Nothing, Boolean> =
    effect {
        val status =
            if (insertedCreditCard != null) {
                InsertStatus.CREDIT_CARD
            } else if (totalInsertedCoins > 0) {
                InsertStatus.COIN
            } else {
                InsertStatus.NONE
            }
        expectedStatus == status
    }
